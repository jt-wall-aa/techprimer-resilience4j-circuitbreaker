package com.aa.itfacs.examples.resilience4j.circuitbreaker.controller;


import com.techprimers.circuitbreaker.model.Activity;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.core.Registry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Iterator;

@Slf4j
@RequestMapping("/activity")
@RestController
public class ActivityController {

    private RestTemplate restTemplate;

    private final String BORED_API = "https://www.boredapi.com/api/activity";
    private final String MOCK_SERVICE = "http://localhost:8081/activity";

    @Autowired
    private  CircuitBreakerRegistry circuitBreakerRegistry;
    private io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker;

    public ActivityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*-- @CircuitBreaker catches failure, then falls back to alternate method.
          The circuit will automatically close if it can reach downstream service.
    --*/
    @GetMapping
    @CircuitBreaker(name = "randomActivity", fallbackMethod = "fallbackRandomActivity")
    public String getRandomActivity() {

        /* The circuitBreaker may not get autowired or created until the first call of this controller.
           So we are finding the circuit breaker in the autowired registry, then adding some Predicates that
           get activated for each CircuitBreaker event type.
         */
        if(circuitBreaker == null)
        {
            circuitBreaker = findCircuitBreaker("randomActivity");
            registerEventsOnCircuitBreaker();
        }

       // Activity activity = getActivityFromBoredAPI();
        Activity activity = getActivityFromMockService();
        log.info("Activity received: " + activity.getActivity());
        return activity.getActivity();
    }

    private Activity getActivityFromBoredAPI(){
        ResponseEntity<Activity> responseEntity = restTemplate.getForEntity(BORED_API, Activity.class);
        return responseEntity.getBody();
    }

    private Activity getActivityFromMockService(){
        ResponseEntity<Activity> responseEntity = restTemplate.getForEntity(MOCK_SERVICE, Activity.class);
        return responseEntity.getBody();
    }

    public String fallbackRandomActivity(Throwable throwable) {

        StringBuilder msg = new StringBuilder();
        String nl = System.lineSeparator();
        log.info("Circuit breaker tripped.");
        if(circuitBreaker != null) {
            log.info("Circuit breaker in state of: " + circuitBreaker.getState());
        }
        msg.append("Circuit breaker was tripped, and caught an exception of: ")
                .append(throwable.getClass().descriptorString()).append(nl)
                .append("message of: ").append(throwable.getMessage()).append(nl)
                .append("Unable to reach dependency service.").append(nl)
                .append("Why don't you read more about Resilience4J instead.");
        return msg.toString();
    }

    private void registerEventsOnCircuitBreaker()
    {

        if(circuitBreaker != null) {
            log.info("Registering predicates for circuit breakers event publisher");
            circuitBreaker.getEventPublisher()
                    .onSuccess(event -> log.info("A successful event of " + event.toString() + " was received at " + Instant.now().toString()))
                    .onError(event -> log.info("An error event of " + event.toString() + " was received at " + Instant.now().toString()))
                    .onIgnoredError(event -> log.info("An ignoredError event of " + event.toString() + " was received at " + Instant.now().toString()))
                    .onReset(event -> log.info("A reset event of " + event.toString() + " was received at " + Instant.now().toString()))
                    .onStateTransition(event -> log.info("A state change event of " + event.toString() + " was received at " + Instant.now().toString()));
            // Or if you want to register a consumer listening
            // to all events, you can do:
            //	circuitBreaker.getEventPublisher()
            //			.onEvent(event -> logger.info(...));\

        } else {
            log.info("Unable to find circuit breaker");
        }

    }

    private io.github.resilience4j.circuitbreaker.CircuitBreaker findCircuitBreaker(String name)
    {
        if(circuitBreakerRegistry != null)
        {
            /* lets show the circuit breaker information */
            io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker = null;
            Iterator<io.github.resilience4j.circuitbreaker.CircuitBreaker> circuitBreakerIterator = circuitBreakerRegistry.getAllCircuitBreakers().iterator();

            while (circuitBreakerIterator.hasNext()) {
                circuitBreaker = circuitBreakerIterator.next();
                if (circuitBreaker.getName().equals(name)) {
                    return circuitBreaker;
                }
            }
            log.info("Cannot find cto register circuitBreaker events");
            return circuitBreaker;
        }
        else {
            log.info("CircuitBreakerRegistry is still null. Unable to register circuitBreaker events");
            return circuitBreaker;
        }
    }

}

