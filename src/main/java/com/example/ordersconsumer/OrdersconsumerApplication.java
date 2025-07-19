package com.example.ordersconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OrdersconsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersconsumerApplication.class, args);
	}

}
