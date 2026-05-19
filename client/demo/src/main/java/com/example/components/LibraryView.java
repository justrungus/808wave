package com.example.components;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LibraryView extends VBox {

    private final TrackService trackService = new TrackService();
    private final PlaylistService playlistService = new PlaylistService();
    private final StackPane contentArea;

    public LibraryView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.setSpacing(10);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lblLoading = new Label("Loading library...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
        this.getChildren().add(lblLoading);

        loadContent();
    }

    private void loadContent() {
        Long userId = Session.getInstance().getUserId();

        Task<LibraryData> task = new Task<>() {
            @Override
            protected LibraryData call() throws Exception {
                List<TrackDTO> mine = trackService.getTracksByUser(userId);
                List<TrackDTO> liked = trackService.getLikedTracks(userId);
                List<PlaylistDTO> playlists = playlistService.getMyPlaylists(userId);
                Set<Long> likedIds = liked.stream()
                        .map(TrackDTO::getId)
                        .collect(Collectors.toSet());
                return new LibraryData(mine, liked, playlists, likedIds);
            }
        };

        task.setOnSucceeded(e -> {
            LibraryData data = task.getValue();
            this.getChildren().clear();

            VBox content = new VBox(25);

            content.getChildren().addAll(
                new TrackSection("My Tracks", data.mine, data.likedIds, () -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(
                        new SeeAllView("My Tracks", data.mine, data.likedIds, () -> {
                            contentArea.getChildren().clear();
                            contentArea.getChildren().add(new LibraryView(contentArea));
                        })
                    );
                }),
                new TrackSection("Liked Tracks", data.liked, data.likedIds, () -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(
                        new SeeAllView("Liked Tracks", data.liked, data.likedIds, () -> {
                            contentArea.getChildren().clear();
                            contentArea.getChildren().add(new LibraryView(contentArea));
                        })
                    );
                }),
                new PlaylistSection("My Playlists", data.playlists, () -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(
                        SeeAllView.forPlaylists("My Playlists", data.playlists, () -> {
                            contentArea.getChildren().clear();
                            contentArea.getChildren().add(new LibraryView(contentArea));
                        })
                    );
                })
            );

            ScrollPane scroll = new ScrollPane(content);
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            this.getChildren().add(scroll);
        });

        task.setOnFailed(e -> {
            System.out.println("Library error: " + task.getException().getMessage());
            task.getException().printStackTrace();
            this.getChildren().clear();
            Label err = new Label("Could not load library.");
            err.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
            this.getChildren().add(err);
        });

        new Thread(task).start();
    }

    private static class LibraryData {
        List<TrackDTO> mine, liked;
        List<PlaylistDTO> playlists;
        Set<Long> likedIds;
        LibraryData(List<TrackDTO> mine, List<TrackDTO> liked, List<PlaylistDTO> playlists, Set<Long> likedIds) {
            this.mine = mine; this.liked = liked; this.playlists = playlists; this.likedIds = likedIds;
        }
    }
}