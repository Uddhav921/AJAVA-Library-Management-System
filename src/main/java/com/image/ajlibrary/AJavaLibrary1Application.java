package com.image.ajlibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Application entry point.
 * @EnableAsync activates Spring's asynchronous method execution (CO2 - Multithreading).
 */
@SpringBootApplication
@EnableAsync
public class AJavaLibrary1Application {

    public static void main(String[] args) {
        SpringApplication.run(AJavaLibrary1Application.class, args);
    }
}
