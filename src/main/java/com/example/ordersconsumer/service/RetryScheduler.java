package com.example.ordersconsumer.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RetryScheduler {

    private final RetryService retryService;

    public RetryScheduler(RetryService retryService) {
        this.retryService = retryService;
    }

    @Scheduled(fixedDelay = 10000)
    public void scheduleRetry() {
        retryService.retryFailedOrders()
                .doOnError(error -> System.err.println("Error ejecutando retry: " + error.getMessage()))
                .subscribe(); // ¡Importante! Suscribirse al Mono
    }
}