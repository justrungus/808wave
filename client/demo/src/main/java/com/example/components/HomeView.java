package com.example.components;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.core.Session;
import com.example.models.TrackDTO;
import com.example.services.TrackService;
import com.example.services.UserService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeView extends VBox {

    private final TrackService trackService = new TrackService();
    private final UserService userService = new UserService();
    private final StackPane contentArea;

    public HomeView(StackPane contentArea) {
        this.contentArea = contentArea;
        this.setSpacing(10);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lblLoading = new Label("Loading...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
        this.getChildren().add(lblLoading);

        loadContent();
    }

    private void loadContent() {
        Long userId = Session.getInstance().getUserId();

        Task<HomeData> task = new Task<>() {
            @Override
            protected HomeData call() throws Exception {
                List<TrackDTO> recent = trackService.getRecentTracks();
                List<TrackDTO> top = trackService.getTopTracks();
                List<TrackDTO> mine = trackService.getTracksByUser(userId);
                List<TrackDTO> liked = trackService.getLikedTracks(userId);
                List<TrackDTO> feed = userId != null ? userService.getFollowingFeed(userId) : List.of();
                Set<Long> likedIds = liked.stream()
                        .map(TrackDTO::getId)
                        .collect(Collectors.toSet());
                return new HomeData(recent, top, mine, feed, likedIds);
            }
        };

        task.setOnSucceeded(e -> {
            HomeData data = task.getValue();
            this.getChildren().clear();

            VBox content = new VBox(25);
            content.setPadding(new Insets(0));

            Runnable goHome = () -> {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(new HomeView(contentArea));
            };

            content.getChildren().addAll(
                new TrackSection("Recently Added", data.recent, data.likedIds, contentArea, goHome, null),
                new TrackSection("Most Played", data.top, data.likedIds, contentArea, goHome, null),
                new TrackSection("Your Tracks", data.mine, data.likedIds, contentArea, goHome, null)
            );

            if (data.feed != null && !data.feed.isEmpty()) {
                content.getChildren().add(
                    new TrackSection("From people you follow", data.feed, data.likedIds, contentArea, goHome, null)
                );
            }

            ScrollPane scroll = new ScrollPane(content);
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            this.getChildren().add(scroll);
        });

        task.setOnFailed(e -> {
            System.out.println("Home error: " + task.getException().getMessage());
            task.getException().printStackTrace();
            this.getChildren().clear();
            Label err = new Label("Could not load content.");
            err.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
            this.getChildren().add(err);
        });

        new Thread(task).start();
    }

    private static class HomeData {
        List<TrackDTO> recent, top, mine, feed;
        Set<Long> likedIds;
        HomeData(List<TrackDTO> recent, List<TrackDTO> top, List<TrackDTO> mine,
                 List<TrackDTO> feed, Set<Long> likedIds) {
            this.recent = recent; this.top = top; this.mine = mine;
            this.feed = feed; this.likedIds = likedIds;
        }
    }
}