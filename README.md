# Beautiful QR Code Generator

A modern, web-based QR code generator with custom logo overlay support. Built with Spring Boot and featuring a beautiful, responsive UI.

## Features

- **Beautiful UI**: Modern, gradient-based design with smooth animations
- **Custom Logo Overlay**: Automatically adds your logo to the center of QR codes
- **High Error Correction**: Uses high-level error correction to ensure QR codes work even if partially damaged
- **Instant Generation**: Fast QR code generation with real-time preview
- **Download Support**: One-click download of generated QR codes
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **SVG Logo Support**: Supports SVG logo files for crisp, scalable branding
- **RESTful API**: Clean REST API for programmatic QR code generation

## Technology Stack

- **Backend**: Spring Boot 4.0.1, Java 25
- **QR Code Library**: ZXing (Zebra Crossing)
- **SVG Processing**: Apache Batik
- **Frontend**: Thymeleaf, Tailwind CSS
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Prerequisites

- Java 25 or higher
- Maven 3.6 or higher

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd qr
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file directly:

```bash
java -jar target/qr-0.0.1-SNAPSHOT.jar
```

### 4. Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

## Configuration

You can customize the QR code generation in `application.properties`:

```properties
# QR Code configuration
qr.size=400              # Size of QR code in pixels
qr.logo-size=80          # Size of logo overlay in pixels
qr.logo-border=4         # Border around logo in pixels
qr.logo-path=classpath:logo.svg  # Path to logo file
```

## API Usage

### Generate QR Code

**Endpoint**: `POST /generate`

**Parameters**:
- `data` (required): The text or URL to encode in the QR code

**Response**: PNG image (binary)

**Example using cURL**:

```bash
curl -X POST "http://localhost:8080/generate" \
  -F "data=https://github.com" \
  --output qrcode.png
```

**Example using JavaScript**:

```javascript
const formData = new FormData();
formData.append('data', 'https://example.com');

fetch('/generate', {
    method: 'POST',
    body: formData
})
.then(response => response.blob())
.then(blob => {
    const url = URL.createObjectURL(blob);
    // Use the URL to display or download the image
});
```

## Project Structure

```
qr/
├── src/
│   ├── main/
│   │   ├── java/com/example/qr/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── model/          # Data models
│   │   │   ├── service/        # Business logic
│   │   │   └── QrApplication.java
│   │   └── resources/
│   │       ├── static/         # Static resources
│   │       ├── templates/      # Thymeleaf templates
│   │       ├── logo.svg       # Custom logo
│   │       └── application.properties
│   └── test/
│       └── java/com/example/qr/
│           ├── controller/     # Controller tests
│           └── service/       # Service tests
├── pom.xml
└── README.md
```

## Customizing the Logo

Replace `src/main/resources/logo.svg` with your own SVG logo file. The logo will be automatically overlaid on the center of generated QR codes with a white border for better visibility.

**Logo Recommendations**:
- Use SVG format for best quality
- Keep the design simple and recognizable
- Use high contrast colors
- Recommended size: 80x80 pixels (configurable)

## Testing

Run all tests:

```bash
mvn test
```

Run tests with coverage:

```bash
mvn clean test jacoco:report
```

## Best Practices Implemented

- **SOLID Principles**: Clean separation of concerns
- **Dependency Injection**: Constructor-based injection
- **Proper Logging**: SLF4J for consistent logging
- **Exception Handling**: Proper error handling and user feedback
- **Unit Testing**: Comprehensive test coverage
- **Code Quality**: Following Java coding standards
- **Documentation**: Javadoc and inline comments
- **Configuration Management**: Externalized configuration

## Features Roadmap

- [ ] Customizable colors (foreground and background)
- [ ] Multiple QR code sizes
- [ ] Batch QR code generation
- [ ] QR code with custom shapes
- [ ] Analytics and tracking
- [ ] User accounts and saved QR codes
- [ ] API rate limiting
- [ ] PNG logo support
- [ ] Vector output formats (SVG, PDF)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [ZXing](https://github.com/zxing/zxing) for QR code generation
- [Apache Batik](https://xmlgraphics.apache.org/batik/) for SVG processing
- [Tailwind CSS](https://tailwindcss.com/) for beautiful styling
- [Spring Boot](https://spring.io/projects/spring-boot) for the robust framework

## Support

For issues, questions, or contributions, please open an issue on GitHub.

---

Made with ❤️ using Spring Boot

