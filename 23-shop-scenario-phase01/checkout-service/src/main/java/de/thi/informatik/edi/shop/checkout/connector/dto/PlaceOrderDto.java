package de.thi.informatik.edi.shop.checkout.connector.dto;

import de.thi.informatik.edi.shop.checkout.model.ShoppingOrderItem;
import de.thi.informatik.edi.shop.checkout.model.ShoppingOrderStatus;
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
