package de.thi.informatik.edi.shop.payment.connector.dto;

import de.thi.informatik.edi.shop.payment.controller.dto.PaymentResponse;
import de.thi.informatik.edi.shop.payment.model.Payment;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentResponseDto {
	private UUID id;
	private UUID orderRef;
	private String zipCode;
	private String city;
	private String street;
	private String lastName;
	private String firstName;
	private String status;
	private double price;
	
	public PaymentResponseDto() {
	}

    public PaymentResponseDto fromPayment(Payment payment) {
		PaymentResponseDto response = new PaymentResponseDto();
		response.id = payment.getId();
		response.orderRef = payment.getOrderRef();
		response.firstName = payment.getFirstName();
		response.lastName = payment.getLastName();
		response.street = payment.getStreet();
		response.city = payment.getCity();
		response.zipCode = payment.getZipCode();
		response.price = payment.getPrice();
		response.status = payment.getStatus().name();
		return response;
	}
}
