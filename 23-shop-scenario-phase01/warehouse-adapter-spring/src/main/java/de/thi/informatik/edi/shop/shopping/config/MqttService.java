package de.thi.informatik.edi.shop.shopping.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    private final MqttClient mqttClient;

    public MqttService(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public void publishPickedQuantity(String articleId, int quantity) {
        String topic = String.format("article/%s/picked", articleId);
        String payload = String.valueOf(quantity);
        MqttMessage message = new MqttMessage(payload.getBytes());
        try {
            mqttClient.publish(topic, message);
            System.out.println("Published to MQTT topic: " + topic + ", message: " + payload);
        } catch (MqttException e) {
            System.err.println("Error publishing to MQTT: " + e.getMessage());
        }
    }

    public void publishPickingEvent(String articleId, int count) {
        String topic = String.format("shipping/%s", articleId);
        String payload = String.valueOf(count);
        MqttMessage message = new MqttMessage(payload.getBytes());
        try {
            mqttClient.publish(topic, message);
            System.out.println("Published to MQTT topic: " + topic + ", message: " + payload);
        } catch (MqttException e) {
            System.err.println("Error publishing to MQTT: " + e.getMessage());
        }
    }
}
