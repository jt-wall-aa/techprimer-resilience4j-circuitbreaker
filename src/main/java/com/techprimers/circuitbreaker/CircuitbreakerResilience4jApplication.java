package com.techprimers.circuitbreaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

@Slf4j
@SpringBootApplication
public class CircuitbreakerResilience4jApplication {

	public static void main(String[] args) {
		CircuitbreakerResilience4jApplication application = new CircuitbreakerResilience4jApplication();
		SpringApplication.run(application.getClass(), args);

		log.info("Application " + CircuitbreakerResilience4jApplication.class + "started.");

	}

	@Bean
	public RestTemplate restTemplate() {
		log.info("Creating restTemplate");
		return new RestTemplate();
	}


}
