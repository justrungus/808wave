package com.example.components;

import com.example.models.TrackDTO;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;

public class TrackSection extends VBox {

    public TrackSection(String title, List<TrackDTO> tracks) {
        this.setSpacing(12);
        this.setPadding(new Insets(0, 0, 20, 0));

        Label lblTitle = new Label(title);
        lblTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #B39DDB;"
        );

        HBox cardsRow = new HBox(12);
        cardsRow.setPadding(new Insets(5, 0, 5, 0));

        if (tracks == null || tracks.isEmpty()) {
            Label empty = new Label("No tracks yet.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            cardsRow.getChildren().add(empty);
        } else {
            for (TrackDTO track : tracks) {
                cardsRow.getChildren().add(new TrackCard(track));
            }
        }

        ScrollPane scroll = new ScrollPane(cardsRow);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(lblTitle, scroll);
    }
}
