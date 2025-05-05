package de.thi.informatik.edi.shop.warehouse.connector.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class PickingEvent {
    private String articleId;
    private int pickedQuantity;
}