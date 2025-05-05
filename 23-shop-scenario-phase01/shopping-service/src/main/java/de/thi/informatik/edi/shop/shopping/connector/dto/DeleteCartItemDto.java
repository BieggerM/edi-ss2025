package de.thi.informatik.edi.shop.shopping.connector.dto;

import de.thi.informatik.edi.shop.shopping.model.CartEntry;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteCartItemDto
{
    public DeleteCartItemDto(UUID cartId, CartEntry cartEntry) {
        this.cartId = cartId;
        this .articleId = cartEntry.getId();
        this.name = cartEntry.getName();
        this.price = cartEntry.getPrice();
        this.count = cartEntry.getCount();
    }
    private UUID cartId;
    private UUID articleId;
    private String name;
    private double price;
    private int count;

}
