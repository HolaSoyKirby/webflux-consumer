package com.example.ordersconsumer.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedOrderDTO {
    private OrderInputDTO order;
    private int retryCount;
}
