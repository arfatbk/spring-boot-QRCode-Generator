package com.example.qr.controller;

import com.example.qr.service.QrCodeService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@Validated
public class QrCodeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeController.class);
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5MB

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
            @RequestParam("data") @NotBlank(message = "Data cannot be empty") String data,
            @RequestParam(value = "foregroundColor", defaultValue = "5DADE2")
            @Pattern(regexp = "^#?[0-9A-Fa-f]{6}$", message = "Invalid foreground color format")
            String foregroundColor,
            @RequestParam(value = "backgroundColor", defaultValue = "FFFFFF")
            @Pattern(regexp = "^#?[0-9A-Fa-f]{6}$", message = "Invalid background color format")
            String backgroundColor,
            @RequestParam(value = "logo", required = false) MultipartFile logoFile) {

        LOGGER.info("Received request to generate QR code for data length: {}, colors: fg={}, bg={}, hasCustomLogo={}",
                    data.length(), foregroundColor, backgroundColor, logoFile != null && !logoFile.isEmpty());

        if(Math.random() < 0.5)
                throw new IllegalArgumentException("Testing observability");
        try {
            // Validate custom logo if provided
            if (logoFile != null && !logoFile.isEmpty()) {
                validateLogoFile(logoFile);
            }

            byte[] qrCode = service.generateQrCodeWithLogo(data, foregroundColor, backgroundColor, logoFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCode.length);

            LOGGER.info("QR code generated successfully");
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException | TranscoderException e) {
            LOGGER.error("Failed to generate QR code for data length: {}", data.length(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void validateLogoFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Logo file size exceeds maximum allowed size of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/svg+xml") &&
                                     !contentType.equals("image/png") &&
                                     !contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Invalid logo file format. Supported formats: SVG, PNG, JPEG");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Logo file name is required");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!extension.matches("svg|png|jpg|jpeg")) {
            throw new IllegalArgumentException("Invalid logo file extension. Supported: .svg, .png, .jpg, .jpeg");
        }
    }
}

