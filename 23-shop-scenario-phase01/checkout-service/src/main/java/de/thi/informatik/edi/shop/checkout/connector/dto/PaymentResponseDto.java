package de.thi.informatik.edi.shop.checkout.connector.dto;

import lombok.Data;

import java.util.UUID;

@Data
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
}
