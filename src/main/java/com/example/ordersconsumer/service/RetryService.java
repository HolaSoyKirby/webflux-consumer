package com.example.ordersconsumer.service;

import com.example.ordersconsumer.api.GoApi;
import com.example.ordersconsumer.model.apimodel.OrderGoResponse;
import com.example.ordersconsumer.model.apimodel.ProductDetail;
import com.example.ordersconsumer.model.kafka.OrderInputDTO;
import com.example.ordersconsumer.model.kafka.FailedOrderDTO;
import com.example.ordersconsumer.model.mongo.order.Product;
import com.example.ordersconsumer.model.mongo.order.SuccessfulProcessedOrder;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class RetryService {

    private static final String REDIS_KEY = "failed-orders";
    private static final int MAX_RETRIES = 5;

    private final ReactiveRedisTemplate<String, FailedOrderDTO> redisTemplate;
    private final GoApi goApi;
    private final OrderService orderService;

    public RetryService(
            ReactiveRedisTemplate<String, FailedOrderDTO> redisTemplate,
            GoApi goApi,
            OrderService orderService
    ) {
        this.redisTemplate = redisTemplate;
        this.goApi = goApi;
        this.orderService = orderService;
    }

    public Mono<Void> saveFailedOrder(FailedOrderDTO failedOrder) {
        return redisTemplate.opsForList()
                .leftPush(REDIS_KEY, failedOrder)
                .doOnNext(count -> System.out.println("Pedido fallido guardado en redis: " + failedOrder.getOrder().getOrderId()))
                .then();
    }

    public Mono<Void> retryFailedOrders() {
        return redisTemplate.opsForList()
                .rightPop(REDIS_KEY)
                .flatMap(this::processOrderWithRetry)
                .onErrorResume(e -> {
                    System.err.println("Error procesando orden del retry: " + e.getMessage());
                    return Mono.empty();
                })
                .then();
    }

    private Mono<Void> processOrderWithRetry(FailedOrderDTO failedOrder) {
        OrderInputDTO order = failedOrder.getOrder();
        int currentRetry = failedOrder.getRetryCount();


        if (currentRetry >= MAX_RETRIES) {
            System.err.println("Pedido " + order.getOrderId() + " excedió máximo de reintentos. Abortando pedido.");
            return Mono.empty(); // Ya no se vuelve a guardar en Redis
        }

        failedOrder.setRetryCount(currentRetry + 1);

        System.out.println("Entrada de pedido fallido: orden " + order.getOrderId() + " reintento número: " + failedOrder.getRetryCount());

        return goApi.validateOrder(order)
                .flatMap(orderResponse -> {
                    if (orderResponse.isValidOrder()) {
                        SuccessfulProcessedOrder mappedOrder = mapToOrder(orderResponse);
                        return orderService.saveOrder(mappedOrder)
                                .doOnSuccess(saved -> System.out.println("Pedido reintentado guardado: " + saved.getOrderId()))
                                .then();
                    } else {
                        System.out.println("Pedido inválido notificando a la patrulla wiu wiu y guardando en órdenes inválidas: " + orderResponse.getErrors());
                        return Mono.empty();
                    }
                })
                .onErrorResume(error -> {
                    System.err.println("Error en reintento número " + failedOrder.getRetryCount() + " del pedido " + order.getOrderId() + ": " + error.getMessage());
                    return saveFailedOrder(failedOrder);
                });
    }

    private SuccessfulProcessedOrder mapToOrder(OrderGoResponse orderResponse) {
        SuccessfulProcessedOrder order = new SuccessfulProcessedOrder();
        order.setOrderId(orderResponse.getOrderId());
        order.setCustomerId(orderResponse.getCustomer().getCustomerId());

        List<Product> products = new ArrayList<>();
        for (ProductDetail productResponse : orderResponse.getProducts()) {
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