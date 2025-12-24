package com.example.qr.controller;

import com.example.qr.service.QrCodeService;
import org.apache.batik.transcoder.TranscoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QrCodeControllerTest {

    private QrCodeController qrCodeController;
    private QrCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeService = mock(QrCodeService.class);
        qrCodeController = new QrCodeController(qrCodeService);
    }

    @Test
    void index_ShouldReturnIndexView() {
        String result = qrCodeController.index();

        assertEquals("index", result, "Should return index view name");
    }

    @Test
    void generateQrCode_ShouldReturnOk_WhenServiceSucceeds() throws IOException, TranscoderException {
        String testData = "https://example.com";
        byte[] mockQrCode = new byte[]{1, 2, 3, 4, 5};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        assertAll(
            () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK"),
            () -> assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType(), "Content type should be PNG"),
            () -> assertNotNull(response.getBody(), "Response body should not be null"),
            () -> assertArrayEquals(mockQrCode, response.getBody(), "Response body should match mock data"),
            () -> assertEquals(mockQrCode.length, response.getHeaders().getContentLength(), "Content length should match")
        );
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF");
    }

    @Test
    void generateQrCode_ShouldReturnInternalServerError_WhenServiceThrowsIOException() throws IOException, TranscoderException {
        String testData = "https://example.com";
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenThrow(new IOException("Test exception"));

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF");
    }

    @Test
    void generateQrCode_ShouldReturnInternalServerError_WhenServiceThrowsTranscoderException() throws IOException, TranscoderException {
        String testData = "https://example.com";
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenThrow(new TranscoderException("Test exception"));

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF");
    }

    @Test
    void generateQrCode_ShouldReturnInternalServerError_WhenServiceThrowsIllegalArgumentException() throws IOException, TranscoderException {
        String testData = "";
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenThrow(new IllegalArgumentException("QR code data cannot be null or empty"));

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF");
    }

    @Test
    void generateQrCode_ShouldWorkWithValidData() throws IOException, TranscoderException {
        String testData = "test-data";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF");
    }

    @Test
    void generateQrCode_ShouldAcceptCustomColors() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "FF0000", "00FF00")).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "FF0000", "00FF00");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "FF0000", "00FF00");
    }

    @Test
    void generateQrCode_ShouldHandleLongData() throws IOException, TranscoderException {
        String longData = "a".repeat(1000);
        byte[] mockQrCode = new byte[]{1, 2, 3, 4, 5};
        when(qrCodeService.generateQrCodeWithLogo(longData, "5DADE2", "FFFFFF")).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(longData, "5DADE2", "FFFFFF");

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(longData, "5DADE2", "FFFFFF");
    }

    @Test
    void generateQrCode_ShouldSetCorrectContentTypeInHeaders() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType(), "Content type should be IMAGE_PNG");
    }

    @Test
    void generateQrCode_ShouldSetCorrectContentLengthInHeaders() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3, 4, 5};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        assertEquals(5, response.getHeaders().getContentLength(), "Content length should be 5");
    }

    @Test
    void generateQrCode_ShouldCallServiceWithCorrectData() throws IOException, TranscoderException {
        String testData = "test-data";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF")).thenReturn(mockQrCode);

        qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF");

        verify(qrCodeService).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF");
        verifyNoMoreInteractions(qrCodeService);
    }
}

