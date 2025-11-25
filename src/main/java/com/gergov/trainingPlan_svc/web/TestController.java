package com.gergov.trainingPlan_svc.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public String hello() {
        log.info("Test hello endpoint called");
        return "Test Hello from Microservice!";
    }

    @GetMapping("/ping")
    public String ping() {
        log.info("Ping endpoint called");
        return "PONG";
    }
}