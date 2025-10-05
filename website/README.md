# File Encryptor Web Application

A secure web application for encrypting and decrypting files using AES encryption with a modern glassmorphism UI design.

## Features

- Upload and encrypt files using AES-256 encryption
- Decrypt previously encrypted files
- Modern frosted glass UI with dark theme
- Responsive design for all screen sizes
- Secure password-based encryption using PBKDF2
- Automatic file download after processing

## Technologies Used

- **Backend**: Java Spring Boot
- **Frontend**: HTML, CSS, JavaScript
- **Encryption**: AES encryption using javax.crypto
- **UI Design**: Modern glassmorphism with CSS

## Requirements

- Java Development Kit (JDK) 11 or higher
- Maven 3.6.0 or higher

## Building the Application

To build the application, navigate to the project directory and run:

```bash
mvn clean package
```

This will create a JAR file in the `target` directory.

## Running the Application

You can run the application with the following command:

```bash
java -jar target/file-encryptor-web-0.0.1-SNAPSHOT.jar
```

Alternatively, you can use Maven to run the application directly:

```bash
mvn spring-boot:run
```

The application will be accessible at `http://localhost:8080`.

## Usage Instructions

1. Open the application in your web browser at `http://localhost:8080`
2. Choose either "Encrypt" or "Decrypt" tab based on your needs
3. Click on the file input area to select a file
4. Enter a secret key (must be at least 8 characters long)
5. Click the "Encrypt File" or "Decrypt File" button
6. The processed file will be automatically downloaded

## Security Notes

- The application uses AES encryption in CBC mode with PKCS5 padding
- A secure key derivation function (PBKDF2) is used to generate encryption keys
- Files are processed securely in memory
- No files or encryption keys are stored on the server
- It's recommended to use a strong, unique secret key for each file
- Always keep your secret keys secure - if lost, encrypted files cannot be recovered

## File Size Limitations

The application is configured to accept files up to 10MB in size. This can be modified in the `application.properties` file if needed.

## License

This project is open source and available under the MIT License.