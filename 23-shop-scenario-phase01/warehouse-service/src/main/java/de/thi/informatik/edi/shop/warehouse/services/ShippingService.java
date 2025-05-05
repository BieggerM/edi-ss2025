package de.thi.informatik.edi.shop.warehouse.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.informatik.edi.shop.warehouse.connector.dto.KafkaProducer;
import de.thi.informatik.edi.shop.warehouse.connector.dto.PickingEvent;
import de.thi.informatik.edi.shop.warehouse.connector.dto.ShippingDto;
import de.thi.informatik.edi.shop.warehouse.connector.dto.ShippingItemDto;
import de.thi.informatik.edi.shop.warehouse.model.ShippingItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import de.thi.informatik.edi.shop.warehouse.model.Shipping;
import de.thi.informatik.edi.shop.warehouse.repositories.ShippingRepository;

@Service
public class ShippingService {

	private final KafkaProducer kafkaProducer;
	private ShippingRepository repository;

	public ShippingService(@Autowired ShippingRepository repository, KafkaProducer kafkaProducer) {
		this.repository = repository;
		this.kafkaProducer = kafkaProducer;
	}

	@KafkaListener(topics = "shipping-request", groupId = "warehouse")
	public void receiveShippingRequest(String json) {
		ObjectMapper mapper = new ObjectMapper();
		ShippingDto shippingdto = new ShippingDto();
		try {
			shippingdto = mapper.readValue(json, ShippingDto.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Shipping shipping = getOrCreateByOrderRef(shippingdto.getOrderRef());

		shipping.update(shippingdto.getOrderRef(), shippingdto.getFirstName(), shippingdto.getLastName(),
				shippingdto.getStreet(), shippingdto.getZipCode(), shippingdto.getCity());

		List<ShippingItemDto> itemsDto = shippingdto.getItems();
		for (ShippingItemDto itemDto : itemsDto) {
			ShippingItem item = new ShippingItem(itemDto.getArticle(), itemDto.getCount());

			PickingEvent pickingEvent = new PickingEvent();
			pickingEvent.setArticleId(itemDto.getArticle().toString());
			pickingEvent.setPickedQuantity(itemDto.getCount());
			kafkaProducer.pickingRequest(pickingEvent);
			shipping.addArticle(item.getArticle(), item.getCount());
		}
		this.repository.save(shipping);

	}

	public Shipping updateFromOrder(UUID orderRef, String firstName, String lastName, String street, String zipCode,
			String city) {
		Shipping shipping = getOrCreateByOrderRef(orderRef);
		shipping.update(orderRef, firstName, lastName, street, zipCode, city);
		this.repository.save(shipping);
		return shipping;
	}

	private Shipping getOrCreateByOrderRef(UUID orderRef) {
		Optional<Shipping> optional = this.repository.findByOrderRef(orderRef);
		if(optional.isEmpty()) {			
			return new Shipping();
		} else {
			return optional.get();
		}
	}
	
	public void addArticlesByOrderRef(UUID orderRef, Consumer<Shipping> addToShipping) {
		Shipping shipping = this.getOrCreateByOrderRef(orderRef);
		addToShipping.accept(shipping);
		this.repository.save(shipping);
	}
	
	public void addArticleByOrderRef(UUID orderRef, UUID article, int count) {
		Shipping shipping = this.getOrCreateByOrderRef(orderRef);
		shipping.addArticle(article, count);
		this.repository.save(shipping);
	}

	public Iterable<Shipping> getShippings() {
		return this.repository.findAll();
	}

	public void doShipping(UUID id) {
		Optional<Shipping> optional = this.repository.findById(id);
		if(optional.isPresent()) {
			Shipping shipping = optional.get();
			shipping.doShipping();
			this.repository.save(shipping);
		} else {
			throw new IllegalArgumentException("Unknown shipping for ID " + id);
		}
	}

}
