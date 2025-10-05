# File Encryptor

A JavaFX desktop application that allows users to encrypt and decrypt files using AES encryption.

## Features

- Simple and intuitive user interface
- AES encryption for secure file protection
- File selection via browse button
- Progress indication during encryption/decryption
- Support for large files with streaming encryption

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building the Application

1. Clone this repository or download the source code
2. Navigate to the project root directory
3. Build the application using Maven:

```
mvn clean package
```

This will generate a JAR file in the `target` directory.

## Running the Application

You can run the application using one of the following methods:

### Using Maven

```
mvn javafx:run
```

### Using the JAR file

```
java -jar target/file-encryptor-1.0-SNAPSHOT.jar
```

## How to Use

1. Launch the application
2. Click "Browse" to select a file to encrypt or decrypt
3. Enter a secret key (at least 8 characters)
4. Click "Encrypt" to encrypt the file or "Decrypt" to decrypt the file
5. The status will be shown at the bottom of the window

## Security Notes

- The application uses AES encryption with PBKDF2 key derivation
- Keep your secret key safe - if lost, encrypted files cannot be recovered
- For maximum security, use long and complex secret keys

## License

This project is open source and available under the MIT License.