package de.thi.informatik.edi.shop.checkout.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.informatik.edi.shop.checkout.connector.KafkaProducer;
import de.thi.informatik.edi.shop.checkout.connector.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import de.thi.informatik.edi.shop.checkout.model.ShoppingOrder;
import de.thi.informatik.edi.shop.checkout.model.ShoppingOrderStatus;
import de.thi.informatik.edi.shop.checkout.repositories.ShoppingOrderRepository;
import jakarta.annotation.PostConstruct;

@Service
public class ShoppingOrderService {
	private ShoppingOrderRepository orders;
	private KafkaProducer kafkaProducer;

	public ShoppingOrderService(@Autowired ShoppingOrderRepository orders, @Autowired KafkaProducer kafkaProducer) {
		this.orders = orders;
		this.kafkaProducer = kafkaProducer;
	}
	
	@PostConstruct
	private void init() {
	}

	@KafkaListener(groupId = "checkout-service", topics = "add-to-cart")
	private void receiveAddShoppingCard(String dtoJson) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		AddCartItemDto dto = objectMapper.readValue(dtoJson, AddCartItemDto.class);

		addItemToOrderByCartRef(
				dto.getCartId(),
				dto.getArticleId(),
				dto.getName(),
				dto.getPrice(),
				dto.getCount()
		);

	}

	@KafkaListener(groupId = "checkout-service", topics = "delete-from-cart")
	private void receiveRemoveItemFromShoppingCard(String dtoJson) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			Map<String, String> dataMap = objectMapper.readValue(dtoJson, Map.class);
			UUID cartId = UUID.fromString(dataMap.get("cartID"));
			UUID articleId = UUID.fromString(dataMap.get("articleId"));
			deleteItemFromOrderByCartRef(cartId,articleId);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	@KafkaListener(groupId = "checkout-service", topics = "cart-finished")
	public void receiveCartFinished(String dtoJson)  {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Map<String, String> dataMap = objectMapper.readValue(dtoJson, Map.class);
			UUID cartId = UUID.fromString(dataMap.get("cartID"));
			createOrderWithCartRef(cartId);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@KafkaListener(groupId = "payment-service", topics = "payment-finished")
	public void receivePaymentFinished(String dtoJson) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			PaymentResponseDto paymentResponseDto = objectMapper.readValue(dtoJson, PaymentResponseDto.class);
			UUID orderId = paymentResponseDto.getOrderRef();
			updateOrderIsPayed(orderId);

			// Retrieve the shopping order
			ShoppingOrder shoppingOrder = orders.findById(orderId).orElseThrow(() ->
					new IllegalArgumentException("Order not found with ID: " + orderId));

			// Create ShippingDto
			ShippingDto shippingDto = new ShippingDto();
			shippingDto.setId(UUID.randomUUID());
			shippingDto.setOrderRef(orderId);
			shippingDto.setFirstName(shoppingOrder.getFirstName());
			shippingDto.setLastName(shoppingOrder.getLastName());
			shippingDto.setStreet(shoppingOrder.getStreet());
			shippingDto.setZipCode(shoppingOrder.getZipCode());
			shippingDto.setCity(shoppingOrder.getCity());
			shippingDto.setStatus(ShippingStatusDto.CREATED);

			// Map ShoppingOrder items to ShippingItemDto
			List<ShippingItemDto> shippingItems = shoppingOrder.getItems().stream().map(item -> {
				ShippingItemDto shippingItemDto = new ShippingItemDto();
				shippingItemDto.setId(item.getId());
				shippingItemDto.setArticle(item.getArticle());
				shippingItemDto.setName(item.getName());
				shippingItemDto.setPrice(item.getPrice());
				shippingItemDto.setCount(item.getCount());
				return shippingItemDto;
			}).toList();
			shippingDto.setItems(shippingItems);

			// Send ShippingDto to warehouse service
			kafkaProducer.sendShippingToWarehouse(shippingDto);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void addItemToOrderByCartRef(UUID cartRef, UUID article, String name, double price, int count) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		order.addItem(article, name, price, count);
		this.orders.save(order);
		
	}
	
	private Optional<ShoppingOrder> findByCartRef(UUID cartRef) {
		return this.orders.findByCartRef(cartRef);
	}

	public void deleteItemFromOrderByCartRef(UUID cartRef, UUID article) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		order.removeItem(article);
		this.orders.save(order);
	}

	public ShoppingOrder createOrderWithCartRef(UUID cartRef) {
		ShoppingOrder order = this.getOrCreate(cartRef);
		this.orders.save(order);
		return order;
	}

	private ShoppingOrder getOrCreate(UUID cartRef) {
		Optional<ShoppingOrder> orderOption = this.findByCartRef(cartRef);
		ShoppingOrder order;
		if(orderOption.isEmpty()) {
			order = new ShoppingOrder(cartRef);
		} else {
			order = orderOption.get();
		}
		return order;
	}

	public void updateOrder(UUID id, String firstName, String lastName, String street, String zipCode,
			String city) {
		ShoppingOrder order = findById(id);
		order.update(firstName, lastName, street, zipCode, city);
		this.orders.save(order);
	}

	private ShoppingOrder findById(UUID id) {
		Optional<ShoppingOrder> optional = this.orders.findById(id);
		if(optional.isEmpty()) {
			throw new IllegalArgumentException("Unknown order with id " + id.toString());
		}
		ShoppingOrder order = optional.get();
		return order;
	}

	public void placeOrder(UUID id) {
		ShoppingOrder order = findById(id);
		if(order.getStatus().equals(ShoppingOrderStatus.CREATED)) {
			order.setStatus(ShoppingOrderStatus.PLACED);
			this.orders.save(order);
			kafkaProducer.placeOrder(order);
		}
	}

	public ShoppingOrder find(UUID id) {
		return this.findById(id);
	}

	public void updateOrderIsPayed(UUID id) {
		ShoppingOrder order = this.findById(id);
		if(order.getStatus() == ShoppingOrderStatus.PLACED) {
			order.setStatus(ShoppingOrderStatus.PAYED);
			this.orders.save(order);
		}
	}

	public void updateOrderIsShipped(UUID id) {
		ShoppingOrder order = this.findById(id);
		if(order.getStatus() == ShoppingOrderStatus.PAYED) {
			order.setStatus(ShoppingOrderStatus.SHIPPED);
			this.orders.save(order);
		}
	}
}
