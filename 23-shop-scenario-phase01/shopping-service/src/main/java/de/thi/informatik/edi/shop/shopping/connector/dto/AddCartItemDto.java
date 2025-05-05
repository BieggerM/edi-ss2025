package de.thi.informatik.edi.shop.shopping.connector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AddCartItemDto {
    private UUID cartId;
    private UUID articleId;
    private String name;
    private double price;
    private int count;
}
