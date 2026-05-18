package com.example.components;

import com.example.models.PlaylistDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import java.util.List;

public class PlaylistSection extends VBox {

    public PlaylistSection(String title, List<PlaylistDTO> playlists) {
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

        if (playlists == null || playlists.isEmpty()) {
            Label empty = new Label("No playlists yet.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
            cardsRow.getChildren().add(empty);
        } else {
            for (PlaylistDTO pl : playlists) {
                cardsRow.getChildren().add(buildPlaylistCard(pl));
            }
        }

        ScrollPane scroll = new ScrollPane(cardsRow);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(lblTitle, scroll);
    }

    private VBox buildPlaylistCard(PlaylistDTO pl) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(150);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 10;" +
            "-fx-cursor: hand;"
        );

        StackPane cover = new StackPane();
        cover.setPrefSize(130, 130);
        Rectangle bg = new Rectangle(130, 130);
        bg.setArcWidth(10);
        bg.setArcHeight(10);
        bg.setFill(Color.web("#2a2a2a"));
        Label icon = new Label("☰");
        icon.setStyle("-fx-font-size: 40px; -fx-text-fill: #B39DDB;");
        cover.getChildren().addAll(bg, icon);

        Label lblName = new Label(pl.getName());
        lblName.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #eeeeee;"
        );

        Label lblCreator = new Label("by " + pl.getCreatorUsername());
        lblCreator.setStyle("-fx-font-size: 11px; -fx-text-fill: #B39DDB;");

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: rgba(179,157,219,0.15);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 10;" +
            "-fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 10;" +
            "-fx-cursor: hand;"
        ));

        card.getChildren().addAll(cover, lblName, lblCreator);
        return card;
    }
}
