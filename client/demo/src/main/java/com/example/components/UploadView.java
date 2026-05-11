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

    public UploadView() {
        this.setSpacing(15);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_CENTER);

        Label lblTitle = new Label("Upload New Track");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");

        TextField txtTitle   = buildField("Track title");
        TextField txtGenre   = buildField("Genre");
        TextField txtAlbum   = buildField("Album");
        TextField txtBpm     = buildField("BPM");
        TextField txtKey     = buildField("Musical key  (e.g. Am, C#)");

        // Selector de audio estilo pill
        Label lblAudioPath = new Label("No audio selected");
        lblAudioPath.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

        Button btnSelectAudio = new Button("Select Audio (.wav, .mp3)");
        btnSelectAudio.getStyleClass().add("pill-button");
        btnSelectAudio.setStyle("-fx-background-color: #B39DDB; -fx-text-fill: #1e1e1e;");

        btnSelectAudio.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Audio File");
            fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
            );
            Stage stage = (Stage) this.getScene().getWindow();
            File selected = fc.showOpenDialog(stage);
            if (selected != null) {
                audioFile = selected;
                lblAudioPath.setText(selected.getName());
                lblAudioPath.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
            }
        });

        HBox audioBox = new HBox(15, btnSelectAudio, lblAudioPath);
        audioBox.setAlignment(Pos.CENTER_LEFT);
        audioBox.setMaxWidth(400);

        // Feedback
        Label lblFeedback = new Label();
        lblFeedback.setStyle("-fx-font-size: 12px;");

        // Botón upload
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
            System.out.println("userId en sesión: " + Session.getInstance().getUserId());
            Task<TrackDTO> uploadTask = new Task<>() {
                @Override
                protected TrackDTO call() throws Exception {
                    return trackService.upload(
                        title, genre, album, finalBpm, key,
                        Session.getInstance().getUserId(),
                        finalAudio
                    );
                }
            };

            uploadTask.setOnSucceeded(event -> {
                setFeedback(lblFeedback, "Track uploaded successfully!", true);
                clearForm(txtTitle, txtGenre, txtAlbum, txtBpm, txtKey, lblAudioPath);
                audioFile = null;
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
                           TextField txtBpm, TextField txtKey, Label lblAudioPath) {
        txtTitle.clear();
        txtGenre.clear();
        txtAlbum.clear();
        txtBpm.clear();
        txtKey.clear();
        lblAudioPath.setText("No audio selected");
        lblAudioPath.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
    }
}