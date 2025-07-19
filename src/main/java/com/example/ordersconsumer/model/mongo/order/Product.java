package com.example.ordersconsumer.model.mongo.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private Integer productId;
    private String name;
    private Double price;
}
