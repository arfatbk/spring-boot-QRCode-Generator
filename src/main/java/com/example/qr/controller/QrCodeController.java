package com.example.qr.controller;

import com.example.qr.service.QrCodeService;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class QrCodeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeController.class);

    private final QrCodeService service;

    public QrCodeController(QrCodeService service) {
        this.service = service;
    }


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> generateQrCode(
            @RequestParam("data") String data,
            @RequestParam(value = "foregroundColor", defaultValue = "5DADE2") String foregroundColor,
            @RequestParam(value = "backgroundColor", defaultValue = "FFFFFF") String backgroundColor) {
        LOGGER.info("Received request to generate QR code for data length: {}, colors: fg={}, bg={}",
                    data.length(), foregroundColor, backgroundColor);
        try {
            // Create service instance with custom colors
            byte[] qrCode = service.generateQrCodeWithLogo(data, foregroundColor, backgroundColor);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCode.length);

            LOGGER.info("QR code generated successfully");
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid QR code data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException | TranscoderException e) {
            LOGGER.error("Failed to generate QR code for data length: {}", data.length(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

