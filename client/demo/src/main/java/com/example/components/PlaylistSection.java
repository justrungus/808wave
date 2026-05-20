package com.example.components;

import java.util.List;

import com.example.models.PlaylistDTO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlaylistSection extends VBox {

    public PlaylistSection(String title, List<PlaylistDTO> playlists,
                           StackPane contentArea, Runnable onBack, Runnable onSeeAll) {
        this.setSpacing(12);
        this.setPadding(new Insets(0, 0, 20, 0));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label(title);
        lblTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #B39DDB;"
        );
        HBox.setHgrow(lblTitle, Priority.ALWAYS);

        Button btnSeeAll = new Button("See all →");
        btnSeeAll.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: gray;" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;"
        );
        btnSeeAll.setOnMouseEntered(e -> btnSeeAll.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #B39DDB;" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;"
        ));
        btnSeeAll.setOnMouseExited(e -> btnSeeAll.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: gray;" +
            "-fx-font-size: 12px;" +
            "-fx-cursor: hand;"
        ));
        btnSeeAll.setOnAction(e -> onSeeAll.run());

        header.getChildren().addAll(lblTitle, btnSeeAll);

        HBox cardsRow = new HBox(12);
        cardsRow.setPadding(new Insets(5, 0, 5, 0));

        if (playlists == null || playlists.isEmpty()) {
            Label empty = new Label("No playlists yet.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            cardsRow.getChildren().add(empty);
        } else {
            for (PlaylistDTO pl : playlists) {
                cardsRow.getChildren().add(new PlaylistCard(pl, contentArea, onBack));
            }
        }

        ScrollPane scroll = new ScrollPane(cardsRow);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(header, scroll);
    }
}