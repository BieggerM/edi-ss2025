package de.thi.informatik.edi.shop.shopping.model;

import lombok.Data;

@Data
public class PickingEvent {
    private String articleId;
    private int pickedQuantity;
}
