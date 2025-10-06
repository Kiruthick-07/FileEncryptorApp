package com.fileencryptor.web.controller;

import com.fileencryptor.web.service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling file encryption and decryption requests.
 */
@Controller
public class FileEncryptorController {

    private final EncryptionService encryptionService;

    @Autowired
    public FileEncryptorController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/encrypt")
    @ResponseBody
    public ResponseEntity<?> encryptFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("secretKey") String secretKey) {
        try {
            if (file.isEmpty()) {
                return createErrorResponse("Please select a file to encrypt");
            }
            
            if (secretKey.isEmpty() || secretKey.length() < 8) {
                return createErrorResponse("Secret key must be at least 8 characters long");
            }

            byte[] encryptedContent = encryptionService.encryptFile(file, secretKey);
            
            String originalFilename = file.getOriginalFilename();
            String encryptedFilename = originalFilename != null ? originalFilename + ".encrypted" : "encrypted_file";
            
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encryptedFilename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(encryptedContent);
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse("Encryption failed: " + e.getMessage());
        }
    }

    @PostMapping("/decrypt")
    @ResponseBody
    public ResponseEntity<?> decryptFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("secretKey") String secretKey) {
        try {
            if (file.isEmpty()) {
                return createErrorResponse("Please select a file to decrypt");
            }
            
            if (secretKey.isEmpty() || secretKey.length() < 8) {
                return createErrorResponse("Secret key must be at least 8 characters long");
            }
            
            // Check file extension - optional warning
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && !originalFilename.endsWith(".encrypted")) {
                // This is just a warning log - we still attempt to decrypt
                System.out.println("Warning: File doesn't have .encrypted extension: " + originalFilename);
            }

            byte[] decryptedContent = encryptionService.decryptFile(file.getBytes(), secretKey);
            
            String decryptedFilename;
            
            if (originalFilename != null && originalFilename.endsWith(".encrypted")) {
                decryptedFilename = originalFilename.substring(0, originalFilename.length() - 10);
            } else {
                decryptedFilename = originalFilename != null ? originalFilename + ".decrypted" : "decrypted_file";
            }
            
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + decryptedFilename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(decryptedContent);
        } catch (SecurityException e) {
            // This catches our specific security exceptions with user-friendly messages
            return createErrorResponse(e.getMessage());
        } catch (IllegalArgumentException e) {
            // This catches validation errors
            return createErrorResponse(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // For other exceptions, provide a more generic message
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("padding")) {
                return createErrorResponse("Decryption failed: The file could not be decrypted with the provided key. Please check your secret key and ensure you're decrypting a valid encrypted file.");
            } else {
                return createErrorResponse("Decryption failed: " + errorMsg);
            }
        }
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}