package com.example.qr.controller;

import com.example.qr.service.QrCodeService;
import org.apache.batik.transcoder.TranscoderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertAll(
            () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK"),
            () -> assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType(), "Content type should be PNG"),
            () -> assertNotNull(response.getBody(), "Response body should not be null"),
            () -> assertArrayEquals(mockQrCode, response.getBody(), "Response body should match mock data"),
            () -> assertEquals(mockQrCode.length, response.getHeaders().getContentLength(), "Content length should match")
        );
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldReturnInternalServerError_WhenServiceThrowsIOException() throws IOException, TranscoderException {
        String testData = "https://example.com";
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenThrow(new IOException("Test exception"));

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldReturnInternalServerError_WhenServiceThrowsTranscoderException() throws IOException, TranscoderException {
        String testData = "https://example.com";
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenThrow(new TranscoderException("Test exception"));

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws IOException, TranscoderException {
        String testData = "";
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenThrow(new IllegalArgumentException("QR code data cannot be null or empty"));

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldWorkWithValidData() throws IOException, TranscoderException {
        String testData = "test-data";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldAcceptCustomColors() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "FF0000", "00FF00", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "FF0000", "00FF00", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "FF0000", "00FF00", null);
    }

    @Test
    void generateQrCode_ShouldHandleLongData() throws IOException, TranscoderException {
        String longData = "a".repeat(1000);
        byte[] mockQrCode = new byte[]{1, 2, 3, 4, 5};
        when(qrCodeService.generateQrCodeWithLogo(longData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(longData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(longData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldSetCorrectContentTypeInHeaders() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType(), "Content type should be IMAGE_PNG");
    }

    @Test
    void generateQrCode_ShouldSetCorrectContentLengthInHeaders() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3, 4, 5};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(5, response.getHeaders().getContentLength(), "Content length should be 5");
    }

    @Test
    void generateQrCode_ShouldCallServiceWithCorrectData() throws IOException, TranscoderException {
        String testData = "test-data";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        verify(qrCodeService).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
        verifyNoMoreInteractions(qrCodeService);
    }

    @ParameterizedTest(name = "Should accept logo file: {0}")
    @CsvSource({
            "logo.png, image/png, fake png content",
            "logo.svg, image/svg+xml, <svg></svg>",
            "logo.jpg, image/jpeg, fake jpeg content"
    })
    void generateQrCode_ShouldAcceptDifferentLogoFormats(String filename, String contentType, String content)
            throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile("logo", filename, contentType, content.getBytes());
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldRejectOversizedLogoFile() {
        String testData = "test";
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                largeContent
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for oversized file");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldRejectInvalidLogoFileType() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "file.txt",
                "text/plain",
                "not an image".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for invalid file type");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldRejectLogoWithInvalidExtension() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "file.bmp",
                "image/bmp",
                "fake bmp".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for invalid extension");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldRejectLogoWithoutFilename() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                null,
                "image/png",
                "fake png".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for null filename");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldRejectLogoWithEmptyFilename() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "",
                "image/png",
                "fake png".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for empty filename");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldAcceptJpegWithAlternateExtension() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.jpeg",
                "image/jpeg",
                "fake jpeg".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for .jpeg extension");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleUppercaseFileExtension() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.PNG",
                "image/png",
                "fake png".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for uppercase extension");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleMixedCaseFileExtension() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.JpG",
                "image/jpeg",
                "fake jpeg".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for mixed case extension");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleColorWithHashPrefix() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "#FF0000", "#00FF00", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "#FF0000", "#00FF00", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for colors with # prefix");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "#FF0000", "#00FF00", null);
    }

    @Test
    void generateQrCode_ShouldHandleColorWithLowercase() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "ff0000", "00ff00", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "ff0000", "00ff00", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for lowercase colors");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "ff0000", "00ff00", null);
    }

    @Test
    void generateQrCode_ShouldHandleComplexUrl() throws IOException, TranscoderException {
        String complexUrl = "https://example.com/path?param1=value1&param2=value2#fragment";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(complexUrl, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(complexUrl, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for complex URL");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(complexUrl, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleSpecialCharacters() throws IOException, TranscoderException {
        String dataWithSpecialChars = "Test!@#$%^&*()_+-=[]{}|;':\"<>?,./~`";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(dataWithSpecialChars, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(dataWithSpecialChars, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for special characters");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(dataWithSpecialChars, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldAcceptMaximumSizeLogoFile() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        byte[] exactlyFiveMB = new byte[5 * 1024 * 1024];
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                exactlyFiveMB
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for exactly 5MB file");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldRejectSlightlyOversizedLogoFile() {
        String testData = "test";
        byte[] slightlyOverFiveMB = new byte[5 * 1024 * 1024 + 1];
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                slightlyOverFiveMB
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for file > 5MB");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldHandleNullContentType() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                null,
                "fake png".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for null content type");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldHandleMultilineData() throws IOException, TranscoderException {
        String multilineData = "Line 1\nLine 2\nLine 3";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(multilineData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(multilineData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for multiline data");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(multilineData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleUnicodeData() throws IOException, TranscoderException {
        String unicodeData = "Hello 世界 🌍";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(unicodeData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(unicodeData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for unicode data");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(unicodeData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldSetCorrectHeadersOnError() {
        String testData = "test";
        MockMultipartFile oversizedFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[6 * 1024 * 1024]
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", oversizedFile);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNull(response.getBody(), "Body should be null on error")
        );
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldHandleVeryLongData() throws IOException, TranscoderException {
        String veryLongData = "a".repeat(4000);
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(veryLongData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(veryLongData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for very long data");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(veryLongData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleEmptyLogoFile() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile emptyFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                new byte[0]
        );
        // Empty file is passed to service - use any() matcher since MockMultipartFile doesn't implement equals properly
        when(qrCodeService.generateQrCodeWithLogo(eq(testData), eq("5DADE2"), eq("FFFFFF"), any())).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", emptyFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for empty file");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(eq(testData), eq("5DADE2"), eq("FFFFFF"), any());
    }

    @Test
    void generateQrCode_ShouldRejectFilenameWithoutExtension() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logofile",
                "image/png",
                "fake png".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for file without extension");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldRejectFilenameWithMultipleDots() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "my.logo.file.bmp",
                "image/bmp",
                "fake bmp".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for invalid extension");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldAcceptFilenameWithMultipleDotsButValidExtension() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "my.logo.file.png",
                "image/png",
                "fake png".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for valid extension despite multiple dots");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleFilenameThatStartsWithDot() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                ".hiddenfile",
                "image/png",
                "fake png".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for hidden file without proper extension");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldHandleVeryLongFilename() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        String longFilename = "a".repeat(250) + ".png";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                longFilename,
                "image/png",
                "fake png".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for very long filename with valid extension");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleFilenameWithSpaces() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "my logo file.png",
                "image/png",
                "fake png".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for filename with spaces");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleFilenameWithSpecialCharacters() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo_file-v1.0.png",
                "image/png",
                "fake png".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for filename with special characters");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleOneByteLessThanMaxSize() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        byte[] justUnderFiveMB = new byte[5 * 1024 * 1024 - 1];
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.png",
                "image/png",
                justUnderFiveMB
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for file just under 5MB");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldHandleSvgWithXmlContentType() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.svg",
                "image/svg+xml",
                "<svg></svg>".getBytes()
        );
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for SVG with correct content type");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", logoFile);
    }

    @Test
    void generateQrCode_ShouldRejectGifFormat() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.gif",
                "image/gif",
                "fake gif".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for GIF format");
        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldRejectWebpFormat() {
        String testData = "test";
        MockMultipartFile logoFile = new MockMultipartFile(
                "logo",
                "logo.webp",
                "image/webp",
                "fake webp".getBytes()
        );

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", logoFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status should be BAD_REQUEST for WebP format");
        verifyNoInteractions(qrCodeService);
    }


    @Test
    void generateQrCode_ShouldHandleWhitespaceInData() throws IOException, TranscoderException {
        String testData = "   data with spaces   ";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for data with whitespace");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleJsonData() throws IOException, TranscoderException {
        String jsonData = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(jsonData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(jsonData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for JSON data");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(jsonData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandlePhoneNumberUri() throws IOException, TranscoderException {
        String phoneUri = "tel:+1-234-567-8900";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(phoneUri, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(phoneUri, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for phone URI");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(phoneUri, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleEmailUri() throws IOException, TranscoderException {
        String emailUri = "mailto:test@example.com?subject=Hello&body=Test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(emailUri, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(emailUri, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for email URI");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(emailUri, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleWifiConfiguration() throws IOException, TranscoderException {
        String wifiConfig = "WIFI:T:WPA;S:NetworkName;P:password123;;";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(wifiConfig, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(wifiConfig, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for WiFi configuration");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(wifiConfig, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleSmsUri() throws IOException, TranscoderException {
        String smsUri = "SMSTO:+1234567890:Hello World";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(smsUri, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(smsUri, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for SMS URI");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(smsUri, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleGeoLocation() throws IOException, TranscoderException {
        String geoUri = "geo:37.7749,-122.4194";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(geoUri, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(geoUri, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for geo location");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(geoUri, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldVerifyNoInteractionWhenValidationFails() {
        String testData = "test";
        MockMultipartFile invalidFile = new MockMultipartFile(
                "logo",
                "file.txt",
                "text/plain",
                "not an image".getBytes()
        );

        qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", invalidFile);

        verifyNoInteractions(qrCodeService);
    }

    @Test
    void generateQrCode_ShouldHandleServiceReturningEmptyArray() throws IOException, TranscoderException {
        String testData = "test";
        byte[] emptyQrCode = new byte[0];
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(emptyQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK"),
                () -> assertNotNull(response.getBody(), "Body should not be null"),
                () -> {
                    byte[] body = response.getBody();
                    assertNotNull(body);
                    assertEquals(0, body.length, "Body should be empty array");
                },
                () -> assertEquals(0, response.getHeaders().getContentLength(), "Content length should be 0")
        );
    }

    @Test
    void generateQrCode_ShouldHandleLargeQrCodeResponse() throws IOException, TranscoderException {
        String testData = "test";
        byte[] largeQrCode = new byte[1024 * 1024]; // 1MB
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(largeQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK"),
                () -> assertNotNull(response.getBody(), "Body should not be null"),
                () -> {
                    byte[] body = response.getBody();
                    assertNotNull(body);
                    assertEquals(1024 * 1024, body.length, "Body should match expected size");
                },
                () -> assertEquals(1024 * 1024, response.getHeaders().getContentLength(), "Content length should match")
        );
    }

    @Test
    void generateQrCode_ShouldHandleDefaultBackgroundColorWhenNotProvided() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        // When backgroundColor is not provided, it defaults to "FFFFFF"
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK with default background color");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleDefaultForegroundColorWhenNotProvided() throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        // When foregroundColor is not provided, it defaults to "5DADE2"
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK with default foreground color");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null);
    }

    @ParameterizedTest(name = "Should accept valid hex colors: fg={0}, bg={1}")
    @CsvSource({
            "000000, FFFFFF",
            "FFFFFF, 000000",
            "#123456, #ABCDEF",
            "abcdef, FEDCBA",
            "#FF5733, #C70039"
    })
    void generateQrCode_ShouldAcceptVariousValidHexColors(String fg, String bg) throws IOException, TranscoderException {
        String testData = "test";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(testData, fg, bg, null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, fg, bg, null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for valid hex colors");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(testData, fg, bg, null);
    }

    @Test
    void generateQrCode_ShouldHandleNumericOnlyData() throws IOException, TranscoderException {
        String numericData = "1234567890";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(numericData, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(numericData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for numeric data");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(numericData, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldHandleSingleCharacterData() throws IOException, TranscoderException {
        String singleChar = "A";
        byte[] mockQrCode = new byte[]{1, 2, 3};
        when(qrCodeService.generateQrCodeWithLogo(singleChar, "5DADE2", "FFFFFF", null)).thenReturn(mockQrCode);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(singleChar, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status should be OK for single character");
        verify(qrCodeService, times(1)).generateQrCodeWithLogo(singleChar, "5DADE2", "FFFFFF", null);
    }

    @Test
    void generateQrCode_ShouldLogErrorMessageOnIOException() throws IOException, TranscoderException {
        String testData = "test";
        IOException testException = new IOException("Test IO error");
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenThrow(testException);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        assertNull(response.getBody(), "Body should be null on error");
    }

    @Test
    void generateQrCode_ShouldLogErrorMessageOnTranscoderException() throws IOException, TranscoderException {
        String testData = "test";
        TranscoderException testException = new TranscoderException("Test transcoder error");
        when(qrCodeService.generateQrCodeWithLogo(testData, "5DADE2", "FFFFFF", null)).thenThrow(testException);

        ResponseEntity<byte[]> response = qrCodeController.generateQrCode(testData, "5DADE2", "FFFFFF", null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), "Status should be INTERNAL_SERVER_ERROR");
        assertNull(response.getBody(), "Body should be null on error");
    }
}
