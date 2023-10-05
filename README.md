# Circuit Breaker using Spring Boot 3 and Resilience4J

![Architecture](./architecture.png)

This project was originally forked from https://github.com/TechPrimers/resilience4j-circuitbreaker

This is a very simple demonstration of the circuit breaker pattern of Resilience4J that demonstrates a fallback method.

Developers should look at the following files
To see fallback method and example circuit breaker:  `src/main/java/com.techprimers.circuitbreaker.controller.ActivityController`

To see configuration settings: src/resources: `application.yml`

For more information visit https://resilience4j.readme.io/docs


## Endpoints
- `http://localhost:8080/activity` returns random activity from `boredapi.com`
