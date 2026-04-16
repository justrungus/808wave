package com.example.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FriendItem extends HBox{
    public FriendItem(String name, boolean isOnline) {
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5));
        this.getStyleClass().add("friend-item");

        // Puntito de estado
        Circle statusDot = new Circle(4);
        statusDot.setFill(isOnline ? Color.web("#2EE59D") : Color.GRAY); 

        Label lblName = new Label(name);
        lblName.setStyle("-fx-text-fill: " + (isOnline ? "white" : "#666") + ";");

        this.getChildren().addAll(statusDot, lblName);
    }
}
