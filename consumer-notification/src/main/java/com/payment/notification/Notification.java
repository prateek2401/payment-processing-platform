package com.payment.notification;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Notification {
    @JsonProperty("paymentId")
    private String paymentId;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("status")
    private String status;
    @JsonProperty("partition")
    private int partition;
    @JsonProperty("offset")
    private long offset;
    @JsonProperty("timestamp")
    private String timestamp;

    public Notification() {}

    public Notification(String paymentId, String userId, double amount, String currency, String status,
                        int partition, long offset, String timestamp) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getPartition() { return partition; }
    public void setPartition(int partition) { this.partition = partition; }
    public long getOffset() { return offset; }
    public void setOffset(long offset) { this.offset = offset; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
