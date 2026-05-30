package com.payment.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnalyticsRepository extends JpaRepository<UserAnalytics, Long> {
    Optional<UserAnalytics> findByDateAndUserIdAndCurrency(LocalDate date, String userId, String currency);
    List<UserAnalytics> findAll();
}
