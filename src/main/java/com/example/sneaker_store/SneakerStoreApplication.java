package com.example.sneaker_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SneakerStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(SneakerStoreApplication.class, args);
	}

}
