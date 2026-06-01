package com.example.components;

import com.example.core.AudioPlayer;
import com.example.core.Session;
import com.example.models.TrackDTO;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;

public class PlayerControlsPanel extends HBox {

    private final AudioPlayer player = AudioPlayer.getInstance();

    private static final String SVG_PLAY  = "M9 15.714V8.287q0-.368.242-.588t.566-.22q.106 0 .214.028q.109.027.215.083l5.842 3.733q.186.13.28.298q.093.167.093.379t-.093.379t-.28.298l-5.843 3.733q-.105.055-.214.083q-.108.028-.214.028q-.323 0-.566-.22T9 15.714";
    private static final String SVG_PAUSE = "M8 16V8q0-.213.144-.356T8.5 7.5t.356.144T9 8v8q0 .213-.144.356T8.5 16.5t-.356-.144T8 16m7.5 0V8q0-.213.144-.356T16 7.5t.356.144T16.5 8v8q0 .213-.144.356T16 16.5t-.356-.144T15.5 16";

    public PlayerControlsPanel() {
        this.getStyleClass().add("glass-panel");
        HBox pill = mainPill();
        HBox.setHgrow(pill, Priority.ALWAYS);
        this.setPadding(new Insets(20, 20, 20, 20));
        this.getChildren().add(pill);
    }

