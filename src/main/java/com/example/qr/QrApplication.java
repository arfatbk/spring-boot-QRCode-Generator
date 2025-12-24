package com.example.qr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QrApplication {

    private QrApplication(){}

    static void main(String[] args) {
        SpringApplication.run(QrApplication.class, args);
    }

}
