package de.thi.informatik.edi.shop.checkout.connector.dto;

import lombok.Data;

import java.util.UUID;

@Data
/**
 * Data Transfer Object for shipping items.
 * This class is used to transfer data related to shipping items between different layers of the application.
 */
public class ShippingItemDto {
    private UUID id;

    private UUID article;

    private String name;

    private double price;

    private int count;
}
