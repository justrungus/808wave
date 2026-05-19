package com.example.components;

import com.example.models.PlaylistDTO;
import com.example.models.TrackDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;

public class SeeAllView extends VBox {

    private SeeAllView() {}

    public SeeAllView(String title, List<TrackDTO> tracks, Runnable onBack) {
        setup(title, onBack);

        FlowPane grid = new FlowPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 10, 0));

        if (tracks == null || tracks.isEmpty()) {
            Label empty = new Label("Nothing here yet.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
            grid.getChildren().add(empty);
        } else {
            for (TrackDTO track : tracks) {
                grid.getChildren().add(new TrackCard(track));
            }
        }

        this.getChildren().add(buildScroll(grid));
    }

    public static SeeAllView forPlaylists(String title, List<PlaylistDTO> playlists, Runnable onBack) {
        SeeAllView view = new SeeAllView();
        view.setup(title, onBack);

        FlowPane grid = new FlowPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10, 0, 10, 0));

        if (playlists == null || playlists.isEmpty()) {
            Label empty = new Label("No playlists yet.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            grid.getChildren().add(empty);
        } else {
            for (PlaylistDTO pl : playlists) {
                grid.getChildren().add(new PlaylistCard(pl));
            }
        }

        view.getChildren().add(view.buildScroll(grid));
        return view;
    }

    private void setup(String title, Runnable onBack) {
        this.setSpacing(15);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Button btnBack = new Button("← Back");
        btnBack.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #B39DDB;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );
        btnBack.setOnAction(e -> onBack.run());

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");

        this.getChildren().addAll(btnBack, lblTitle);
    }

    private ScrollPane buildScroll(FlowPane grid) {
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }
}