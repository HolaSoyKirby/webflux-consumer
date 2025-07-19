package com.example.ordersconsumer.service;

import com.example.ordersconsumer.model.mongo.order.SuccessfulProcessedOrder;
import com.example.ordersconsumer.repository.SuccessfulProcessedOrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final SuccessfulProcessedOrderRepository successfulProcessedOrderRepository;

    public OrderService(
            SuccessfulProcessedOrderRepository successfulProcessedOrderRepository
    ) {
        this.successfulProcessedOrderRepository = successfulProcessedOrderRepository;
    }

    public Mono<SuccessfulProcessedOrder> saveOrder(SuccessfulProcessedOrder successfulProcessedOrder) {
        return successfulProcessedOrderRepository.save(successfulProcessedOrder);
    }
}
