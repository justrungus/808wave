package com.example.components;

import java.io.File;

import com.example.core.Session;
import com.example.models.TrackDTO;
import com.example.services.TrackService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class UploadView extends VBox {

    private final TrackService trackService = new TrackService();
    private File audioFile;
    private File coverFile;

    public UploadView() {
        this.setSpacing(15);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_CENTER);

        Label lblTitle = new Label("Upload New Track");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");

        TextField txtTitle = buildField("Track title");
        TextField txtGenre = buildField("Genre");
        TextField txtAlbum = buildField("Album");
        TextField txtBpm   = buildField("BPM");
        TextField txtKey   = buildField("Musical key (e.g. Am, C#)");

        // Selector de audio
        Label lblAudioPath = new Label("No audio selected");
        lblAudioPath.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
        Button btnSelectAudio = buildFileButton("Select Audio (.wav, .mp3)");
        btnSelectAudio.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Audio File");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
            );
            File selected = fc.showOpenDialog((Stage) this.getScene().getWindow());
            if (selected != null) {
                audioFile = selected;
                lblAudioPath.setText(selected.getName());
                lblAudioPath.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
            }
        });

        // Selector de portada
        Label lblCoverPath = new Label("No cover selected (optional)");
        lblCoverPath.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
        Button btnSelectCover = buildFileButton("Select Cover Art (.jpg, .png)");
        btnSelectCover.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Cover Art");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File selected = fc.showOpenDialog((Stage) this.getScene().getWindow());
            if (selected != null) {
                coverFile = selected;
                lblCoverPath.setText(selected.getName());
                lblCoverPath.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
            }
        });

        HBox audioBox = new HBox(15, btnSelectAudio, lblAudioPath);
        audioBox.setAlignment(Pos.CENTER_LEFT);
        audioBox.setMaxWidth(400);

        HBox coverBox = new HBox(15, btnSelectCover, lblCoverPath);
        coverBox.setAlignment(Pos.CENTER_LEFT);
        coverBox.setMaxWidth(400);

        Label lblFeedback = new Label();
        lblFeedback.setStyle("-fx-font-size: 12px;");

        Button btnUpload = new Button("Upload to Server");
        btnUpload.getStyleClass().add("pill-button");
        btnUpload.setStyle("-fx-background-color: #B39DDB; -fx-text-fill: #1e1e1e; -fx-font-size: 14px; -fx-padding: 10 30 10 30;");

        btnUpload.setOnAction(e -> {
            String title   = txtTitle.getText();
            String genre   = txtGenre.getText();
            String album   = txtAlbum.getText();
            String bpmText = txtBpm.getText();
            String key     = txtKey.getText();

            if (title.isEmpty() || audioFile == null) {
                setFeedback(lblFeedback, "Title and audio file are required.", false);
                return;
            }

            Integer bpm = null;
            if (!bpmText.isEmpty()) {
                try {
                    bpm = Integer.parseInt(bpmText);
                } catch (NumberFormatException ex) {
                    setFeedback(lblFeedback, "BPM must be a number.", false);
                    return;
                }
            }

            btnUpload.setDisable(true);
            setFeedback(lblFeedback, "Uploading...", true);

            final Integer finalBpm = bpm;
            final File finalAudio = audioFile;
            final File finalCover = coverFile;

            Task<TrackDTO> uploadTask = new Task<>() {
                @Override
                protected TrackDTO call() throws Exception {
                    return trackService.upload(
                        title, genre, album, finalBpm, key,
                        Session.getInstance().getUserId(),
                        finalAudio, finalCover
                    );
                }
            };

            uploadTask.setOnSucceeded(event -> {
                setFeedback(lblFeedback, "Track uploaded successfully!", true);
                clearForm(txtTitle, txtGenre, txtAlbum, txtBpm, txtKey, lblAudioPath, lblCoverPath);
                audioFile = null;
                coverFile = null;
                btnUpload.setDisable(false);
            });

            uploadTask.setOnFailed(event -> {
                setFeedback(lblFeedback, uploadTask.getException().getMessage(), false);
                btnUpload.setDisable(false);
            });

            new Thread(uploadTask).start();
        });

        this.getChildren().addAll(
            lblTitle,
            txtTitle,
            buildRow(txtGenre, txtAlbum),
            buildRow(txtBpm, txtKey),
            audioBox,
            coverBox,
            lblFeedback,
            btnUpload
        );
    }

    private TextField buildField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(400);
        field.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: #B39DDB;" +
            "-fx-prompt-text-fill: gray;" +
            "-fx-background-radius: 10;"
        );
        field.getStyleClass().add("now-playing-title");
        return field;
    }

    private Button buildFileButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("pill-button");
        btn.setStyle("-fx-background-color: #B39DDB; -fx-text-fill: #1e1e1e;");
        return btn;
    }

    private HBox buildRow(TextField... fields) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(400);
        for (TextField f : fields) {
            f.setMaxWidth(192);
            row.getChildren().add(f);
        }
        return row;
    }

    private void setFeedback(Label label, String message, boolean success) {
        label.setText(message);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (success ? "#2ecc71" : "#e74c3c") + ";");
    }

    private void clearForm(TextField txtTitle, TextField txtGenre, TextField txtAlbum,
                           TextField txtBpm, TextField txtKey,
                           Label lblAudioPath, Label lblCoverPath) {
        txtTitle.clear();
        txtGenre.clear();
        txtAlbum.clear();
        txtBpm.clear();
        txtKey.clear();
        lblAudioPath.setText("No audio selected");
        lblAudioPath.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
        lblCoverPath.setText("No cover selected (optional)");
        lblCoverPath.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
    }
}