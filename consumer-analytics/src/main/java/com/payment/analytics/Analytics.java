package com.payment.analytics;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "analytics")
public class Analytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;
    @Column(nullable = false)
    @JsonProperty("date")
    private LocalDate date;
    @Column(name = "total_transactions")
    @JsonProperty("totalTransactions")
    private Integer totalTransactions;
    @Column(name = "total_amount")
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    @Column(nullable = false)
    @JsonProperty("currency")
    private String currency;

    public Analytics() {}

    public Analytics(LocalDate date, Integer totalTransactions, BigDecimal totalAmount, String currency) {
        this.date = date;
        this.totalTransactions = totalTransactions;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(Integer totalTransactions) { this.totalTransactions = totalTransactions; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
