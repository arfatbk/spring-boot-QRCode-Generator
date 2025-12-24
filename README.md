# Beautiful QR Code Generator

#### A modern, web-based QR code generator with custom logo overlay support. Built with Spring Boot and featuring a beautiful, responsive UI.

---

![sample.PNG](docs/sample.PNG)

---

## Features

- **Beautiful UI**: Modern, gradient-based design with smooth animations
- **Custom Logo Overlay**: Upload your own logo or use the default logo
- **Multiple Logo Formats**: Support for SVG, PNG, and JPEG logos
- **Color Customization**: Choose custom foreground and background colors with live preview
- **Color Presets**: Quick access to popular color combinations
- **High Error Correction**: Uses high-level error correction to ensure QR codes work even if partially damaged
- **Instant Generation**: Fast QR code generation with real-time preview
- **Download Support**: One-click download of generated QR codes
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **RESTful API**: Clean REST API for programmatic QR code generation
- **Spring Validation**: Input validation for all parameters

## Technology Stack

- **Backend**: Spring Boot 4.0.1, Java 25
- **QR Code Library**: [Nayuki QR Code Generator](https://github.com/nayuki/QR-Code-generator) v1.8.0
- **SVG Processing**: Apache Batik 1.19
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

**Content-Type**: `multipart/form-data`

**Parameters**:
- `data` (required): The text or URL to encode in the QR code
- `foregroundColor` (optional): Hex color code for QR code data modules (default: `5DADE2`)
- `backgroundColor` (optional): Hex color code for QR code background (default: `FFFFFF`)
- `logo` (optional): Logo image file (SVG, PNG, or JPEG, max 5MB)

**Response**: PNG image (binary)

**Example using cURL**:

```bash
# Basic QR code with default colors and logo
curl -X POST "http://localhost:8080/generate" \
  -F "data=https://github.com" \
  --output qrcode.png

# QR code with custom colors
curl -X POST "http://localhost:8080/generate" \
  -F "data=https://github.com" \
  -F "foregroundColor=FF0000" \
  -F "backgroundColor=FFFFFF" \
  --output qrcode.png

# QR code with custom logo
curl -X POST "http://localhost:8080/generate" \
  -F "data=https://github.com" \
  -F "logo=@/path/to/your/logo.png" \
  --output qrcode.png

# QR code with custom colors and logo
curl -X POST "http://localhost:8080/generate" \
  -F "data=https://github.com" \
  -F "foregroundColor=5DADE2" \
  -F "backgroundColor=FFFFFF" \
  -F "logo=@/path/to/your/logo.svg" \
  --output qrcode.png
```

**Example using JavaScript**:

```javascript
const formData = new FormData();
formData.append('data', 'https://example.com');
formData.append('foregroundColor', '5DADE2');
formData.append('backgroundColor', 'FFFFFF');

// Optional: Add custom logo
const logoFile = document.getElementById('logoInput').files[0];
if (logoFile) {
    formData.append('logo', logoFile);
}

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

You have two options for customizing the logo:

### Option 1: Upload Logo via UI
Use the web interface to upload a custom logo for each QR code generation:
- Click "Choose file..." in the Custom Logo section
- Select your logo file (SVG, PNG, or JPEG)
- Maximum file size: 5MB
- The logo will be automatically resized and centered

### Option 2: Replace Default Logo
Replace `src/main/resources/logo.svg` with your own logo file to use as the default:
- This logo will be used when no custom logo is uploaded
- Supports SVG format for best quality
- PNG and JPEG can also be used

**Logo Recommendations**:
- Use SVG format for best quality and scalability
- PNG or JPEG with transparent background recommended
- Keep the design simple and recognizable
- Use high contrast colors for visibility
- Square or near-square aspect ratio works best
- Recommended size: 60x60 pixels (automatically resized)
- Maximum upload size: 5MB

**Logo Styling** (automatically applied):
- White rounded background with 8px padding
- 2px black border for definition
- Subtle drop shadow for depth
- 12px corner radius for modern look
- Automatically centered on QR code
- Maintains aspect ratio when resizing

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
- **Service Tests**: 43 tests covering all business logic
  - Parameterized tests for color validation
  - Edge cases (null, empty, invalid formats)
  - Multiple data formats (URLs, email, phone, WiFi, JSON, Unicode)
  - Custom logo upload scenarios (SVG, PNG, JPEG)
  - File validation tests
- **Controller Tests**: 66 tests validating HTTP layer
  - Mock-based unit tests
  - Exception handling verification
  - Response validation
  - Logo upload validation
  - File size and type validation

**Total**: 109 comprehensive tests

## Best Practices Implemented

- **SOLID Principles**: Clean separation of concerns with single responsibility
- **Thread-Safe Design**: Immutable service fields, stateless request handling
- **Dependency Injection**: Constructor-based injection throughout
- **Proper Logging**: SLF4J with contextual information (data length, colors, logo status)
- **Input Validation**: Comprehensive validation with Spring Validation framework
- **Exception Handling**: Proper error handling with specific exceptions and appropriate HTTP status codes
- **Parameterized Testing**: JUnit 5 parameterized tests for better maintainability
- **Code Quality**: Following Java coding standards and best practices
- **Documentation**: Javadoc and inline comments for complex logic
- **Configuration Management**: Externalized configuration with sensible defaults
- **No Code Duplication**: DRY principle applied throughout
- **File Upload Security**: File size and type validation

## Features Roadmap

### ✅ Completed
- [x] Customizable colors (foreground and background)
- [x] Color presets with visual preview
- [x] Smart color rendering (black corners, custom data)
- [x] Transparent logo backgrounds
- [x] Thread-safe service implementation
- [x] Comprehensive test coverage
- [x] Input validation with clear error messages
- [x] Custom logo upload (SVG, PNG, JPEG)
- [x] File validation (size, type, format)
- [x] Logo preview in UI
- [x] Spring Validation integration

### 🚀 Future Enhancements
- [ ] Multiple QR code sizes
- [ ] Batch QR code generation
- [ ] QR code with custom shapes (rounded modules)
- [ ] Analytics and tracking
- [ ] User accounts and saved QR codes
- [ ] API rate limiting
- [ ] Vector output formats (SVG, PDF)
- [ ] QR code templates
- [ ] Bulk upload from CSV
- [ ] Logo library/gallery
- [ ] QR code history

## Recent Changes

### Version 1.1.0 (Latest)
- ✨ Added custom logo upload feature
- ✨ Support for multiple logo formats (SVG, PNG, JPEG)
- ✨ Added Spring Validation for input parameters
- ✨ Logo file size and type validation (max 5MB)
- ✨ Logo preview in UI
- ✨ Improved error handling with appropriate HTTP status codes
- 📝 Updated API documentation
- ✅ Added 11 new tests for logo upload functionality
- 🎨 Enhanced UI with logo upload field

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

