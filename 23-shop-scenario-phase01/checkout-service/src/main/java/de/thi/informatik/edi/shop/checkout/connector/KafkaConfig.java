package de.thi.informatik.edi.shop.checkout.connector;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic shoppingCartAddTopic() {
        return TopicBuilder.name("order-finished")
                .partitions(4)
                .replicas(1)
                .build();
    }
}