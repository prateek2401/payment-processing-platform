package com.payment.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@EnableScheduling
public class AnalyticsListener {
    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private UserAnalyticsRepository userAnalyticsRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void updateAnalytics(String message,
                                @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);

            System.out.printf("[Analytics] Received | Partition: %d | Offset: %d | User: %s | Amount: %.2f %s%n",
                    partition, offset, event.getUserId(), event.getAmount(), event.getCurrency());

            LocalDate today = LocalDate.now();
            
            // Update overall analytics
            Analytics analytics = analyticsRepository.findByDateAndCurrency(today, event.getCurrency())
                    .orElse(new Analytics(today, 0, BigDecimal.ZERO, event.getCurrency()));
            analytics.setTotalTransactions(analytics.getTotalTransactions() + 1);
            analytics.setTotalAmount(analytics.getTotalAmount().add(BigDecimal.valueOf(event.getAmount())));
            analyticsRepository.save(analytics);

            // Update user-specific analytics
            UserAnalytics userAnalytics = userAnalyticsRepository.findByDateAndUserIdAndCurrency(today, event.getUserId(), event.getCurrency())
                    .orElse(new UserAnalytics(today, event.getUserId(), 0, BigDecimal.ZERO, event.getCurrency()));
            userAnalytics.setTotalTransactions(userAnalytics.getTotalTransactions() + 1);
            userAnalytics.setTotalAmount(userAnalytics.getTotalAmount().add(BigDecimal.valueOf(event.getAmount())));
            userAnalyticsRepository.save(userAnalytics);

            System.out.printf("[Analytics] Updated | Partition: %d | Offset: %d%n%n",
                    partition, offset);

        } catch (Exception e) {
            System.err.println("Error updating analytics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void printAnalytics() {
        List<Analytics> analyticsList = analyticsRepository.findAllOrderByDateDesc();
        if (!analyticsList.isEmpty()) {
            System.out.println("=== Current Analytics ===");
            for (Analytics a : analyticsList) {
                System.out.printf("Date: %s | Currency: %s | Tx: %d | Amount: %.2f%n",
                        a.getDate(), a.getCurrency(), a.getTotalTransactions(), a.getTotalAmount());
            }
            System.out.println("=========================\n");
        }
    }
}
