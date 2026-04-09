package com.example.components;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class NowPlayingPanel extends VBox{
    public NowPlayingPanel(){
        this.getStyleClass().add("glass-panel");
        Label title = new Label("NowPlaying");
        title.getStyleClass().add(Styles.TITLE_4);

        this.getChildren().add(title);
    }
}
