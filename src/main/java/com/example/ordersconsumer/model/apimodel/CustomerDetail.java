package com.example.ordersconsumer.model.apimodel;

import lombok.Data;

@Data
public class CustomerDetail {
    private Integer customerId;
    private String name;
    private Boolean active;
}
