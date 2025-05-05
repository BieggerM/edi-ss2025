package de.thi.informatik.edi.shop.shopping.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.informatik.edi.shop.shopping.config.MqttService;
import de.thi.informatik.edi.shop.shopping.model.PickingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PickingConsumer {

    private final MqttService mqttService;

    public PickingConsumer(MqttService mqttService) {
        this.mqttService = mqttService;
    }

    @KafkaListener(topics = "picking-request") // Ersetze "your-picking-topic-name" durch den tatsächlichen Topic-Namen
    public void receivePickingMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        PickingEvent pickingEvent = new PickingEvent();
        try {
            pickingEvent = objectMapper.readValue(message, PickingEvent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mqttService.publishPickedQuantity(pickingEvent.getArticleId(), pickingEvent.getPickedQuantity());
    }


}
