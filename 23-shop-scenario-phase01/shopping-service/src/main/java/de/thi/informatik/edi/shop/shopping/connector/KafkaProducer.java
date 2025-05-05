package de.thi.informatik.edi.shop.shopping.connector;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.informatik.edi.shop.shopping.connector.dto.AddCartItemDto;
import de.thi.informatik.edi.shop.shopping.connector.dto.CartEvent;
import de.thi.informatik.edi.shop.shopping.connector.dto.DeleteCartItemDto;
import de.thi.informatik.edi.shop.shopping.model.Cart;
import de.thi.informatik.edi.shop.shopping.model.CartEntry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class KafkaProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void addCartItem(UUID cartID, CartEntry cartEntry) {
        AddCartItemDto cartEvent = new AddCartItemDto(cartID, cartEntry.getId(), cartEntry.getName(), cartEntry.getPrice(), cartEntry.getCount());

        ObjectMapper mapper = new ObjectMapper();
        String cartEventJson = "";
        try {
            cartEventJson = mapper.writeValueAsString(cartEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        kafkaTemplate.send("add-to-cart", cartEventJson);
    }

    public void deleteCartItem(UUID cartID, CartEntry entry) {
        DeleteCartItemDto cartEvent = new DeleteCartItemDto(cartID, entry);
        ObjectMapper mapper = new ObjectMapper();
        String cartEventJson = "";
        try {
            cartEventJson = mapper.writeValueAsString(cartEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        kafkaTemplate.send("delete-from-cart", cartEventJson);
    }

    public void cartFinished(UUID id) {
        String jsonPayload = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("cartID", id); // UUID will be serialized as String by default

            // Serialize the Map to a JSON String
            jsonPayload = mapper.writeValueAsString(dataMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send("cart-finished", jsonPayload);
    }
}
