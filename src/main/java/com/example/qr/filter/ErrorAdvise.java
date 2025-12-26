package com.example.qr.filter;

import com.example.qr.controller.QrCodeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorAdvise {
    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeController.class);


    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        LOGGER.error("An error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("someting went wrong");
    }
}
