package com.example.components;

import java.util.List;

import com.example.core.AudioPlayer;
import com.example.core.Session;
import com.example.models.TrackDTO;
import com.example.services.TrackService;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class TrackCard extends VBox {

    private final TrackService trackService = new TrackService();

    public TrackCard(TrackDTO track, boolean initialLiked, StackPane contentArea, Runnable onBack) {
        this(track, initialLiked, contentArea, onBack, null);
    }

    public TrackCard(TrackDTO track, boolean initialLiked, StackPane contentArea, Runnable onBack, List<TrackDTO> queue) {
        this.setSpacing(6);
        this.setAlignment(Pos.TOP_LEFT);
        this.setPrefWidth(150);
        this.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 12; -fx-padding: 10; -fx-cursor: hand;");

        StackPane coverPane = new StackPane();
        coverPane.setPrefSize(130, 130);
        Rectangle placeholder = new Rectangle(130, 130);
        placeholder.setArcWidth(10);
        placeholder.setArcHeight(10);
        placeholder.setFill(Color.web("#2a2a2a"));

        if (track.getCoverPath() != null) {
            try {
                String safePath = track.getCoverPath().replace(" ", "%20");
                Image img = new Image("file:" + safePath);
                if (!img.isError()) {
                    ImageView cover = new ImageView(img);
                    cover.setFitWidth(130); cover.setFitHeight(130); cover.setPreserveRatio(false);
                    Rectangle clip = new Rectangle(130, 130);
                    clip.setArcWidth(10); clip.setArcHeight(10);
                    cover.setClip(clip);
                    coverPane.getChildren().add(cover);
                } else { addNote(coverPane, placeholder); }
            } catch (Exception e) { addNote(coverPane, placeholder); }
        } else { addNote(coverPane, placeholder); }

        SVGPath heartEmpty = new SVGPath();
        heartEmpty.setContent("M12.1 18.55l-.1.1l-.11-.1C7.14 14.24 4 11.39 4 8.5C4 6.5 5.5 5 7.5 5c1.54 0 3.04 1 3.57 2.36h1.86C13.46 6 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5c0 2.89-3.14 5.74-7.9 10.05M16.5 3c-1.74 0-3.41.81-4.5 2.08C10.91 3.81 9.24 3 7.5 3C4.42 3 2 5.41 2 8.5c0 3.77 3.4 6.86 8.55 11.53L12 21.35l1.45-1.32C18.6 15.36 22 12.27 22 8.5C22 5.41 19.58 3 16.5 3");
        heartEmpty.setFill(Color.WHITE); heartEmpty.setScaleX(0.9); heartEmpty.setScaleY(0.9);

        SVGPath heartFilled = new SVGPath();
        heartFilled.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5c0 3.77-3.4 6.86-8.55 11.53z");
        heartFilled.setFill(Color.web("#B39DDB")); heartFilled.setScaleX(0.9); heartFilled.setScaleY(0.9);
        heartEmpty.setVisible(!initialLiked); heartFilled.setVisible(initialLiked);
        StackPane heartIcon = new StackPane(heartEmpty, heartFilled);

        Button btnLike = new Button();
        btnLike.setGraphic(heartIcon);
        btnLike.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 50%; -fx-min-width: 30px; -fx-min-height: 30px; -fx-cursor: hand; -fx-padding: 4px;");
        btnLike.setVisible(false);
        StackPane.setAlignment(btnLike, Pos.BOTTOM_RIGHT);

        Button btnAddPlaylist = new Button("+");
        btnAddPlaylist.setStyle("-fx-background-color: #B39DDB; -fx-text-fill: #1e1e1e; -fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 24px; -fx-min-height: 24px; -fx-cursor: hand;");
        btnAddPlaylist.setVisible(false);
        StackPane.setAlignment(btnAddPlaylist, Pos.TOP_RIGHT);
        coverPane.getChildren().addAll(btnLike, btnAddPlaylist);

        Label lblTitle = new Label(track.getTitle());
        lblTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #eeeeee; -fx-max-width: 130px;");

        Label lblArtist = new Label(track.getUploaderUsername());
        lblArtist.setStyle("-fx-font-size: 11px; -fx-text-fill: #B39DDB; -fx-cursor: hand;");
        lblArtist.setOnMouseClicked(e -> {
            e.consume();
            if (contentArea != null && track.getUploaderId() != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(new ProfileView(track.getUploaderId(), contentArea, onBack));
            }
        });

        this.setOnMouseEntered(e -> {
            this.setStyle("-fx-background-color: rgba(179,157,219,0.15); -fx-background-radius: 12; -fx-padding: 10; -fx-cursor: hand;");
            btnLike.setVisible(true); btnAddPlaylist.setVisible(true);
        });
        this.setOnMouseExited(e -> {
            this.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 12; -fx-padding: 10; -fx-cursor: hand;");
            btnLike.setVisible(false); btnAddPlaylist.setVisible(false);
        });

        btnLike.setOnAction(e -> {
            e.consume();
            btnLike.setDisable(true);
            Task<Boolean> t = new Task<>() {
                @Override protected Boolean call() throws Exception {
                    return trackService.toggleLike(Session.getInstance().getUserId(), track.getId());
                }
            };
            t.setOnSucceeded(ev -> { boolean liked = t.getValue(); heartEmpty.setVisible(!liked); heartFilled.setVisible(liked); btnLike.setDisable(false); });
            t.setOnFailed(ev -> btnLike.setDisable(false));
            new Thread(t).start();
        });

        btnAddPlaylist.setOnAction(e -> { e.consume(); new AddToPlaylistPopup(track).show(); });

        this.setOnMouseClicked(e -> {
            if (contentArea == null || onBack == null) return;
            if (e.getTarget() == btnLike || e.getTarget() == btnAddPlaylist) return;
            AudioPlayer.getInstance().play(track, queue);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new TrackDetailView(track, initialLiked, onBack, contentArea));
        });

        this.getChildren().addAll(coverPane, lblTitle, lblArtist);
    }

    private void addNote(StackPane pane, Rectangle bg) {
        Label note = new Label("♪");
        note.setStyle("-fx-font-size: 40px; -fx-text-fill: #B39DDB;");
        pane.getChildren().addAll(bg, note);
    }
}