package de.thi.informatik.edi.shop.checkout.connector.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
public class AddCartItemDto {
    private UUID cartId;
    private UUID articleId;
    private String name;
    private double price;
    private int count;

    public AddCartItemDto(UUID cartId, UUID articleId, String name, double price, int count) {
        this.cartId = cartId;
        this.articleId = articleId;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public void setArticleId(UUID articleId) {
        this.articleId = articleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
    }

    public UUID getCartId() {
        return cartId;
    }
}
