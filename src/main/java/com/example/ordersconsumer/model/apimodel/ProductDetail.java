package com.example.ordersconsumer.model.apimodel;

import lombok.Data;

@Data
public class ProductDetail {
    private Integer productId;
    private String name;
    private String description;
    private Double price;
}