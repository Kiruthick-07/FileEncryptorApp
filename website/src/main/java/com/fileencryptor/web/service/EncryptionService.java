package com.fileencryptor.web.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * Service for encrypting and decrypting files using AES.
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final byte[] SALT = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    /**
     * Encrypts a file using AES encryption.
     *
     * @param file      the file to encrypt
     * @param secretKey the secret key for encryption
     * @return encrypted file bytes
     * @throws Exception if encryption fails
     */
    public byte[] encryptFile(MultipartFile file, String secretKey) throws Exception {
        // Generate key from password
        SecretKey key = generateKey(secretKey);

        // Generate IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Initialize cipher with IV
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

        // Process the file
        byte[] fileContent = file.getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Write the IV to the beginning of the output
        outputStream.write(iv);

        // Encrypt and write data
        byte[] encryptedContent = cipher.doFinal(fileContent);
        outputStream.write(encryptedContent);

        return outputStream.toByteArray();
    }

    /**
     * Decrypts a file using AES decryption.
     *
     * @param encryptedFileContent encrypted file content
     * @param secretKey           the secret key for decryption
     * @return decrypted file bytes
     * @throws Exception if decryption fails
     */
    public byte[] decryptFile(byte[] encryptedFileContent, String secretKey) throws Exception {
        // Generate key from password
        SecretKey key = generateKey(secretKey);

        // Read IV from the beginning of the file
        byte[] iv = new byte[16];
        System.arraycopy(encryptedFileContent, 0, iv, 0, 16);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Initialize cipher for decryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        // Process the file content (skipping the IV)
        byte[] encryptedContent = new byte[encryptedFileContent.length - 16];
        System.arraycopy(encryptedFileContent, 16, encryptedContent, 0, encryptedContent.length);

        // Decrypt
        return cipher.doFinal(encryptedContent);
    }

    /**
     * Generates an AES key from the provided secret key string.
     *
     * @param secretKey the secret key as a string
     * @return the generated SecretKey
     * @throws NoSuchAlgorithmException if the algorithm is not available
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    private SecretKey generateKey(String secretKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            // PBKDF2 key derivation
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
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