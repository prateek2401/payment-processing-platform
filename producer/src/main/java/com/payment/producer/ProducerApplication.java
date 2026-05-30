package com.payment.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableScheduling
@RestController
@RequestMapping("/api/payments")
public class ProducerApplication implements CommandLineRunner {
    private static final List<String> USERS = Arrays.asList("user-101", "user-102", "user-103", "user-104", "user-105");
    private static final List<String> CURRENCIES = Arrays.asList("USD", "EUR", "INR", "GBP");
    private static final AtomicInteger COUNT = new AtomicInteger(0);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${enable.auto.events}")
    private boolean enableAutoEvents;

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Producer connected!");
        System.out.println("Auto events enabled: " + enableAutoEvents);
        System.out.println("API available at http://localhost:8080/api/payments");
        System.out.println();
    }

    @Scheduled(fixedDelay = 2000)
    public void sendAutoPaymentEvent() {
        if (!enableAutoEvents) {
            return;
        }
        String userId = USERS.get((int) (Math.random() * USERS.size()));
        PaymentEvent event = new PaymentEvent(
                UUID.randomUUID().toString(),
                userId,
                Math.round(Math.random() * 1000 * 100.0) / 100.0,
                CURRENCIES.get((int) (Math.random() * CURRENCIES.size())),
                "PENDING",
                java.time.Instant.now().toString()
        );
        sendToKafka(event);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendManualPaymentEvent(@RequestBody PaymentEvent event) {
        if (event.getPaymentId() == null || event.getPaymentId().isEmpty()) {
            event.setPaymentId(UUID.randomUUID().toString());
        }
        if (event.getStatus() == null || event.getStatus().isEmpty()) {
            event.setStatus("PENDING");
        }
        if (event.getTimestamp() == null || event.getTimestamp().isEmpty()) {
            event.setTimestamp(java.time.Instant.now().toString());
        }

        sendToKafka(event);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment event sent to Kafka",
                "data", event
        ));
    }

    private void sendToKafka(PaymentEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.getUserId(), eventJson).whenComplete((result, ex) -> {
                if (ex != null) {
                    System.err.println("Error sending message: " + ex.getMessage());
                } else {
                    int count = COUNT.incrementAndGet();
                    System.out.printf("[%d] Sent payment %s | User: %s | Partition: %d | Offset: %d%n",
                            count,
                            event.getPaymentId(),
                            event.getUserId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception e) {
            System.err.println("Error serializing event: " + e.getMessage());
        }
    }
}
