package com.chill.order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCaching
@SpringBootApplication
@EnableFeignClients
public class OrderAndPaymentMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderAndPaymentMicroserviceApplication.class, args);
	}

}
