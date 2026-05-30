package com.payment.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private UserAnalyticsRepository userAnalyticsRepository;

    @GetMapping
    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAllOrderByDateDesc();
    }

    @GetMapping("/users")
    public List<UserAnalytics> getAllUserAnalytics() {
        return userAnalyticsRepository.findAll();
    }
}
