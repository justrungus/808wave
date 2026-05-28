package com.example.components;

import com.example.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FriendItem extends HBox {

    public FriendItem(User user, boolean isOnline) {
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(5));
        this.getStyleClass().add("friend-item");
        this.setStyle("-fx-cursor: hand;");

        Circle statusDot = new Circle(4);
        statusDot.setFill(isOnline ? Color.web("#2EE59D") : Color.GRAY);

        Label lblName = new Label(user.getUsername());
        lblName.setStyle("-fx-text-fill: " + (isOnline ? "white" : "#666") + ";");

        this.getChildren().addAll(statusDot, lblName);

        final Long friendId = user.getUserId();

        this.setOnMouseClicked(e -> {
            javafx.scene.Parent p = this.getParent();
            while (p != null) {
                if (p instanceof com.example.controllers.MainController mc) {
                    javafx.scene.layout.StackPane ca = mc.getContentArea();
                    if (ca == null) break;
                    ca.getChildren().clear();
                    ca.getChildren().add(
                        new ProfileView(friendId, ca, () -> {
                            ca.getChildren().clear();
                            ca.getChildren().add(new HomeView(ca));
                        })
                    );
                    break;
                }
                p = p.getParent();
            }
        });
    }
}