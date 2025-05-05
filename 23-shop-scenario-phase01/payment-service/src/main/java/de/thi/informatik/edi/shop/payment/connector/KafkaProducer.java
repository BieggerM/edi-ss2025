package de.thi.informatik.edi.shop.payment.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thi.informatik.edi.shop.payment.connector.dto.PaymentResponseDto;
import de.thi.informatik.edi.shop.payment.model.Payment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void finishedPayment(Payment payment) {
        ObjectMapper objectMapper = new ObjectMapper();
        String paymentResponseJson = "";
        try {
            PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
            paymentResponseDto = paymentResponseDto.fromPayment(payment);
            paymentResponseJson = objectMapper.writeValueAsString(paymentResponseDto);
        } catch (Exception e) {
            e.printStackTrace();



        }
        String topic = "payment-finished";
        kafkaTemplate.send(topic, paymentResponseJson);

    }
}
