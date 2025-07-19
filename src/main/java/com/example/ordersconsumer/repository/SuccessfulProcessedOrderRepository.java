package com.example.ordersconsumer.repository;

import com.example.ordersconsumer.model.mongo.order.SuccessfulProcessedOrder;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuccessfulProcessedOrderRepository extends ReactiveMongoRepository<SuccessfulProcessedOrder, String> {
}
