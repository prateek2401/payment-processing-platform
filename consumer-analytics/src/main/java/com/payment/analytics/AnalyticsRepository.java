package com.payment.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    Optional<Analytics> findByDateAndCurrency(LocalDate date, String currency);

    @Query("SELECT a FROM Analytics a ORDER BY a.date DESC, a.currency")
    List<Analytics> findAllOrderByDateDesc();
}
