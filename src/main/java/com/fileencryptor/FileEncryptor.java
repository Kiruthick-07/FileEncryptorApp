package com.fileencryptor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * Utility class for file encryption and decryption using AES.
 */
public class FileEncryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding"; // Changed to CBC mode with padding
    private static final int BUFFER_SIZE = 8192; // 8KB buffer size
    private static final byte[] SALT = {
        (byte)0x43, (byte)0x76, (byte)0x95, (byte)0xc7,
        (byte)0x5b, (byte)0xd7, (byte)0x45, (byte)0x17 
    };

    /**
     * Encrypts a file using AES encryption.
     *
     * @param inputFilePath path to the input file to encrypt
     * @param outputFilePath path to save the encrypted file
     * @param secretKey the secret key for encryption
     * @throws Exception if encryption fails
     */
    public static void encrypt(String inputFilePath, String outputFilePath, String secretKey) throws Exception {
        doEncryptionDecryption(Cipher.ENCRYPT_MODE, inputFilePath, outputFilePath, secretKey);
    }

    /**
     * Decrypts a file using AES encryption.
     *
     * @param inputFilePath path to the encrypted file
     * @param outputFilePath path to save the decrypted file
     * @param secretKey the secret key for decryption
     * @throws Exception if decryption fails
     */
    public static void decrypt(String inputFilePath, String outputFilePath, String secretKey) throws Exception {
        doEncryptionDecryption(Cipher.DECRYPT_MODE, inputFilePath, outputFilePath, secretKey);
    }

    /**
     * Performs the encryption or decryption operation.
     *
     * @param cipherMode Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @param inputFilePath path to the input file
     * @param outputFilePath path to save the output file
     * @param secretKey the secret key
     * @throws Exception if operation fails
     */
    private static void doEncryptionDecryption(int cipherMode, String inputFilePath, 
            String outputFilePath, String secretKey) throws Exception {
        
        SecretKey key = generateKey(secretKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        
        if (cipherMode == Cipher.ENCRYPT_MODE) {
            // For encryption: generate a random IV
            byte[] iv = new byte[16]; // 16 bytes for AES
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            
            // Initialize cipher with IV for encryption
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            
            try (FileInputStream inputStream = new FileInputStream(inputFilePath);
                 FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
                
                // Write the IV to the beginning of the output file
                outputStream.write(iv);
                
                byte[] inputBuffer = new byte[BUFFER_SIZE];
                int bytesRead;
                
                while ((bytesRead = inputStream.read(inputBuffer)) != -1) {
                    byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                    if (outputBuffer != null) {
                        outputStream.write(outputBuffer);
                    }
                }
                
                byte[] finalBuffer = cipher.doFinal();
                if (finalBuffer != null) {
                    outputStream.write(finalBuffer);
                }
            }
        } else {
            // For decryption: read the IV from the beginning of the file
            try (FileInputStream inputStream = new FileInputStream(inputFilePath);
                 FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
                
                // Read the 16-byte IV from the beginning of the file
                byte[] iv = new byte[16];
                int ivBytesRead = inputStream.read(iv);
                
                if (ivBytesRead != 16) {
                    throw new IllegalArgumentException("Input file does not contain a valid IV. It might not be encrypted properly or is corrupted.");
                }
                
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                
                // Initialize cipher with IV for decryption
                cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
                
                byte[] inputBuffer = new byte[BUFFER_SIZE];
                int bytesRead;
                
                try {
                    while ((bytesRead = inputStream.read(inputBuffer)) != -1) {
                        byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                        if (outputBuffer != null) {
                            outputStream.write(outputBuffer);
                        }
                    }
                    
                    byte[] finalBuffer = cipher.doFinal();
                    if (finalBuffer != null) {
                        outputStream.write(finalBuffer);
                    }
                } catch (Exception e) {
                    throw new Exception("Decryption failed: " + e.getMessage() + ". Make sure the file is properly encrypted and you are using the correct key.", e);
                }
            }
        }
    }

    /**
     * Generates an AES key from the provided secret key string.
     * Uses PBKDF2 for secure key derivation.
     *
     * @param secretKey the secret key as a string
     * @return the generated SecretKey
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws InvalidKeySpecException if the key specification is invalid
     */
    private static SecretKey generateKey(String secretKey) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        try {
            // PBKDF2 (Password-Based Key Derivation Function 2) to generate a secure key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            // Using 65536 iterations and 256-bit key length for AES-256
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), SALT, 65536, 256);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            
            return new SecretKeySpec(keyBytes, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("The encryption algorithm is not available: " + e.getMessage());
        } catch (InvalidKeySpecException e) {
            throw new InvalidKeySpecException("Invalid key specification: " + e.getMessage());
        }
    }
}