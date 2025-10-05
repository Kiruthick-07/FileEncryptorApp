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

            byte[] decryptedContent = encryptionService.decryptFile(file.getBytes(), secretKey);
            
            String originalFilename = file.getOriginalFilename();
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
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse("Decryption failed: " + e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}