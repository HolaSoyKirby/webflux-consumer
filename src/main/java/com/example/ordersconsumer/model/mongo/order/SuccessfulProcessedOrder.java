package com.example.ordersconsumer.model.mongo.order;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "orders")
public class SuccessfulProcessedOrder {

    @Id
    private String id;  // Mongo ObjectId
    private Integer orderId;
    private Integer customerId;
    private List<Product> products;
}
