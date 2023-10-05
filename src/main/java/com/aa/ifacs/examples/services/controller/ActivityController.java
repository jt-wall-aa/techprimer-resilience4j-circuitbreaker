package com.aa.ifacs.examples.services.controller;

import com.google.gson.Gson;

import com.aa.ifacs.examples.services.model.Activity;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Iterator;
import java.util.Random;

@Slf4j
@RequestMapping("/activity")
@RestController
public class ActivityController {

    private RestTemplate restTemplate;

    private String[] activities = {"walking", "swimming", "biking", "hiking", "dancing",
                                   "gymnastics", "weight lifting", "golf", "skiing",
                                    "wake boarding", "archery", "jogging",
                                    "boxing", "judo", "wrestling"};

    private Gson gson = new Gson();

    private java.util.Random random = new Random();
    public ActivityController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*-- @CircuitBreaker catches failure, then falls back to alternate method.
          The circuit will automatically close if it can reach downstream service.
    --*/
    @GetMapping
    public String getRandomActivity() {

        int length = activities.length;
        Activity activity = new Activity(activities[random.nextInt(length-1)]);
        String s = gson.toJson(activity);
        log.info("Returning activity of " + s);
        return s;
    }


}