    private SVGPath makeIcon(String svgPathData) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPathData);
        icon.setFill(Color.web("#B39DDB"));
        icon.setScaleX(1.3);
        icon.setScaleY(1.3);
        return icon;
    }

    private Button makeBtn(String svgPath) {
        Button btn = new Button();
        btn.setGraphic(makeIcon(svgPath));
        btn.getStyleClass().add("pill-button");
        return btn;
    }

    private HBox mainPill() {
        HBox mainPill = new HBox(5);
        mainPill.setAlignment(Pos.CENTER);

        //volumen
        String pathVol = "M18.692 11.975q0-2.056-1.11-3.749q-1.109-1.693-2.978-2.524q-.202-.098-.3-.278t-.012-.362q.093-.208.317-.268t.45.039q2.099.979 3.366 2.893t1.267 4.249t-1.267 4.249t-3.366 2.893q-.226.099-.45.039t-.317-.267q-.086-.183.012-.363t.3-.278q1.869-.83 2.979-2.524t1.11-3.749M7.73 14H5.116q-.349 0-.578-.23t-.23-.578v-2.384q0-.349.23-.578t.578-.23H7.73l2.685-2.685q.244-.244.568-.116t.324.476v8.65q0 .348-.324.476t-.568-.116zm8.077-2q0 .82-.302 1.555q-.302.736-.846 1.278q-.173.13-.378.041t-.205-.312V9.389q0-.223.205-.313t.377.041q.545.548.847 1.306T15.808 12";
        Button btnVolume = makeBtn(pathVol);

        //play-pausa
        SVGPath playIcon = makeIcon(SVG_PLAY);
        Button btnPlay = new Button();
        btnPlay.setGraphic(playIcon);
        btnPlay.getStyleClass().add("pill-button");

        //anterior
        String pathBack = "M6.73 16.116V7.885q0-.214.144-.357q.143-.144.357-.144t.357.144t.143.356v8.231q0 .214-.143.357q-.144.143-.357.143t-.357-.143q-.143-.143-.143-.357m9.277-.332l-4.662-3.112q-.186-.13-.27-.295T10.992 12t.084-.376q.084-.164.27-.295l4.662-3.112q.106-.08.217-.105t.237-.025q.323 0 .565.217t.242.59v6.212q0 .373-.242.59q-.242.218-.565.218q-.125 0-.237-.025t-.217-.106";
        Button btnBack = makeBtn(pathBack);

        Label lblActualTime = new Label("0:00");
        lblActualTime.getStyleClass().add("slide-label");

        Slider timeSlider = new Slider(0, 100, 0);
        timeSlider.getStyleClass().add("player-slider");
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMaxWidth(550);

        timeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            javafx.scene.Node track = timeSlider.lookup(".track");
            if (track != null) {
                double pct = (newVal.doubleValue() / timeSlider.getMax()) * 100;
                track.setStyle("-fx-background-color: linear-gradient(to right, #B39DDB " + pct + "%, rgba(255,255,255,0.15) " + pct + "%);");
            }
        });

        Label lblMaxTime = new Label("0:00");
        lblMaxTime.getStyleClass().add("slide-label");

        //siguiente
        String pathNext = "M16.27 16.116V7.885q0-.214.143-.357q.143-.144.356-.144t.357.144t.143.356v8.231q0 .214-.143.357t-.357.143t-.356-.143q-.144-.143-.144-.357m-9.538-1.01v-6.21q0-.374.242-.591q.243-.218.566-.218q.125 0 .236.025t.217.106l4.662 3.112q.187.13.27.295t.084.376t-.084.376q-.083.164-.27.295l-4.662 3.112q-.105.08-.217.106t-.237.025q-.323 0-.565-.218q-.242-.217-.242-.59";
        Button btnNext = makeBtn(pathNext);

        //like
        SVGPath heartEmpty = new SVGPath();
        heartEmpty.setContent("M12.1 18.55l-.1.1l-.11-.1C7.14 14.24 4 11.39 4 8.5C4 6.5 5.5 5 7.5 5c1.54 0 3.04 1 3.57 2.36h1.86C13.46 6 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5c0 2.89-3.14 5.74-7.9 10.05M16.5 3c-1.74 0-3.41.81-4.5 2.08C10.91 3.81 9.24 3 7.5 3C4.42 3 2 5.41 2 8.5c0 3.77 3.4 6.86 8.55 11.53L12 21.35l1.45-1.32C18.6 15.36 22 12.27 22 8.5C22 5.41 19.58 3 16.5 3");
        heartEmpty.setFill(Color.web("#B39DDB")); heartEmpty.setScaleX(1.1); heartEmpty.setScaleY(1.1);

        SVGPath heartFilled = new SVGPath();
        heartFilled.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5c0 3.77-3.4 6.86-8.55 11.53z");
        heartFilled.setFill(Color.web("#ffffff")); heartFilled.setScaleX(1.1); heartFilled.setScaleY(1.1);
        heartFilled.setVisible(false);
        StackPane heartIcon = new StackPane(heartEmpty, heartFilled);
        Button btnLike = new Button();
        btnLike.setGraphic(heartIcon);
        btnLike.getStyleClass().add("pill-button");

        //Aleatorio
        String pathShuffle = "M14.23 19v-1h3.1l-3.52-3.521l.713-.713L18 17.242v-2.954h1V19zm-8.522 0L5 18.292L17.292 6h-3.061V5H19v4.712h-1V6.708zm3.678-8.925L5 5.689L5.689 5l4.386 4.387z";
        Button btnShuffle = makeBtn(pathShuffle);

        //Loop
        String pathLoop = "m5.927 18.192l1.735 1.735q.146.146.153.344q.006.198-.153.363q-.166.166-.357.169t-.357-.162l-2.382-2.383q-.131-.131-.184-.268q-.053-.136-.053-.298t.053-.298t.184-.267l2.382-2.383q.146-.146.347-.153t.367.159q.16.165.162.354t-.162.354l-1.735 1.734h10.765q.27 0 .443-.173t.173-.442v-2.885q0-.213.143-.356t.357-.144t.357.144t.143.356v2.885q0 .671-.472 1.143t-1.144.472zM18.073 6.808H7.308q-.27 0-.442.173q-.174.173-.174.442v2.885q0 .213-.143.357t-.357.143t-.356-.143t-.144-.357V7.423q0-.671.472-1.143t1.144-.472h10.765l-1.734-1.735q-.147-.146-.153-.344t.153-.363q.165-.166.356-.169q.192-.003.357.163l2.383 2.382q.13.131.183.268q.053.136.053.298t-.053.298q-.052.136-.183.267l-2.383 2.383q-.146.146-.347.153t-.366-.159q-.16-.165-.163-.354t.163-.354z";
        Button btnLoop = makeBtn(pathLoop);

        //Descarga
        String pathDownload = "M5.5 20h13q.213 0 .356.144t.144.357t-.144.356T18.5 21h-13q-.213 0-.356-.144T5 20.499t.144-.356T5.5 20m6.13-3.379q-.164-.08-.295-.242l-3.989-5.292q-.298-.404-.077-.851t.723-.447h1.643V3.808q0-.343.232-.576T10.442 3h3.097q.343 0 .575.232t.232.576v5.98h1.643q.502 0 .723.448q.22.447-.077.85l-4.008 5.293q-.122.161-.294.242q-.173.081-.356.081t-.347-.08";
        Button btnDownload = makeBtn(pathDownload);

        //logica botones

        //play-pausa
        btnPlay.setOnAction(e -> player.playPause());
        player.playingProperty().addListener((obs, wasPlaying, isPlaying) ->
                playIcon.setContent(isPlaying ? SVG_PAUSE : SVG_PLAY));

        //siguiente-anterior
        btnNext.setOnAction(e -> player.next());
        btnBack.setOnAction(e -> player.previous());

        //aleatorio
        btnShuffle.setOnAction(e -> {
            player.toggleShuffle();
            boolean on = player.shuffleProperty().get();
            ((SVGPath) btnShuffle.getGraphic()).setFill(Color.web(on ? "#ffffff" : "#B39DDB"));
        });

        //loop
        btnLoop.setOnAction(e -> {
            player.toggleLoop();
            boolean on = player.loopProperty().get();
            ((SVGPath) btnLoop.getGraphic()).setFill(Color.web(on ? "#ffffff" : "#B39DDB"));
        });

        //seeking
        timeSlider.setOnMouseReleased(e -> player.seek(timeSlider.getValue()));

        //slider volumen
        player.currentTrackProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) return;
            javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
                @Override
                public void handle(long now) {
                    javafx.scene.media.MediaPlayer mp = player.getMediaPlayer();
                    if (mp == null) return;
                    javafx.util.Duration total = mp.getTotalDuration();
                    javafx.util.Duration current = mp.getCurrentTime();
                    if (total != null && !total.isUnknown() && total.toSeconds() > 0) {
                        double pct = (current.toSeconds() / total.toSeconds()) * 100;
                        if (!timeSlider.isValueChanging()) timeSlider.setValue(pct);
                        lblActualTime.setText(formatTime(current));
                        lblMaxTime.setText(formatTime(total));
                    }
                }
            };
            timer.start();
            player.currentTrackProperty().addListener((o, ot, nt) -> timer.stop());
        });

        //like estado inicial
        TrackDTO current = player.getCurrentTrack();
        if (current != null) {
            boolean liked = player.isLiked(current.getId());
            heartEmpty.setVisible(!liked);
            heartFilled.setVisible(liked);
        }

        //like actualizar al cambiar canción
        player.currentTrackProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) { heartEmpty.setVisible(true); heartFilled.setVisible(false); return; }
            boolean liked = player.isLiked(newT.getId());
            heartEmpty.setVisible(!liked);
            heartFilled.setVisible(liked);
        });

        //like  actualizar cuando cambia el set global
        player.likedIdsProperty().addListener((obs, oldSet, newSet) -> {
            TrackDTO t = player.getCurrentTrack();
            if (t == null) return;
            boolean liked = newSet.contains(t.getId());
            heartEmpty.setVisible(!liked);
            heartFilled.setVisible(liked);
        });

        //toggle like
        btnLike.setOnAction(e -> {
            TrackDTO t = player.getCurrentTrack();
            if (t == null) return;
            player.toggleLike(Session.getInstance().getUserId(), t.getId());
        });

        //volumen popup vertical 
        btnVolume.setOnAction(e -> {
            Slider volSlider = new Slider(0, 1, player.volumeProperty().get());
            volSlider.setOrientation(Orientation.VERTICAL);
            volSlider.setPrefHeight(100);
            volSlider.valueProperty().bindBidirectional(player.volumeProperty());

            VBox popup = new VBox(volSlider);
            popup.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 8; -fx-padding: 10;");
            popup.setPrefWidth(40);

            Popup volPopup = new Popup();
            volPopup.getContent().add(popup);
            volPopup.setAutoHide(true);

            javafx.geometry.Bounds bounds = btnVolume.localToScreen(btnVolume.getBoundsInLocal());
            volPopup.show(btnVolume.getScene().getWindow(),
                    bounds.getMinX() - 5,
                    bounds.getMinY() - 120);
        });

        mainPill.getChildren().addAll(
                btnVolume, btnPlay, btnBack,
                lblActualTime, timeSlider, lblMaxTime,
                btnNext, btnLike, btnShuffle, btnLoop, btnDownload
        );
        return mainPill;
    }

    private String formatTime(javafx.util.Duration d) {
        int totalSecs = (int) d.toSeconds();
        return String.format("%d:%02d", totalSecs / 60, totalSecs % 60);
    }
}