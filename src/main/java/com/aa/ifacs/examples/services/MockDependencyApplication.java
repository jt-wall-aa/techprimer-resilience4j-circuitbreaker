package com.aa.ifacs.examples.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@SpringBootApplication
public class MockDependencyApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MockDependencyApplication.class);
        application.setDefaultProperties(Collections
                .singletonMap("server.port", "8081"));
        application.run(args);
        // Needed to set the server port directly, so had to change the startup method.
       // SpringApplication.run(CircuitbreakerResilience4jApplication.class
        log.info("Application " + MockDependencyApplication.class + "started.");

    }

    @Bean
    public RestTemplate restTemplate() {
        log.info("Creating restTemplate");
        return new RestTemplate();
    }



}
