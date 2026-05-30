package com.payment.processor;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    @Column(name = "payment_id", unique = true, nullable = false)
    @JsonProperty("paymentId")
    private String paymentId;
    @Column(name = "user_id", nullable = false)
    @JsonProperty("userId")
    private String userId;
    @Column(nullable = false)
    @JsonProperty("amount")
    private BigDecimal amount;
    @Column(nullable = false)
    @JsonProperty("currency")
    private String currency;
    @Column(nullable = false)
    @JsonProperty("status")
    private String status;
    @Column(name = "created_at")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @Column(name = "processed_at")
    @JsonProperty("processedAt")
    private LocalDateTime processedAt;
    @Column(name = "kafka_partition")
    @JsonProperty("partition")
    private Integer partition;
    @Column(name = "kafka_offset")
    @JsonProperty("offset")
    private Long offset;

    public Payment() {}

    public Payment(String paymentId, String userId, BigDecimal amount, String currency, String status,
                   LocalDateTime processedAt, Integer partition, Long offset) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.processedAt = processedAt;
        this.partition = partition;
        this.offset = offset;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public Integer getPartition() { return partition; }
    public void setPartition(Integer partition) { this.partition = partition; }
    public Long getOffset() { return offset; }
    public void setOffset(Long offset) { this.offset = offset; }
}
