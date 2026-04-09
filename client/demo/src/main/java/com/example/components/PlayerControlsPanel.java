package com.example.components;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class PlayerControlsPanel extends HBox{
    

    public PlayerControlsPanel(){
        this.getStyleClass().add("glass-panel");
        Label title = new Label("Controll");
        title.getStyleClass().add(Styles.TITLE_4);

        this.getChildren().add(title);
    }
}
