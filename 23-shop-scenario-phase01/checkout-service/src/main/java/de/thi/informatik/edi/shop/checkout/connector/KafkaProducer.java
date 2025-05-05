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

    }

    public void sendShippingToWarehouse(ShippingDto shippingDto) {
        try {
            String shippingJson = objectMapper.writeValueAsString(shippingDto);
            kafkaTemplate.send("shipping-service", shippingJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
