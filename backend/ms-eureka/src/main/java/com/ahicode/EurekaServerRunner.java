package com.ahicode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerRunner {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerRunner.class, args);
    }
}