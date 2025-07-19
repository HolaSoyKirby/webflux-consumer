package com.example.ordersconsumer.api;

import com.example.ordersconsumer.exception.NotFoundException;
import com.example.ordersconsumer.model.apimodel.CustomerDetail;
import com.example.ordersconsumer.model.apimodel.OrderGoResponse;
import com.example.ordersconsumer.model.apimodel.ProductDetail;
import com.example.ordersconsumer.model.kafka.OrderInputDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoApi {
    private final WebClient goApiClient;

    public GoApi(@Qualifier("goApiClient") WebClient goApiClient) {
        this.goApiClient = goApiClient;
    }

    public Mono<OrderGoResponse> validateOrder(OrderInputDTO orderInput) {
        Integer orderId = orderInput.getOrderId();
        Integer customerId = orderInput.getCustomerId();
        List<Integer> productIds = orderInput.getProductIds() != null ? orderInput.getProductIds() : List.of();

        return getCustomer(customerId)
                .zipWith(getProducts(productIds))
                .map(tuple -> {
                    CustomerDetail customer = tuple.getT1();
                    List<ProductDetail> products = tuple.getT2();

                    List<String> errors = new ArrayList<>();

                    if (!Boolean.TRUE.equals(customer.getActive())) {
                        errors.add("Cliente inválido o inactivo");
                    }

                    if (products.size() != productIds.size()) {
                        errors.add("Algunos productos seleccionados son inválidos");
                    }

                    OrderGoResponse response = new OrderGoResponse();
                    response.setOrderId(orderId);
                    response.setCustomer(customer);
                    response.setProducts(products);
                    response.setErrors(errors);
                    return response;
                });
    }

    public Mono<CustomerDetail> getCustomer(Integer customerId) {
        return goApiClient.get()
                .uri("/customer/{id}", customerId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, r -> {
                    if (r.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new NotFoundException("Cliente no encontrado con el ID: " + customerId));
                    }
                    return r.createException();
                })
                .bodyToMono(CustomerDetail.class)
                .onErrorResume(e -> {
                    if (e instanceof NotFoundException) {
                        return Mono.just(new CustomerDetail());
                    }
                    System.err.println("Error obteniendo cliente: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<List<ProductDetail>> getProducts(List<Integer> productIds) {
        if (productIds.isEmpty()) {
            return Mono.just(List.of());
        }

        return goApiClient.post()
                .uri("/product/get-list-of-products")
                .bodyValue(productIds)
                .retrieve()
                .bodyToFlux(ProductDetail.class)
                .collectList()
                .onErrorResume(e -> {
                    System.err.println("Error obteniendo productos: " + e.getMessage());
                    return Mono.error(e);
                });
    }
}
