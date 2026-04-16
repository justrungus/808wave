package com.example.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class FriendsPanel extends VBox{
    
    public FriendsPanel(){

        this.setSpacing(15);
        
        this.getStyleClass().add("glass-panel");
        this.setId("friends-sidebar");
        this.setMinWidth(200);
        this.setSpacing(15);
        

        Label lblHeader = new Label("Friends");
        lblHeader.getStyleClass().add("friends-header");
        // lblHeader.setMaxWidth(Double.MAX_VALUE);
        this.setAlignment(Pos.TOP_CENTER);

        VBox listContainer = new VBox(20);

        VBox onlineBox = new VBox(10);
        Label lblOnline = new Label("Online");
        lblOnline.getStyleClass().add("status-divider");
        onlineBox.getChildren().addAll(lblOnline,
            new FriendItem("Friend 1", true),
            new FriendItem("Friend 2", true),
            new FriendItem("Friend 3", true)
    
        );
        
        VBox offlineBox = new VBox(10);
        Label lblOffline = new Label("Offline");
        lblOffline.getStyleClass().add("status-divider");
        offlineBox.getChildren().addAll(lblOffline,
            new FriendItem("Friend 4", false),
            new FriendItem("Friend 4", false),
            new FriendItem("Friend 4", false),
            new FriendItem("Friend 4", false),
            new FriendItem("Friend 4", false),
            new FriendItem("Friend 4", false),
            new FriendItem("Friend 4", false),
            
            new FriendItem("Friend 5", false)
        );

        listContainer.getChildren().addAll(onlineBox, offlineBox);
        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        this.getChildren().addAll(lblHeader, scroll);
    }
}
