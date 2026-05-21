package com.example.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SearchView extends VBox {

    public SearchView(String query, boolean fullResults, StackPane contentArea) {
        this.setSpacing(15);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lbl = new Label("Searching for: " + query);
        lbl.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
        this.getChildren().add(lbl);
    }
}
