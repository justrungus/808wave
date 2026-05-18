package com.example.components;

import com.example.models.TrackDTO;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TrackCard extends VBox{

    public TrackCard(TrackDTO track){
        this.setSpacing(6);
        this.setAlignment(Pos.TOP_LEFT);
        this.setPrefWidth(150);
        this.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 12; " +
            "-fx-padding: 10; " +
            "-fx-cursor: hand;"
        );

        StackPane coverPane = new StackPane();
        coverPane.setPrefSize(130, 130);

        Rectangle placeholder = new Rectangle(130, 130);
        placeholder.setArcWidth(10);
        placeholder.setArcHeight(10);
        placeholder.setFill(Color.web("#2a2a2a"));
        
        if (track.getCoverPath() != null){
            try {
                String safePath = track.getCoverPath().replace(" ", "%20");
                Image img = new Image("file:" + safePath);
                if (img.isError()) {
                    System.out.println("Image error: " + img.getException().getMessage());
                }
                ImageView cover = new ImageView(img);
                cover.setFitWidth(130);
                cover.setFitHeight(130);
                cover.setPreserveRatio(false);
                Rectangle clip = new Rectangle(130, 130);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                cover.setClip(clip);
                coverPane.getChildren().add(cover);
            } catch (Exception e) {
                coverPane.getChildren().add(placeholder);
            }
        }else{
            Label note = new Label("♪");
            note.setStyle("-fx-font-size: 40px; -fx-text-fill: #B39DDB;");
            coverPane.getChildren().addAll(placeholder, note);
        }

        Label lblTitle = new Label(track.getTitle());
        lblTitle.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #eeeeee;" +
            "-fx-max-width: 130px;"
        );
        lblTitle.setWrapText(false);
        lblTitle.setEllipsisString("...");

        Label lblArtist = new Label(track.getUploaderUsername());
        lblArtist.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #B39DDB;"
        );

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

        //añadir playlist
        Button btnAddPlaylist = new Button("+");
        btnAddPlaylist.setStyle(
            "-fx-background-color: #B39DDB;" +
            "-fx-text-fill: #1e1e1e;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 24px;" +
            "-fx-min-height: 24px;" +
            "-fx-cursor: hand;"
        );
        btnAddPlaylist.setVisible(false);

        StackPane.setAlignment(btnAddPlaylist, Pos.TOP_RIGHT);
        coverPane.getChildren().add(btnAddPlaylist);

        this.setOnMouseEntered(e -> {
            this.setStyle(
                "-fx-background-color: rgba(179,157,219,0.15);" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 10;" +
                "-fx-cursor: hand;"
            );
            btnAddPlaylist.setVisible(true);
        });
        this.setOnMouseExited(e -> {
            this.setStyle(
                "-fx-background-color: rgba(255,255,255,0.05);" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 10;" +
                "-fx-cursor: hand;"
            );
            btnAddPlaylist.setVisible(false);
        });

        btnAddPlaylist.setOnAction(e -> {
            e.consume();
            new AddToPlaylistPopup(track).show();
        });

        this.getChildren().addAll(coverPane, lblTitle, lblArtist);
    }
}
