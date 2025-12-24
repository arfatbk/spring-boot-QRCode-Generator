# Beautiful QR Code Generator

#### A modern, web-based QR code generator with custom logo overlay support. Built with Spring Boot and featuring a beautiful, responsive UI.

---

![sample.PNG](docs/sample.PNG)

---

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

Replace `src/main/resources/logo.svg` with your own SVG logo file. The logo will be automatically overlaid in the center of generated QR codes.

**Logo Recommendations**:
- Use SVG format for best quality
- Ensure transparent background (no background rectangles in SVG)
- Keep the design simple and recognizable
- Use high contrast colors
- Optimize viewBox to fit actual logo content
- Recommended display size: 60x60 pixels (configurable)

**Logo Styling** (automatically applied):
- White rounded background with 8px padding
- 2px black border for definition
- Subtle drop shadow for depth
- 12px corner radius for modern look
- Automatically centered on QR code

## Testing

Run all tests:

```bash
mvn test
```

Run tests with coverage:

```bash
mvn clean test jacoco:report
```

**Test Coverage**:
- **Service Tests**: 20 tests covering all business logic
  - Parameterized tests for color validation
  - Edge cases (null, empty, invalid formats)
  - Multiple data formats (URLs, email, phone, WiFi, JSON, Unicode)
- **Controller Tests**: 11 tests validating HTTP layer
  - Mock-based unit tests
  - Exception handling verification
  - Response validation

**Total**: 31 comprehensive tests

## Best Practices Implemented

- **SOLID Principles**: Clean separation of concerns with single responsibility
- **Thread-Safe Design**: Immutable service fields, stateless request handling
- **Dependency Injection**: Constructor-based injection throughout
- **Proper Logging**: SLF4J with contextual information (data length, colors)
- **Input Validation**: Comprehensive validation with descriptive error messages
- **Exception Handling**: Proper error handling with specific exceptions
- **Parameterized Testing**: JUnit 5 parameterized tests for better maintainability
- **Code Quality**: Following Java coding standards and best practices
- **Documentation**: Javadoc and inline comments for complex logic
- **Configuration Management**: Externalized configuration with sensible defaults
- **No Code Duplication**: DRY principle applied throughout

## Features Roadmap

### ✅ Completed
- [x] Customizable colors (foreground and background)
- [x] Color presets with visual preview
- [x] Smart color rendering (black corners, custom data)
- [x] Transparent logo backgrounds
- [x] Thread-safe service implementation
- [x] Comprehensive test coverage
- [x] Input validation with clear error messages

### 🚀 Future Enhancements
- [ ] Multiple QR code sizes
- [ ] Batch QR code generation
- [ ] QR code with custom shapes (rounded modules)
- [ ] Analytics and tracking
- [ ] User accounts and saved QR codes
- [ ] API rate limiting
- [ ] PNG/JPG logo support (in addition to SVG)
- [ ] Vector output formats (SVG, PDF)
- [ ] QR code templates
- [ ] Bulk upload from CSV

## Recent Changes

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Nayuki QR Code Generator](https://github.com/nayuki/QR-Code-generator) for excellent QR code generation library
- [Apache Batik](https://xmlgraphics.apache.org/batik/) for SVG processing
- [Tailwind CSS](https://tailwindcss.com/) for beautiful styling
- [Spring Boot](https://spring.io/projects/spring-boot) for the robust framework

## Support

For issues, questions, or contributions, please open an issue on GitHub.

---

Made with ❤️ using Spring Boot 4 and Java 25

