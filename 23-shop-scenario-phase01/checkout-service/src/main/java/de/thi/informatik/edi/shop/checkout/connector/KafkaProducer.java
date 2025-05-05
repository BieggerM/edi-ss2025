package de.thi.informatik.edi.shop.checkout.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.informatik.edi.shop.checkout.connector.dto.*;
import de.thi.informatik.edi.shop.checkout.model.ShoppingOrder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KafkaProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void placeOrder(ShoppingOrder order) {
        ObjectMapper mapper = new ObjectMapper();
        String orderFinishedJson = "";
        try {
            // Convert ShoppingOrderItem to ShoppingOrderItemDto
            List<ShoppingOrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                ShoppingOrderItemDto dto = new ShoppingOrderItemDto();
                dto.setId(item.getId());
                dto.setArticle(item.getArticle());
                dto.setName(item.getName());
                dto.setPrice(item.getPrice());
                dto.setCount(item.getCount());
                return dto;
            }).toList();

            // Convert ShoppingOrder to PlaceOrderDto
            PlaceOrderDto placeOrderDto = new PlaceOrderDto();
            placeOrderDto.setFirstName(order.getFirstName());
            placeOrderDto.setLastName(order.getLastName());
            placeOrderDto.setStreet(order.getStreet());
            placeOrderDto.setZipCode(order.getZipCode());
            placeOrderDto.setCity(order.getCity());
            placeOrderDto.setOrderId(order.getId());
            placeOrderDto.setStatus(order.getStatus().toString());
            placeOrderDto.setItems(itemDtos);

            try {
                orderFinishedJson = mapper.writeValueAsString(placeOrderDto);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String topic = "order-finished";
        kafkaTemplate.send(topic, orderFinishedJson);
}

    public void sendShippingToWarehouse(ShippingDto shippingDto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String shippingJson = objectMapper.writeValueAsString(shippingDto);
            kafkaTemplate.send("shipping-request", shippingJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
