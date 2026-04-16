package com.example.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class NowPlayingPanel extends VBox{
    public NowPlayingPanel(){
        this.getStyleClass().add("glass-panel");
        this.setSpacing(15);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.CENTER);
        this.setMinWidth(220);

        //imagen
        ImageView albumArt = new ImageView();

        String imagePath = getClass().getResource("/com/example/imageDefault.png").toExternalForm();
        albumArt.setImage(new Image(imagePath));
        
        albumArt.setFitWidth(180);
        albumArt.setFitHeight(180);
        albumArt.setPreserveRatio(true);

        Rectangle clip = new Rectangle(180, 180);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        albumArt.setClip(clip);

        //nombre track y artista
        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(Pos.CENTER);
        
        Label lblSong = new Label("Track name");
        lblSong.getStyleClass().add("now-playing-title");

        Label lblArtist = new Label("Artist name");
        lblArtist.getStyleClass().add("now-playing-artist");

        infoContainer.getChildren().addAll(lblSong, lblArtist);

        this.getChildren().addAll(albumArt, infoContainer);

        
    }
}
