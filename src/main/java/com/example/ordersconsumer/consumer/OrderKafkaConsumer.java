package com.example.ordersconsumer.consumer;

import com.example.ordersconsumer.api.GoApi;
import com.example.ordersconsumer.model.apimodel.OrderGoResponse;
import com.example.ordersconsumer.model.apimodel.ProductDetail;
import com.example.ordersconsumer.model.kafka.OrderInputDTO;
import com.example.ordersconsumer.model.mongo.order.Product;
import com.example.ordersconsumer.model.mongo.order.SuccessfulProcessedOrder;
import com.example.ordersconsumer.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderKafkaConsumer {
    private final GoApi goApi;
    private final OrderService orderService;

    public OrderKafkaConsumer(
            GoApi goApi,
            OrderService orderService
    ) {
        this.goApi = goApi;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "${kafka-order.topic}", groupId = "${kafka-order.group-id}")
    public void listen(OrderInputDTO input, Acknowledgment ack) {
        System.out.println("Procesando pedido en kafka consumer: " + input.getOrderId());

        goApi.validateOrder(input)
                .flatMap(orderResponse -> {
                    if (orderResponse.isValidOrder()) {
                        SuccessfulProcessedOrder order = mapToOrder(orderResponse);
                        return orderService.saveOrder(order)
                                .doOnSuccess(savedOrder -> {
                                    System.out.println("Pedido guardado: " + savedOrder.getOrderId());
                                    System.out.println("Notificando pedido confirmado al usuario al usuario: " + savedOrder.getCustomerId());
                                });
                    } else {
                        System.out.println("Pedido inválido notificando a la patrulla wiu wiu y guardando en órdenes inválidas: " + orderResponse.getErrors());
                        return Mono.empty();
                    }
                })
                .doOnError(error -> {
                    System.err.println("Error procesando pedido: " + error.getMessage());
                    System.err.println("Guardando en DB de pedidos fallidos para procesarlos después");
                })
                .doFinally(signal -> ack.acknowledge())
                .subscribe();
    }

    private SuccessfulProcessedOrder mapToOrder(OrderGoResponse orderResponse) {
        SuccessfulProcessedOrder order = new SuccessfulProcessedOrder();
        order.setOrderId(orderResponse.getOrderId());
        order.setCustomerId(orderResponse.getCustomer().getCustomerId());

        List<Product> products = new ArrayList<>();
        for(ProductDetail productResponse : orderResponse.getProducts()) {
            Product product = new Product();
            product.setProductId(productResponse.getProductId());
            product.setName(productResponse.getName());
            product.setPrice(productResponse.getPrice());

            products.add(product);
        }

        order.setProducts(products);

        return order;
    }
}
