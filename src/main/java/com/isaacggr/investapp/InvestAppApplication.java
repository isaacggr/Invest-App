package com.isaacggr.investapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.isaacggr")
public class InvestAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(InvestAppApplication.class, args);
    }
}
