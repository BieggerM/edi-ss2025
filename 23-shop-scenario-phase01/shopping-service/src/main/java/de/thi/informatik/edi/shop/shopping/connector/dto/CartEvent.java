package de.thi.informatik.edi.shop.shopping.connector.dto;

import java.util.UUID;

public record CartEvent(
        UUID cartId,
        UUID articleId,
        String name,
        double price,
        int count

) {

}



