package com.example.components;


import java.io.File;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadView extends VBox{
    
    private File audioFile;
    private File imageFile;

    public UploadView(){
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.TOP_CENTER);

        Label lblTitle = new Label("Upload New Track");
        lblTitle.getStyleClass().add("friends-header"); 
        
        //To do cambiar estilos 


        TextField txtTrackName = new TextField();
        txtTrackName.setPromptText("Track title");
        txtTrackName.getStyleClass().add("now-playing-title");
        txtTrackName.setMaxWidth(400);

        TextField txtArtist = new TextField();
        txtArtist.setPromptText("Artist name");
        txtArtist.setMaxWidth(400);

        Label lblAudioPath = new Label("No audio selected");
        Button btnSelectAudio = new Button("Select Audio (.wav, .mp3)");
        
        Label lblImagePath = new Label("No cover art selected");
        Button btnSelectImage = new Button("Select Cover Art (.jpg, .png)");

        btnSelectAudio.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Audio File");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
            );

            Stage stage = (Stage) this.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(stage);
            
            if (selectedFile != null) {
                this.audioFile = selectedFile;
                lblAudioPath.setText(selectedFile.getName());
                lblAudioPath.setStyle("-fx-text-fill: #2ecc71;"); 
            }
        });

        btnSelectImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Cover Art");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            
            Stage stage = (Stage) this.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(stage);
            
            if (selectedFile != null) {
                this.imageFile = selectedFile;
                lblImagePath.setText(selectedFile.getName());
                lblImagePath.setStyle("-fx-text-fill: #2ecc71;");
            }
        });


    Button btnUpload = new Button("Upload to Server");
        btnUpload.getStyleClass().addAll(Styles.ACCENT, Styles.LARGE);
        
        btnUpload.setOnAction(e -> {
            String title = txtTrackName.getText();
            String artist = txtArtist.getText();

            // Validacion 
            if (title.isEmpty() || artist.isEmpty() || audioFile == null || imageFile == null) {
                System.out.println("ERROR: Faltan datos por rellenar.");
                // TODO alerta al usuario
                return;
            }

            System.out.println("Preparando para subir: " + title + " de " + artist);
            // TODO: Llamar al TrackService 
        });

        
        VBox audioBox = new VBox(5, btnSelectAudio, lblAudioPath);
        audioBox.setAlignment(Pos.CENTER);
        
        VBox imageBox = new VBox(5, btnSelectImage, lblImagePath);
        imageBox.setAlignment(Pos.CENTER);

        HBox filePickers = new HBox(40, audioBox, imageBox);
        filePickers.setAlignment(Pos.CENTER);

        
        this.getChildren().addAll(lblTitle, txtTrackName, txtArtist, filePickers, btnUpload);
    }



    
}
