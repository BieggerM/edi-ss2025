package de.thi.informatik.edi.shop.payment.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.informatik.edi.shop.payment.connector.KafkaProducer;
import de.thi.informatik.edi.shop.payment.connector.dto.PlaceOrderDto;
import de.thi.informatik.edi.shop.payment.connector.dto.ShoppingOrderItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import de.thi.informatik.edi.shop.payment.model.Payment;
import de.thi.informatik.edi.shop.payment.model.PaymentStatus;
import de.thi.informatik.edi.shop.payment.repositories.PaymentRepository;

@Service
public class PaymentService {

	private PaymentRepository payments;

	public PaymentService(@Autowired PaymentRepository payments, @Autowired KafkaProducer kafkaProducer) {
		this.payments = payments;
		this.kafkaProducer = kafkaProducer;
	}
	public KafkaProducer kafkaProducer;


	@KafkaListener(groupId = "payment-service", topics = "order-finished")
	public void receivePlacedOrder(String json) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			PlaceOrderDto placeOrderDto = objectMapper.readValue(json, PlaceOrderDto.class);
			List<ShoppingOrderItemDto> items = placeOrderDto.getItems();
			double summedPrice = 0;
			for (ShoppingOrderItemDto item : items) {
				summedPrice += item.getPrice() * item.getCount();
			}
			Payment payment = getOrCreateByOrderRef(placeOrderDto.getOrderId());
			payment.update(placeOrderDto.getFirstName(), placeOrderDto.getLastName(), placeOrderDto.getStreet(),
					placeOrderDto.getZipCode(), placeOrderDto.getCity());
			payment.setPrice(summedPrice);
			this.payments.save(payment);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public Payment findById(UUID id) {
		Optional<Payment> optional = this.payments.findById(id);
		if (optional.isEmpty()) {
			throw new IllegalArgumentException("Unknown payment with ID " + id.toString());
		}
		return optional.get();
	}

	public Payment getOrCreateByOrderRef(UUID orderRef) {
		Optional<Payment> optional = this.payments.findByOrderRef(orderRef);
		Payment payment;
		if (optional.isEmpty()) {
			payment = new Payment(orderRef);
		} else {
			payment = optional.get();
		}
		this.payments.save(payment);
		return payment;
	}

	public void pay(UUID id) {
		Payment payment = this.findById(id);
		PaymentStatus before = payment.getStatus();
		payment.pay();
		this.payments.save(payment);
		kafkaProducer.finishedPayment(payment);
	}

	public void updatePrice(UUID id, double price) {
		Payment payment = this.findById(id);
		PaymentStatus before = payment.getStatus();
		payment.setPrice(price);
		this.payments.save(payment);
	}

	public void updateData(UUID id, String firstName, String lastName, String street, String zipCode, String city) {
		Payment payment = this.findById(id);
		payment.update(firstName, lastName, street, zipCode, city);
		this.payments.save(payment);
	}

}
