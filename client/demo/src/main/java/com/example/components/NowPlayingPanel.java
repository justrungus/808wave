package com.example.components;

import com.example.core.AudioPlayer;
import com.example.core.Config;
import com.example.models.TrackDTO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class NowPlayingPanel extends VBox {

    private final ImageView albumArt = new ImageView();
    private final Label lblSong = new Label("Track name");
    private final Label lblArtist = new Label("Artist name");

    public NowPlayingPanel() {
        this.getStyleClass().add("glass-panel");
        this.setSpacing(15);
        this.setPadding(new Insets(15));
        this.setAlignment(Pos.CENTER);
        this.setMinWidth(220);

        String defaultPath = getClass().getResource("/com/example/imageDefault.png").toExternalForm();
        albumArt.setImage(new Image(defaultPath));
        albumArt.setFitWidth(180);
        albumArt.setFitHeight(180);
        albumArt.setPreserveRatio(false);
        Rectangle clip = new Rectangle(180, 180);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        albumArt.setClip(clip);

        VBox infoContainer = new VBox(5);
        infoContainer.setAlignment(Pos.CENTER);
        lblSong.getStyleClass().add("now-playing-title");
        lblArtist.getStyleClass().add("now-playing-artist");
        infoContainer.getChildren().addAll(lblSong, lblArtist);

        this.getChildren().addAll(albumArt, infoContainer);

        AudioPlayer.getInstance().currentTrackProperty().addListener((obs, oldTrack, newTrack) -> {
            if (newTrack != null) updateUI(newTrack);
        });
    }

    private void updateUI(TrackDTO track) {
        lblSong.setText(track.getTitle());
        lblArtist.setText(track.getUploaderUsername());

        if (track.getCoverPath() != null) {
            try {
                String safePath = (Config.SERVER_URL + "/" + track.getCoverPath()).replace(" ", "%20");
                Image img = new Image(safePath);
                if (!img.isError()) { albumArt.setImage(img); return; }
            } catch (Exception ignored) {}
        }
        String defaultPath = getClass().getResource("/com/example/imageDefault.png").toExternalForm();
        albumArt.setImage(new Image(defaultPath));
    }
}