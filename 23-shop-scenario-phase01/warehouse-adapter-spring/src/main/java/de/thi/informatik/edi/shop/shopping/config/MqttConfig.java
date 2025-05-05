package de.thi.informatik.edi.shop.shopping.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() throws MqttException {
        String brokerUrl = "tcp://localhost:1883";
        String clientId = "warehouse-adapter"; // Wähle eine eindeutige Client-ID
        MqttClient client = new MqttClient(brokerUrl, clientId);
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        client.connect(connectOptions);
        return client;
    }
}