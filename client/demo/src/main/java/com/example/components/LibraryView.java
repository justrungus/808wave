package com.example.components;

import java.util.List;

import com.example.core.Session;
import com.example.models.PlaylistDTO;
import com.example.models.TrackDTO;
import com.example.services.PlaylistService;
import com.example.services.TrackService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class LibraryView extends VBox{
    
    private final TrackService trackService = new TrackService();
    private final PlaylistService playlistService = new PlaylistService();

    public LibraryView(){
        this.setSpacing(10);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lblLoading = new Label("Loading library...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
        this.getChildren().add(lblLoading);

        loadContent();
    }

    private void loadContent(){
        Long userId = Session.getInstance().getUserId();

        Task<LibraryData> task = new Task<>(){
            @Override
            protected LibraryData call() throws Exception {
                List<TrackDTO> mine = trackService.getTracksByUser(userId);
                List<TrackDTO> liked = trackService.getLikedTracks(userId);
                List<PlaylistDTO> playlists = playlistService.getMyPlaylists(userId);
                return new LibraryData(mine, liked, playlists);
            }
        };

        task.setOnSucceeded(e -> {
            LibraryData data = task.getValue();
            this.getChildren().clear();
            VBox content = new VBox(25);

            content.getChildren().addAll(
                new TrackSection("My Tracks", data.mine),
                new TrackSection("Liked Tracks", data.liked),
                new PlaylistSection("My Playlists", data.playlists)
            );

            ScrollPane scroll = new ScrollPane(content);
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            this.getChildren().add(scroll);
        });

        new Thread(task).start();
    }

    private static class LibraryData {
        List<TrackDTO> mine, liked;
        List<PlaylistDTO> playlists;
        LibraryData(List<TrackDTO> mine, List<TrackDTO> liked, List<PlaylistDTO> playlists) {
            this.mine = mine; this.liked = liked; this.playlists = playlists;
        }
    }

}
