package com.example.qr.service;

import org.apache.batik.transcoder.TranscoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeServiceTest {

    private QrCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        Resource logoResource = new ClassPathResource("logo.svg");
        qrCodeService = new QrCodeService(logoResource, "5DADE2", "FFFFFF");
    }

    @Test
    void generateQrCodeWithLogo_ShouldReturnByteArray_WhenValidDataProvided() throws IOException, TranscoderException {
        String testData = "https://github.com";

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldThrowException_WhenNullDataProvided() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(null),
                "Should throw IllegalArgumentException for null data");
        assertEquals("QR code data cannot be null or empty", exception.getMessage());
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleLongText() throws IOException, TranscoderException {
        String longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(10);

        byte[] result = qrCodeService.generateQrCodeWithLogo(longText);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleSpecialCharacters() throws IOException, TranscoderException {
        String specialText = "Test!@#$%^&*()_+-=[]{}|;':\"<>?,./~`";

        byte[] result = qrCodeService.generateQrCodeWithLogo(specialText);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleUrls() throws IOException, TranscoderException {
        String url = "https://example.com/path?param1=value1&param2=value2";

        byte[] result = qrCodeService.generateQrCodeWithLogo(url);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleEmptyString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(""),
                "Should throw IllegalArgumentException for empty data");
        assertEquals("QR code data cannot be null or empty", exception.getMessage());
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleWhitespaceOnlyString() throws IOException, TranscoderException {
        String whitespaceData = "   ";

        byte[] result = qrCodeService.generateQrCodeWithLogo(whitespaceData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleVeryShortText() throws IOException, TranscoderException {
        String shortText = "a";

        byte[] result = qrCodeService.generateQrCodeWithLogo(shortText);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleNumericData() throws IOException, TranscoderException {
        String numericData = "1234567890";

        byte[] result = qrCodeService.generateQrCodeWithLogo(numericData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleEmailAddress() throws IOException, TranscoderException {
        String emailData = "mailto:test@example.com";

        byte[] result = qrCodeService.generateQrCodeWithLogo(emailData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandlePhoneNumber() throws IOException, TranscoderException {
        String phoneData = "tel:+1234567890";

        byte[] result = qrCodeService.generateQrCodeWithLogo(phoneData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleMultilineText() throws IOException, TranscoderException {
        String multilineData = "Line 1\nLine 2\nLine 3";

        byte[] result = qrCodeService.generateQrCodeWithLogo(multilineData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleUnicodeCharacters() throws IOException, TranscoderException {
        String unicodeData = "Hello 世界 🌍";

        byte[] result = qrCodeService.generateQrCodeWithLogo(unicodeData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleJsonData() throws IOException, TranscoderException {
        String jsonData = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";

        byte[] result = qrCodeService.generateQrCodeWithLogo(jsonData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleWifiConfiguration() throws IOException, TranscoderException {
        String wifiData = "WIFI:T:WPA;S:MyNetwork;P:MyPassword;;";

        byte[] result = qrCodeService.generateQrCodeWithLogo(wifiData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    // Color customization tests - Valid colors
    @ParameterizedTest(name = "Should accept colors: fg={0}, bg={1}")
    @CsvSource({
            "FF0000, FFFF00",           // Red on Yellow
            "#FF0000, #00FF00",         // With hash prefix
            "000000, FFFFFF",           // Black and White
            "ff0000, ffff00",           // Lowercase
            "5DADE2, FFFFFF",           // Light Blue (default)
            "9B59B6, ECF0F1",           // Purple on Light Gray
            "FFFFFF, 000000"            // White on Black (dark mode)
    })
    void generateQrCodeWithLogo_ShouldAcceptValidColorCombinations(String foregroundColor, String backgroundColor)
            throws IOException, TranscoderException {
        String testData = "https://example.com";

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, foregroundColor, backgroundColor);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    // Color customization tests - Invalid colors
    @ParameterizedTest(name = "Should reject invalid colors: fg={0}, bg={1} - {2}")
    @CsvSource({
            "INVALID, FFFFFF, invalid characters",
            "FF0000, ZZZ, too short with invalid chars",
            "FFF, FFFFFF, too short",
            "FF00001, FFFFFF, too long"
    })
    void generateQrCodeWithLogo_ShouldThrowException_WhenInvalidColorFormat(
            String foregroundColor, String backgroundColor, String reason) {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, foregroundColor, backgroundColor),
                "Should throw IllegalArgumentException for " + reason);
        assertTrue(exception.getMessage().contains("Invalid hex color format"));
    }

    @Test
    void generateQrCodeWithLogo_ShouldThrowException_WhenColorIsNull() {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, null, "FFFFFF"),
                "Should throw IllegalArgumentException for null color");
        assertTrue(exception.getMessage().contains("Color cannot be null or empty"));
    }

    @Test
    void generateQrCodeWithLogo_ShouldThrowException_WhenColorIsEmpty() {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, "", "FFFFFF"),
                "Should throw IllegalArgumentException for empty color");
        assertTrue(exception.getMessage().contains("Color cannot be null or empty"));
    }
}

