package com.example.components;

import com.example.core.Session;
import com.example.models.TrackDTO;
import com.example.services.TrackService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.util.List;

public class HomeView extends VBox {

    private final TrackService trackService = new TrackService();

    public HomeView() {
        this.setSpacing(10);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lblLoading = new Label("Loading...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
        this.getChildren().add(lblLoading);

        loadContent();
    }

    private void loadContent() {
        Task<HomeData> task = new Task<>() {
            @Override
            protected HomeData call() throws Exception {
                List<TrackDTO> recent = trackService.getRecentTracks();
                List<TrackDTO> top = trackService.getTopTracks();
                List<TrackDTO> mine = trackService.getTracksByUser(Session.getInstance().getUserId());
                return new HomeData(recent, top, mine);
            }
        };

        task.setOnSucceeded(e -> {
            HomeData data = task.getValue();
            this.getChildren().clear();

            VBox content = new VBox(25);
            content.setPadding(new Insets(0));

            content.getChildren().addAll(
                new TrackSection("Recently Added", data.recent, null),
                new TrackSection("Most Played", data.top, null),
                new TrackSection("Your Tracks", data.mine, null)
            );

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
        List<TrackDTO> recent, top, mine;
        HomeData(List<TrackDTO> recent, List<TrackDTO> top, List<TrackDTO> mine) {
            this.recent = recent; this.top = top; this.mine = mine;
        }
    }
}