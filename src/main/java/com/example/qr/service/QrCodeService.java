package com.example.qr.service;

import io.nayuki.qrcodegen.QrCode;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class QrCodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeService.class);
    private static final int QR_CODE_SIZE = 400;
    private static final int LOGO_SIZE = 60;
    private static final int LOGO_BORDER = 8;

    private final Resource defaultLogoResource;
    private final int defaultForegroundColor;
    private final int defaultBackgroundColor;

    public QrCodeService(
            @Value("classpath:logo.svg") Resource defaultLogoResource,
            @Value("${qr.foreground-color:000000}") String foregroundColorHex,
            @Value("${qr.background-color:FFFFFF}") String backgroundColorHex) {
        this.defaultLogoResource = defaultLogoResource;
        this.defaultForegroundColor = parseHexColor(foregroundColorHex);
        this.defaultBackgroundColor = parseHexColor(backgroundColorHex);
    }

    public byte[] generateQrCodeWithLogo(String data) throws IOException, TranscoderException {
        String fgHex = String.format("%06X", defaultForegroundColor & 0xFFFFFF);
        String bgHex = String.format("%06X", defaultBackgroundColor & 0xFFFFFF);
        return generateQrCodeWithLogo(data, fgHex, bgHex, null);
    }

    public byte[] generateQrCodeWithLogo(String data, String foregroundColorHex, String backgroundColorHex,
                                          MultipartFile customLogo)
            throws IOException, TranscoderException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("QR code data cannot be null or empty");
        }

        // Validate and parse colors
        int fgColor = parseHexColor(foregroundColorHex);
        int bgColor = parseHexColor(backgroundColorHex);

        LOGGER.debug("Generating QR code for data: {}, fg={}, bg={}, customLogo={}",
                     data, foregroundColorHex, backgroundColorHex, customLogo != null && !customLogo.isEmpty());

        // Generate QR code using Nayuki library with high error correction
        QrCode qrCode = QrCode.encodeText(data, QrCode.Ecc.HIGH);

        // Convert QR code to BufferedImage
        int qrSize = qrCode.size;
        int scale = QR_CODE_SIZE / qrSize;
        int border = 1;
        BufferedImage qrImage = toBufferedImage(qrCode, scale, border, fgColor, bgColor);

        // Load logo (custom or default)
        BufferedImage logo = loadLogo(customLogo);
        BufferedImage finalImage = overlayLogo(qrImage, logo);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "png", baos);
        LOGGER.debug("QR code generated successfully, size: {} bytes", baos.size());
        return baos.toByteArray();
    }

    private BufferedImage loadLogo(MultipartFile customLogo) throws IOException, TranscoderException {
        if (customLogo != null && !customLogo.isEmpty()) {
            return loadCustomLogo(customLogo);
        }
        return convertSvgToPng(defaultLogoResource);
    }

    private BufferedImage loadCustomLogo(MultipartFile logoFile) throws IOException, TranscoderException {
        String contentType = logoFile.getContentType();

        if ("image/svg+xml".equals(contentType)) {
            return convertSvgToPng(logoFile.getInputStream());
        } else if ("image/png".equals(contentType) || "image/jpeg".equals(contentType)) {
            BufferedImage originalLogo = ImageIO.read(logoFile.getInputStream());
            if (originalLogo == null) {
                throw new IOException("Failed to read logo image");
            }
            return resizeLogo(originalLogo);
        } else {
            throw new IllegalArgumentException("Unsupported logo format: " + contentType);
        }
    }

    private BufferedImage resizeLogo(BufferedImage original) {
        // Calculate dimensions to maintain aspect ratio
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth = LOGO_SIZE;
        int newHeight = LOGO_SIZE;

        if (aspectRatio > 1) {
            newHeight = (int) (LOGO_SIZE / aspectRatio);
        } else {
            newWidth = (int) (LOGO_SIZE * aspectRatio);
        }

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();

        // High-quality rendering
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resized;
    }

    private int parseHexColor(String hexColor) {
        if (hexColor == null || hexColor.isEmpty()) {
            throw new IllegalArgumentException("Color cannot be null or empty");
        }

        // Remove # if present
        String cleanHex = hexColor.startsWith("#") ? hexColor.substring(1) : hexColor;

        // Validate hex format
        if (!cleanHex.matches("[0-9A-Fa-f]{6}")) {
            throw new IllegalArgumentException("Invalid hex color format: " + hexColor + ". Expected format: RRGGBB or #RRGGBB");
        }

        return 0xFF000000 | Integer.parseInt(cleanHex, 16);
    }

    private BufferedImage toBufferedImage(QrCode qr, int scale, int border, int foregroundColor, int backgroundColor) {
        int size = (qr.size + border * 2) * scale;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Fill background
        g.setColor(new Color(backgroundColor));
        g.fillRect(0, 0, size, size);

        // Draw QR code modules
        for (int y = 0; y < qr.size; y++) {
            for (int x = 0; x < qr.size; x++) {
                if (qr.getModule(x, y)) {
                    // Check if this module is part of a position detection pattern (corner squares)
                    // Position patterns are 7x7 squares at (0,0), (size-7,0), and (0,size-7)
                    boolean isPositionPattern = isInPositionPattern(x, y, qr.size);

                    // Use black for position patterns, custom color for data
                    if (isPositionPattern) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(new Color(foregroundColor));
                    }

                    g.fillRect((x + border) * scale, (y + border) * scale, scale, scale);
                }
            }
        }

        g.dispose();
        return image;
    }

    private boolean isInPositionPattern(int x, int y, int size) {
        // Top-left position pattern (0,0 to 6,6)
        if (x < 7 && y < 7) {
            return true;
        }
        // Top-right position pattern (size-7,0 to size-1,6)
        if (x >= size - 7 && y < 7) {
            return true;
        }
        // Bottom-left position pattern (0,size-7 to 6,size-1)
        return x < 7 && y >= size - 7;
    }


    private BufferedImage convertSvgToPng(Resource svgResource) throws IOException, TranscoderException {
        return convertSvgToPng(svgResource.getInputStream());
    }

    private BufferedImage convertSvgToPng(InputStream svgInputStream) throws IOException, TranscoderException {
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) LOGO_SIZE);
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) LOGO_SIZE);

        TranscoderInput input = new TranscoderInput(svgInputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outputStream);

        transcoder.transcode(input, output);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return ImageIO.read(inputStream);
    }

    private BufferedImage overlayLogo(BufferedImage qrImage, BufferedImage logo) {
        int deltaHeight = qrImage.getHeight() - logo.getHeight();
        int deltaWidth = qrImage.getWidth() - logo.getWidth();

        BufferedImage combined = new BufferedImage(qrImage.getWidth(), qrImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        // Enable high-quality rendering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.drawImage(qrImage, 0, 0, null);

        int logoX = deltaWidth / 2;
        int logoY = deltaHeight / 2;
        int backgroundWidth = logo.getWidth() + (LOGO_BORDER * 2);
        int backgroundHeight = logo.getHeight() + (LOGO_BORDER * 2);
        int cornerRadius = 12;

        // Draw subtle shadow for depth
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRoundRect(
                logoX - LOGO_BORDER + 2,
                logoY - LOGO_BORDER + 2,
                backgroundWidth,
                backgroundHeight,
                cornerRadius,
                cornerRadius
        );

        // Draw white background
        g.setColor(Color.WHITE);
        g.fillRoundRect(
                logoX - LOGO_BORDER,
                logoY - LOGO_BORDER,
                backgroundWidth,
                backgroundHeight,
                cornerRadius,
                cornerRadius
        );

        // Draw black border around logo
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(
                logoX - LOGO_BORDER,
                logoY - LOGO_BORDER,
                backgroundWidth,
                backgroundHeight,
                cornerRadius,
                cornerRadius
        );

        // Draw logo
        g.drawImage(logo, logoX, logoY, null);
        g.dispose();

        return combined;
    }
}
