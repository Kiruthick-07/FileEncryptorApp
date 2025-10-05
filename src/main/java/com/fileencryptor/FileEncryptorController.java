package com.fileencryptor;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Controller class for the File Encryptor application.
 * Handles user interactions and connects the UI with the encryption logic.
 */
public class FileEncryptorController {

    @FXML private TextField filePathField;
    @FXML private PasswordField secretKeyField;
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    private File selectedFile;

    /**
     * Handles the browse button click event.
     * Opens a file chooser dialog to select a file for encryption or decryption.
     */
    @FXML
    private void handleBrowseButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        
        // Set initial directory to user's home directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        // Get the window from any control in the UI
        Stage stage = (Stage) filePathField.getScene().getWindow();
        
        // Show the file chooser dialog
        selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            filePathField.setText(selectedFile.getAbsolutePath());
            statusLabel.setText("File selected: " + selectedFile.getName());
        }
    }

    /**
     * Handles the encrypt button click event.
     * Encrypts the selected file using the entered secret key.
     */
    @FXML
    private void handleEncryptButton() {
        if (!validateInputs()) {
            return;
        }
        
        String secretKey = secretKeyField.getText();
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateProgress(0.1, 1.0);
                    updateMessage("Starting encryption...");
                    
                    // Create output file path by adding .encrypted extension
                    File outputFile = new File(selectedFile.getAbsolutePath() + ".encrypted");
                    
                    // Perform encryption
                    FileEncryptor.encrypt(selectedFile.getAbsolutePath(), 
                                          outputFile.getAbsolutePath(), 
                                          secretKey);
                    
                    updateProgress(1.0, 1.0);
                    updateMessage("File encrypted successfully: " + outputFile.getName());
                } catch (Exception e) {
                    updateProgress(0, 1.0);
                    updateMessage("Error: " + e.getMessage());
                    throw e;
                }
                return null;
            }
        };
        
        // Bind UI components to task properties
        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
        
        // Handle task completion
        task.setOnSucceeded(e -> {
            // Unbind properties
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
        });
        
        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            showErrorAlert("Encryption Failed", task.getException().getMessage());
        });
        
        // Run the task in a background thread
        new Thread(task).start();
    }

    /**
     * Handles the decrypt button click event.
     * Decrypts the selected file using the entered secret key.
     */
    @FXML
    private void handleDecryptButton() {
        if (!validateInputs()) {
            return;
        }
        
        String secretKey = secretKeyField.getText();
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    updateProgress(0.1, 1.0);
                    updateMessage("Starting decryption...");
                    
                    String outputPath = selectedFile.getAbsolutePath();
                    // If file ends with .encrypted, remove it for the output file
                    if (outputPath.endsWith(".encrypted")) {
                        outputPath = outputPath.substring(0, outputPath.length() - ".encrypted".length());
                    } else {
                        // Otherwise add .decrypted suffix
                        outputPath = outputPath + ".decrypted";
                    }
                    
                    File outputFile = new File(outputPath);
                    
                    // Perform decryption
                    FileEncryptor.decrypt(selectedFile.getAbsolutePath(), 
                                          outputFile.getAbsolutePath(), 
                                          secretKey);
                    
                    updateProgress(1.0, 1.0);
                    updateMessage("File decrypted successfully: " + outputFile.getName());
                } catch (Exception e) {
                    updateProgress(0, 1.0);
                    updateMessage("Error: " + e.getMessage());
                    throw e;
                }
                return null;
            }
        };
        
        // Bind UI components to task properties
        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());
        
        // Handle task completion
        task.setOnSucceeded(e -> {
            // Unbind properties
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
        });
        
        task.setOnFailed(e -> {
            progressBar.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            showErrorAlert("Decryption Failed", task.getException().getMessage());
        });
        
        // Run the task in a background thread
        new Thread(task).start();
    }

    /**
     * Validates user inputs before encryption or decryption.
     * 
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        if (selectedFile == null || !selectedFile.exists()) {
            showErrorAlert("File Error", "Please select a valid file first.");
            return false;
        }
        
        if (secretKeyField.getText().trim().isEmpty()) {
            showErrorAlert("Key Error", "Please enter a secret key.");
            return false;
        }
        
        if (secretKeyField.getText().length() < 8) {
            showErrorAlert("Key Error", "Secret key must be at least 8 characters long.");
            return false;
        }
        
        return true;
    }

    /**
     * Shows an error alert with the specified title and message.
     * 
     * @param title the title of the alert
     * @param message the content message of the alert
     */
    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}