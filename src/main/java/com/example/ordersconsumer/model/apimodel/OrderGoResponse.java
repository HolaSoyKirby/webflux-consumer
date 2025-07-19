package com.example.ordersconsumer.model.apimodel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderGoResponse {
    private Integer orderId;
    private CustomerDetail customer;
    private List<ProductDetail> products;
    private List<String> errors;

    public boolean isValidOrder() {
        return errors == null || errors.isEmpty();
    }
}
