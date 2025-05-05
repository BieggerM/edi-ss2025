package de.thi.informatik.edi.shop.payment.connector.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
public class PlaceOrderDto {
    private List<ShoppingOrderItemDto> items;

    private UUID orderId;
    private String firstName;
    private String lastName;
    private String street;
    private String zipCode;
    private String city;
    private String status;


}

