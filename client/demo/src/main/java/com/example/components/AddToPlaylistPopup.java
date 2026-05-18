package com.example.components;

import com.example.core.Session;
import com.example.models.PlaylistDTO;
import com.example.models.TrackDTO;
import com.example.services.PlaylistService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class AddToPlaylistPopup {

    private final PlaylistService playlistService = new PlaylistService();
    private final TrackDTO track;

    public AddToPlaylistPopup(TrackDTO track) {
        this.track = track;
    }

    public void show() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setTitle("Add to Playlist");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setStyle(
            "-fx-background-color: #1e1e1e;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #B39DDB;" +
            "-fx-border-radius: 12;" +
            "-fx-border-width: 1;"
        );

        Label lblTitle = new Label("Add \"" + track.getTitle() + "\" to playlist");
        lblTitle.setStyle("-fx-text-fill: #B39DDB; -fx-font-size: 14px; -fx-font-weight: bold;");

        
        HBox createBox = new HBox(8);
        createBox.setAlignment(Pos.CENTER_LEFT);
        TextField txtName = new TextField();
        txtName.setPromptText("New playlist name...");
        txtName.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: #B39DDB;" +
            "-fx-prompt-text-fill: gray;" +
            "-fx-background-radius: 8;"
        );
        Button btnCreate = new Button("Create & Add");
        btnCreate.setStyle("-fx-background-color: #B39DDB; -fx-text-fill: #1e1e1e; -fx-background-radius: 8;");

        btnCreate.setOnAction(e -> {
            String name = txtName.getText().trim();
            if (name.isEmpty()) return;
            btnCreate.setDisable(true);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    PlaylistDTO created = playlistService.createPlaylist(name, Session.getInstance().getUserId());
                    playlistService.addTrackToPlaylist(created.getId(), track.getId());
                    return null;
                }
            };
            task.setOnSucceeded(ev -> popup.close());
            task.setOnFailed(ev -> {
                btnCreate.setDisable(false);
                txtName.setStyle(txtName.getStyle() + "-fx-border-color: #e74c3c;");
            });
            new Thread(task).start();
        });

        createBox.getChildren().addAll(txtName, btnCreate);

        
        Label lblOr = new Label("— or add to existing —");
        lblOr.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");
        lblOr.setMaxWidth(Double.MAX_VALUE);
        lblOr.setAlignment(Pos.CENTER);

        
        VBox playlistList = new VBox(6);
        Label lblLoading = new Label("Loading playlists...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
        playlistList.getChildren().add(lblLoading);

        Task<List<PlaylistDTO>> loadTask = new Task<>() {
            @Override
            protected List<PlaylistDTO> call() throws Exception {
                return playlistService.getMyPlaylists(Session.getInstance().getUserId());
            }
        };

        loadTask.setOnSucceeded(ev -> {
            playlistList.getChildren().clear();
            List<PlaylistDTO> playlists = loadTask.getValue();
            if (playlists.isEmpty()) {
                Label empty = new Label("No playlists yet.");
                empty.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
                playlistList.getChildren().add(empty);
            } else {
                for (PlaylistDTO pl : playlists) {
                    Button btnAdd = new Button("+ " + pl.getName());
                    btnAdd.setMaxWidth(Double.MAX_VALUE);
                    btnAdd.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.05);" +
                        "-fx-text-fill: #eeeeee;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                    );
                    btnAdd.setOnMouseEntered(e -> btnAdd.setStyle(
                        "-fx-background-color: rgba(179,157,219,0.2);" +
                        "-fx-text-fill: #B39DDB;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                    ));
                    btnAdd.setOnMouseExited(e -> btnAdd.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.05);" +
                        "-fx-text-fill: #eeeeee;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
                    ));
                    btnAdd.setOnAction(e -> {
                        btnAdd.setDisable(true);
                        Task<Void> addTask = new Task<>() {
                            @Override
                            protected Void call() throws Exception {
                                playlistService.addTrackToPlaylist(pl.getId(), track.getId());
                                return null;
                            }
                        };
                        addTask.setOnSucceeded(ev2 -> popup.close());
                        addTask.setOnFailed(ev2 -> btnAdd.setDisable(false));
                        new Thread(addTask).start();
                    });
                    playlistList.getChildren().add(btnAdd);
                }
            }
        });

        new Thread(loadTask).start();

        
        Button btnCancel = new Button("Cancel");
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-background-radius: 8;");
        btnCancel.setOnAction(e -> popup.close());

        root.getChildren().addAll(lblTitle, createBox, lblOr, playlistList, btnCancel);

        Scene scene = new Scene(root, 320, 400);
        scene.setFill(null);
        popup.setScene(scene);
        popup.showAndWait();
    }
}
