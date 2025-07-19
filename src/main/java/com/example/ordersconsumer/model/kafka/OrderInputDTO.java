package com.example.ordersconsumer.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderInputDTO {
    private Integer orderId;
    private Integer customerId;
    private List<Integer> productIds;
}
