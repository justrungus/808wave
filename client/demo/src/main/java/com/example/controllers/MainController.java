package com.example.controllers;

import com.example.components.FriendsPanel;
import com.example.components.MainContentPanel;
import com.example.components.NowPlayingPanel;
import com.example.components.PlayerControlsPanel;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class MainController extends HBox {

    private MainContentPanel mainContent;

    public MainController() {

        

        this.setSpacing(10);
        this.setPadding(new Insets(0));
        this.setStyle("-fx-background-color: transparent;");

        VBox colLeft = new VBox(10);
        colLeft.setPrefWidth(300);

        FriendsPanel friends = new FriendsPanel();
        NowPlayingPanel nowPlaying = new NowPlayingPanel();

        nowPlaying.setMinHeight(275);

        VBox.setVgrow(friends, Priority.ALWAYS);

        colLeft.getChildren().addAll(friends, nowPlaying);

        VBox colRight = new VBox(10);
        HBox.setHgrow(colRight, Priority.ALWAYS);

        this.mainContent = new MainContentPanel();
        PlayerControlsPanel controls = new PlayerControlsPanel();

        VBox.setVgrow(mainContent, Priority.ALWAYS);
        controls.setPrefHeight(50);

        colRight.getChildren().addAll(mainContent, controls);

        this.getChildren().addAll(colLeft, colRight);

        
    }

    public void refreshUserData() {
        if (mainContent != null) {
            mainContent.refreshUserData();
        }
    }

    
}
