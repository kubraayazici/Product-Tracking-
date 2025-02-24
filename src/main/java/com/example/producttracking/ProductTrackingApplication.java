package com.example.producttracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(
        exclude = {
                SystemMetricsAutoConfiguration.class,
                TomcatMetricsAutoConfiguration.class
        }
)
@EntityScan("com.example.producttracking.model")
public class ProductTrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductTrackingApplication.class, args);
        System.out.println("Spring Boot application started successfully!");
    }
}