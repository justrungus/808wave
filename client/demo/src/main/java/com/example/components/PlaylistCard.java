package com.example.components;

import com.example.models.PlaylistDTO;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PlaylistCard extends VBox {

    public PlaylistCard(PlaylistDTO pl) {
        this.setSpacing(6);
        this.setAlignment(Pos.TOP_LEFT);
        this.setPrefWidth(150);
        this.setStyle(
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

        this.setOnMouseEntered(e -> this.setStyle(
            "-fx-background-color: rgba(179,157,219,0.15);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 10;" +
            "-fx-cursor: hand;"
        ));
        this.setOnMouseExited(e -> this.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 10;" +
            "-fx-cursor: hand;"
        ));

        this.getChildren().addAll(cover, lblName, lblCreator);
    }
}