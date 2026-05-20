package com.example.components;


import com.example.core.Session;
import com.example.models.TrackDTO;
import com.example.services.TrackService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class TrackDetailView extends VBox{

    private final TrackService trackService = new TrackService();

    public TrackDetailView(TrackDTO track, boolean initialLiked, Runnable onBack, StackPane contentArea){
        this.setSpacing(0);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Button btnBack = new Button("<- Back");
        btnBack.setStyle(
            "-fx-background-color: transparent;"+
            "-fx-text-fill: #B39DDB;"+
            "-fx-cursor: hand;"
        );
        btnBack.setOnAction(e -> onBack.run());

        HBox topSection = new HBox(25);
        topSection.setAlignment(Pos.TOP_LEFT);
        topSection.setPadding(new Insets(20, 0, 25, 0));

        //portada
        StackPane coverPane = new StackPane();
        coverPane.setPrefSize(200, 200);
        coverPane.setMinSize(200,200);

        Rectangle placeholder = new Rectangle(200, 200);
        placeholder.setArcWidth(14);
        placeholder.setArcHeight(14);
        placeholder.setFill(Color.web("#2a2a2a"));

        if (track.getCoverPath() != null) {
            try {
                String safePath = track.getCoverPath().replace(" ", "%20");
                Image img = new Image("file:" + safePath);
                if (!img.isError()) {
                    ImageView cover = new ImageView(img);
                    cover.setFitWidth(200);
                    cover.setFitHeight(200);
                    cover.setPreserveRatio(false);
                    Rectangle clip = new Rectangle(200, 200);
                    clip.setArcWidth(14);
                    clip.setArcHeight(14);
                    cover.setClip(clip);
                    coverPane.getChildren().add(cover);
                } else {
                    Label note = new Label("♪");
                    note.setStyle("-fx-font-size: 60px; -fx-text-fill: #B39DDB;");
                    coverPane.getChildren().addAll(placeholder, note);
                }
            } catch (Exception e) {
                Label note = new Label("♪");
                note.setStyle("-fx-font-size: 60px; -fx-text-fill: #B39DDB;");
                coverPane.getChildren().addAll(placeholder, note);
            }
        } else {
            Label note = new Label("♪");
            note.setStyle("-fx-font-size: 60px; -fx-text-fill: #B39DDB;");
            coverPane.getChildren().addAll(placeholder, note);
        }

        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label lblTitle = new Label(track.getTitle());
        lblTitle.setStyle(
            "-fx-font-size: 26px;"+
            "-fx-font-weight: bold;"+
            "-fx-text-fill: #eeeeee;"
        );
        lblTitle.setWrapText(true);

        Label lblArtist = new Label(track.getUploaderUsername());
        lblArtist.setStyle("-fx-font-size: 15px; -fx-text-fill: #B39DDB;");

        //botones
        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.setPadding(new Insets(10, 0, 0, 0));
        //play
        Button btnPlay = buildIconButton("M9 15.714V8.287q0-.368.242-.588t.566-.22q.106 0 .214.028q.109.027.215.083l5.842 3.733q.186.13.28.298q.093.167.093.379t-.093.379t-.28.298l-5.843 3.733q-.105.055-.214.083q-.108.028-.214.028q-.323 0-.566-.22T9 15.714", "#B39DDB");
        //like
        SVGPath heartEmpty = new SVGPath();
        heartEmpty.setContent("M12.1 18.55l-.1.1l-.11-.1C7.14 14.24 4 11.39 4 8.5C4 6.5 5.5 5 7.5 5c1.54 0 3.04 1 3.57 2.36h1.86C13.46 6 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5c0 2.89-3.14 5.74-7.9 10.05M16.5 3c-1.74 0-3.41.81-4.5 2.08C10.91 3.81 9.24 3 7.5 3C4.42 3 2 5.41 2 8.5c0 3.77 3.4 6.86 8.55 11.53L12 21.35l1.45-1.32C18.6 15.36 22 12.27 22 8.5C22 5.41 19.58 3 16.5 3");
        heartEmpty.setFill(Color.WHITE);
        heartEmpty.setScaleX(1.1);
        heartEmpty.setScaleY(1.1);
        
        SVGPath heartFilled = new SVGPath();
        heartFilled.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5c0 3.77-3.4 6.86-8.55 11.53z");
        heartFilled.setFill(Color.web("#B39DDB"));
        heartFilled.setScaleX(1.1);
        heartFilled.setScaleY(1.1);
        heartFilled.setVisible(initialLiked);
        heartEmpty.setVisible(!initialLiked);

        StackPane heartIcon = new StackPane(heartEmpty, heartFilled);
        Button btnLike = new Button();
        btnLike.setGraphic(heartIcon);
        btnLike.setStyle(buildBtnStyle());

        //playlist
        Button btnPlaylist = new Button("+");
        btnPlaylist.setStyle(buildBtnStyle() + "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");
        //descargar
        Button btnDownload = buildIconButton("M5.5 20h13q.213 0 .356.144t.144.357t-.144.356T18.5 21h-13q-.213 0-.356-.144T5 20.499t.144-.356T5.5 20m6.13-3.379q-.164-.08-.295-.242l-3.989-5.292q-.298-.404-.077-.851t.723-.447h1.643V3.808q0-.343.232-.576T10.442 3h3.097q.343 0 .575.232t.232.576v5.98h1.643q.502 0 .723.448q.22.447-.077.85l-4.008 5.293q-.122.161-.294.242q-.173.081-.356.081t-.347-.08", "#B39DDB");

        //logica like

        btnLike.setOnAction(e -> {
            btnLike.setDisable(true);
            Task<Boolean> likeTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return trackService.toggleLike(Session.getInstance().getUserId(), track.getId());
                }
            };
            likeTask.setOnSucceeded(ev -> {
                boolean liked = likeTask.getValue();
                heartEmpty.setVisible(!liked);
                heartFilled.setVisible(liked);
                btnLike.setDisable(false);
            });
            likeTask.setOnFailed(ev -> btnLike.setDisable(false));
            new Thread(likeTask).start();
        });

        //logica playlist
        btnPlaylist.setOnAction(e -> new AddToPlaylistPopup(track).show());

        btnRow.getChildren().addAll(btnPlay, btnLike, btnPlaylist, btnDownload);
        infoBox.getChildren().addAll(lblTitle, lblArtist, btnRow);
        topSection.getChildren().addAll(coverPane, infoBox);

        //separador
        HBox separator = new HBox();
        separator.setStyle("-fx-background-color: rgba(255,255,255,0.08);");
        separator.setPrefHeight(1);
        separator.setMaxWidth(Double.MAX_VALUE);

        //info
        HBox infoRow = new HBox(30);
        infoRow.setPadding(new Insets(20, 0, 20, 0));
        infoRow.setAlignment(Pos.CENTER_LEFT);

        if (track.getGenre() != null && !track.getGenre().isEmpty())
            infoRow.getChildren().add(buildInfoChip("Genre", track.getGenre()));
        if (track.getBpm() != null)
            infoRow.getChildren().add(buildInfoChip("BPM", track.getBpm().toString()));
        if (track.getMusicalKey() != null && !track.getMusicalKey().isEmpty())
            infoRow.getChildren().add(buildInfoChip("Key", track.getMusicalKey()));

        //descripcion
        VBox descBox = new VBox(8);
        if (track.getDescription() != null && !track.getDescription().isEmpty()) {
            Label lblDescTitle = new Label("About");
            lblDescTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");
            Label lblDesc = new Label(track.getDescription());
            lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: #cccccc; -fx-wrap-text: true;");
            lblDesc.setWrapText(true);
            descBox.getChildren().addAll(lblDescTitle, lblDesc);
        }

        VBox content = new VBox(0, btnBack, topSection, separator, infoRow, descBox);
        content.setPadding(new Insets(0));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        this.getChildren().add(scroll);
    }

    private Button buildIconButton(String svgPath, String color){
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setFill(Color.web(color));
        icon.setScaleX(1.1);
        icon.setScaleY(1.1);
        Button btn = new Button();
        btn.setGraphic(icon);
        btn.setStyle(buildBtnStyle());
        return btn;
    }

    private String buildBtnStyle() {
        return "-fx-background-color: rgba(255,255,255,0.08);" +
                "-fx-background-radius: 50%;" +
                "-fx-min-width: 40px;" +
                "-fx-min-height: 40px;" +
                "-fx-cursor: hand;";
    }

    private VBox buildInfoChip(String label, String value) {
        VBox chip = new VBox(3);
        chip.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #eeeeee;");
        chip.getChildren().addAll(lbl, val);
        return chip;
    }
}
