package com.example;

import com.example.components.FriendsPanel;
import com.example.components.MainContentPanel;
import com.example.components.NowPlayingPanel;
import com.example.components.PlayerControlsPanel;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainController extends HBox {

    public MainController() {

        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: transparent;");

        VBox colLeft = new VBox(20);
        colLeft.setPrefWidth(300);

        FriendsPanel friends = new FriendsPanel();
        NowPlayingPanel nowPlaying = new NowPlayingPanel();

        VBox.setVgrow(friends, Priority.ALWAYS); 
        
        colLeft.getChildren().addAll(friends, nowPlaying);




        VBox colRight = new VBox(20);
        HBox.setHgrow(colRight, Priority.ALWAYS); 
        
        MainContentPanel mainContent = new MainContentPanel();
        PlayerControlsPanel controls = new PlayerControlsPanel();
        
        
        VBox.setVgrow(mainContent, Priority.ALWAYS);
        controls.setPrefHeight(80); 
        
        colRight.getChildren().addAll(mainContent, controls);

        
        this.getChildren().addAll(colLeft, colRight);
    }
}
