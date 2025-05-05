package de.thi.informatik.edi.shop.checkout.connector.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ShippingDto {
    private UUID id;
    private List<ShippingItemDto> items;
    private String firstName;
    private String lastName;
    private String street;
    private String zipCode;
    private String city;
    private UUID orderRef;
    private ShippingStatusDto status;
}

