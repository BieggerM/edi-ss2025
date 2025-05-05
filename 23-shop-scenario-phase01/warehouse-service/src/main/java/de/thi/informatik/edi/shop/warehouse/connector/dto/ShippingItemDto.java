package de.thi.informatik.edi.shop.warehouse.connector.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ShippingItemDto {
    private UUID id;

    private UUID article;

    private String name;

    private double price;

    private int count;
}