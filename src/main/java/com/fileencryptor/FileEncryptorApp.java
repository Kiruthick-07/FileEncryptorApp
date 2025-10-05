package com.fileencryptor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the File Encryptor application.
 * Launches the JavaFX UI and sets the primary stage.
 */
public class FileEncryptorApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fileencryptor/FileEncryptorView.fxml"));
            Parent root = loader.load();
            
            // Set up the scene
            Scene scene = new Scene(root);
            
            // Configure the stage
            primaryStage.setTitle("File Encryptor");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to launch the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}