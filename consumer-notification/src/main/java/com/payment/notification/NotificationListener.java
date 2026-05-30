package com.payment.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class NotificationListener {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void sendNotification(String message,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);

            System.out.printf("[Notification] Received | Partition: %d | Offset: %d | User: %s | Payment: %s%n",
                    partition, offset, event.getUserId(), event.getPaymentId());

            Notification notification = new Notification(
                    event.getPaymentId(),
                    event.getUserId(),
                    event.getAmount(),
                    event.getCurrency(),
                    "SENT",
                    partition,
                    offset,
                    Instant.now().toString()
            );

            String notificationKey = "notification:" + event.getUserId() + ":" + event.getPaymentId();
            String userNotificationsKey = "user:notifications:" + event.getUserId();

            redisTemplate.opsForValue().set(notificationKey, notification, Duration.ofSeconds(86400));
            redisTemplate.opsForList().leftPush(userNotificationsKey, notificationKey);
            redisTemplate.opsForList().trim(userNotificationsKey, 0, 49);

            System.out.printf("[Notification] Sent | Partition: %d | Offset: %d | User: %s%n%n",
                    partition, offset, event.getUserId());

        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
