package de.thi.informatik.edi.shop.warehouse.connector.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private KafkaTemplate<String, String> kafkaTemplate;


    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void pickingRequest(PickingEvent pickingEvent) {
        String topic = "picking-request";
        String pickingEventJson = "";
        try {
            pickingEventJson = new ObjectMapper().writeValueAsString(pickingEvent);
        } catch (Exception e) {
            e.printStackTrace();

        }
        kafkaTemplate.send(topic, pickingEventJson);
    }


}
