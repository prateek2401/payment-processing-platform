package com.payment.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class PaymentProcessorListener {
    @Autowired
    private PaymentRepository paymentRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void processPayment(String message,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);

            System.out.printf("[Processor] Received | Partition: %d | Offset: %d | User: %s | Payment: %s%n",
                    partition, offset, event.getUserId(), event.getPaymentId());

            if (!paymentRepository.existsByPaymentId(event.getPaymentId())) {
                Payment payment = new Payment(
                        event.getPaymentId(),
                        event.getUserId(),
                        BigDecimal.valueOf(event.getAmount()),
                        event.getCurrency(),
                        "COMPLETED",
                        LocalDateTime.now(),
                        partition,
                        offset
                );
                paymentRepository.save(payment);
            }

            System.out.printf("[Processor] Processed | Partition: %d | Offset: %d | Status: COMPLETED%n%n",
                    partition, offset);

        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
