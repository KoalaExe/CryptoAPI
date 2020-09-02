package com.dev.CryptoAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CryptoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoApiApplication.class, args);
	}

}
