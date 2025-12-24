package com.example.qr.service;

import org.apache.batik.transcoder.TranscoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    @ParameterizedTest(name = "Should handle data format: {1}")
    @CsvSource(delimiter = '|', value = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. | Long text",
            "Test!@#$%^&*()_+-=[]{}|;':\"<>?,./~` | Special characters",
            "https://example.com/path?param1=value1&param2=value2 | URL with parameters"
    })
    void generateQrCodeWithLogo_ShouldHandleVariousDataFormats(String testData, String description)
            throws IOException, TranscoderException {
        byte[] result = qrCodeService.generateQrCodeWithLogo(testData);

        assertNotNull(result, "QR code byte array should not be null for " + description);
        assertTrue(result.length > 0, "QR code byte array should not be empty for " + description);
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleEmptyString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(""),
                "Should throw IllegalArgumentException for empty data");
        assertEquals("QR code data cannot be null or empty", exception.getMessage());
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

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, foregroundColor, backgroundColor, null);

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
                () -> qrCodeService.generateQrCodeWithLogo(testData, foregroundColor, backgroundColor, null),
                "Should throw IllegalArgumentException for " + reason);
        assertTrue(exception.getMessage().contains("Invalid hex color format"));
    }

    @Test
    void generateQrCodeWithLogo_ShouldThrowException_WhenColorIsNull() {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, null, "FFFFFF", null),
                "Should throw IllegalArgumentException for null color");
        assertTrue(exception.getMessage().contains("Color cannot be null or empty"));
    }

    @Test
    void generateQrCodeWithLogo_ShouldThrowException_WhenColorIsEmpty() {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, "", "FFFFFF", null),
                "Should throw IllegalArgumentException for empty color");
        assertTrue(exception.getMessage().contains("Color cannot be null or empty"));
    }

    // Custom logo tests
    @Test
    void generateQrCodeWithLogo_ShouldAcceptCustomSvgLogo() throws IOException, TranscoderException {
        String testData = "https://example.com";
        byte[] svgContent = Files.readAllBytes(Paths.get("src/main/resources/logo.svg"));
        MockMultipartFile svgFile = new MockMultipartFile(
                "logo",
                "custom-logo.svg",
                "image/svg+xml",
                svgContent
        );

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", svgFile);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldUseDefaultLogoWhenNullProvided() throws IOException, TranscoderException {
        String testData = "https://example.com";

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldUseDefaultLogoWhenEmptyFileProvided() throws IOException, TranscoderException {
        String testData = "https://example.com";
        MockMultipartFile emptyFile = new MockMultipartFile("logo", "", "image/png", new byte[0]);

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", emptyFile);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldThrowException_WhenInvalidLogoFormat() {
        String testData = "https://example.com";
        MockMultipartFile invalidFile = new MockMultipartFile(
                "logo",
                "file.txt",
                "text/plain",
                "invalid content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", invalidFile),
                "Should throw IllegalArgumentException for invalid logo format");
        assertTrue(exception.getMessage().contains("Unsupported logo format"));
    }

    @Test
    void generateQrCodeWithLogo_ShouldAcceptPngLogo() throws IOException, TranscoderException {
        String testData = "https://example.com";

        // Create a simple 10x10 PNG image
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);

        MockMultipartFile pngFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                baos.toByteArray()
        );

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", pngFile);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldAcceptJpegLogo() throws IOException, TranscoderException {
        String testData = "https://example.com";

        // Create a simple 10x10 JPEG image
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos);

        MockMultipartFile jpegFile = new MockMultipartFile(
                "logo",
                "logo.jpg",
                "image/jpeg",
                baos.toByteArray()
        );

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", jpegFile);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleCorruptedImageData() {
        String testData = "https://example.com";
        MockMultipartFile corruptedFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                "corrupted data".getBytes()
        );

        assertThrows(IOException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", corruptedFile),
                "Should throw IOException for corrupted image");
    }

    @ParameterizedTest(name = "Should handle logo dimensions: {2}")
    @CsvSource({
            "200, 50, Wide aspect ratio (200x50)",
            "50, 200, Tall aspect ratio (50x200)",
            "100, 100, Square (100x100)",
            "1, 1, Very small (1x1)",
            "500, 500, Large (500x500)"
    })
    void generateQrCodeWithLogo_ShouldHandleVariousLogoDimensions(int width, int height, String description)
            throws IOException, TranscoderException {
        String testData = "https://example.com";

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);

        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                baos.toByteArray()
        );

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);

        assertNotNull(result, "QR code byte array should not be null for " + description);
        assertTrue(result.length > 0, "QR code byte array should not be empty for " + description);
    }

    // Additional color edge case tests
    @ParameterizedTest(name = "Should reject 3-digit hex color: {0}")
    @CsvSource({
            "FFF",
            "000",
            "F0F"
    })
    void generateQrCodeWithLogo_ShouldReject3DigitHexColors(String shortColor) {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, shortColor, "FFFFFF", null),
                "Should reject 3-digit hex color");
        assertTrue(exception.getMessage().contains("Invalid hex color format"));
    }

    @ParameterizedTest(name = "Should handle color edge case: {2}")
    @CsvSource({
            "aAbBcC, DdEeFf, Mixed case hex colors",
            "000000, FFFFFF, All zero foreground",
            "FFFFFF, 000000, All F foreground",
            "FFFFFF, FFFFFF, Same colors (unreadable)",
            "FFFFFF, FFFFFE, Very similar colors"
    })
    void generateQrCodeWithLogo_ShouldHandleColorEdgeCases(String foregroundColor, String backgroundColor, String description)
            throws IOException, TranscoderException {
        String testData = "test";

        byte[] result = qrCodeService.generateQrCodeWithLogo(testData, foregroundColor, backgroundColor, null);

        assertNotNull(result, "QR code byte array should not be null for " + description);
        assertTrue(result.length > 0, "QR code byte array should not be empty for " + description);
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleRgbPrimaryColors() throws IOException, TranscoderException {
        String testData = "test";

        byte[] redResult = qrCodeService.generateQrCodeWithLogo(testData, "FF0000", "FFFFFF", null);
        byte[] greenResult = qrCodeService.generateQrCodeWithLogo(testData, "00FF00", "FFFFFF", null);
        byte[] blueResult = qrCodeService.generateQrCodeWithLogo(testData, "0000FF", "FFFFFF", null);

        assertAll(
                () -> assertNotNull(redResult, "Red QR code should not be null"),
                () -> assertNotNull(greenResult, "Green QR code should not be null"),
                () -> assertNotNull(blueResult, "Blue QR code should not be null")
        );
    }

    @Test
    void generateQrCodeWithLogo_ShouldRejectColorWithSpace() {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, "FF 0000", "FFFFFF", null),
                "Should reject color with space");
        assertTrue(exception.getMessage().contains("Invalid hex color format"));
    }

    @Test
    void generateQrCodeWithLogo_ShouldRejectColorWithSpecialChars() {
        String testData = "test";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> qrCodeService.generateQrCodeWithLogo(testData, "FF@000", "FFFFFF", null),
                "Should reject color with special chars");
        assertTrue(exception.getMessage().contains("Invalid hex color format"));
    }

    @Test
    void generateQrCodeWithLogo_ShouldHandleLargeDataCapacity() throws IOException, TranscoderException {
        // QR codes with HIGH error correction can hold significant amount of data
        // Using 1000 characters to stay safely within limits
        String largeData = "A".repeat(1000);

        byte[] result = qrCodeService.generateQrCodeWithLogo(largeData);

        assertNotNull(result, "QR code byte array should not be null");
        assertTrue(result.length > 0, "QR code byte array should not be empty");
    }

}

