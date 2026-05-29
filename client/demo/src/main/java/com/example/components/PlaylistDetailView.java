package com.example.components;


import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.core.Session;
import com.example.models.PlaylistDTO;
import com.example.models.TrackDTO;
import com.example.services.PlaylistService;
import com.example.services.TrackService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PlaylistDetailView extends VBox{
    
    private final PlaylistService playlistService = new PlaylistService();
    private final TrackService trackService = new TrackService();

    private PlaylistDTO playlist;
    private final StackPane contentArea;
    private final Runnable onBack;

    public PlaylistDetailView(PlaylistDTO playlist, StackPane contentArea, Runnable onBack) {
        this.playlist = playlist;
        this.contentArea = contentArea;
        this.onBack = onBack;

        this.setSpacing(0);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lblLoading = new Label("Loading playlist...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
        this.getChildren().add(lblLoading);
        
        loadContent();
    }

    private void loadContent(){
        Long userId = Session.getInstance().getUserId();

        Task<PlaylistData> task = new Task<>(){
            @Override
            protected PlaylistData call() throws Exception {
                List<TrackDTO> tracks = playlistService.getPlaylistTracks(playlist.getId());
                List<TrackDTO> liked = trackService.getLikedTracks(userId);
                Set<Long> likedIds = liked.stream()
                        .map(TrackDTO::getId)
                        .collect(Collectors.toSet());
                return new PlaylistData(tracks, likedIds);
            }
        };

        task.setOnSucceeded(e -> {
            PlaylistData data = task.getValue();
            this.getChildren().clear();
            this.getChildren().add(buildScroll(data));
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            this.getChildren().clear();
            Label err = new Label("Could not load playlist.");
            err.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
            this.getChildren().add(err);
        });

        new Thread(task).start();
    }

    private ScrollPane buildScroll(PlaylistData data){
        VBox content = new VBox(15);
        content.getChildren().addAll(
                buildBackButton(),
                buildHeader(),
                buildSeparator(),
                buildTrackList(data)
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private Button buildBackButton(){
        Button btn = new Button("<- Back");
        btn.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-text-fill: #B39DDB;"
                + "-fx-font-size: 13px;"
                + "-fx-cursor: hand;"
        );
        btn.setOnAction(e -> onBack.run());
        return btn;
    }

    private HBox buildHeader(){
        boolean isOwner = isOwner();
        
        StackPane coverPane = new StackPane();
        coverPane.setPrefSize(180,180);
        coverPane.setMinSize(180,180);

        Rectangle bg = new Rectangle(180,180);
        bg.setArcWidth(14);
        bg.setArcHeight(14);
        bg.setFill(Color.web("#2a2a2a"));

        boolean hasCover = false;
        if(playlist.getCoverPath() != null){
            try{
                String safePath = playlist.getCoverPath().replace(" ", "%20");
                Image img = new Image("file: "+safePath);
                if(!img.isError()){
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(180);
                    iv.setFitHeight(180);
                    iv.setPreserveRatio(false);
                    Rectangle clip = new Rectangle(180, 180);
                    clip.setArcWidth(14);
                    clip.setArcHeight(14);
                    iv.setClip(clip);
                    coverPane.getChildren().add(iv);
                    hasCover = true;
                }
            }catch (Exception ignored) {}
        }
        if (!hasCover){
            Label note = new Label("♪");
            note.setStyle("-fx-font-size: 56px; -fx-text-fill: #b39ddb");
            coverPane.getChildren().addAll(bg, note);
        }
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label lblKind = new Label("PLAYLIST");
        lblKind.setStyle("-fx-font-size: 11px; -fx-text-fill: gray; -fx-font-weight: bold;");

        Label lblName = new Label(playlist.getName());
        lblName.setStyle(
                "-fx-font-size: 26px;"+
                "-fx-font-weight: bold;"+
                "-fx-text-fill: #eeeeee;"
        );
        lblName.setWrapText(true);

        Label lblCreator = new Label(playlist.getName());
        lblCreator.setStyle("-fx-font-size: 14px; -fx-text-fill: #B39DDB;");

        HBox btnRow = new HBox(10);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        btnRow.setPadding(new Insets(10, 0, 0 ,0));
        
        if (isOwner) {
            Button btnEditCover = new Button("Edit cover");
            btnEditCover.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.08);"
                    + "-fx-text-fill: #B39DDB;"
                    + "-fx-background-radius: 8;"
                    + "-fx-cursor: hand;"
                    + "-fx-padding: 6 12 6 12;"
            );
            btnEditCover.setOnAction(e -> editCover());

            Button btnDelete = new Button("Delete playlist");
            btnDelete.setStyle(
                    "-fx-background-color: rgba(231,76,60,0.15);"
                    + "-fx-text-fill: #e74c3c;"
                    + "-fx-background-radius: 8;"
                    + "-fx-cursor: hand;"
                    + "-fx-padding: 6 12 6 12;"
            );
            btnDelete.setOnAction(e -> confirmAndDelete());

            btnRow.getChildren().addAll(btnEditCover, btnDelete);
        }

        infoBox.getChildren().addAll(lblKind, lblName, lblCreator, btnRow);

        HBox header = new HBox(25, coverPane, infoBox);
        header.setPadding(new Insets(20, 0, 25, 0));
        header.setAlignment(Pos.TOP_LEFT);
        return header;
    }

    private Region buildSeparator(){
        Region sep = new Region();
        sep.setStyle("-fx-background-color: rgba(255,255,255,0.08);");
        sep.setPrefHeight(1);
        sep.setMaxWidth(Double.MAX_VALUE);
        return sep;
    }

    private VBox buildTrackList(PlaylistData data){
        VBox list = new VBox(4);
        list.setPadding(new Insets(15, 0,0,0));

        if(data.tracks == null || data.tracks.isEmpty()){
            Label empty = new Label("This playlist is empty");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
            empty.setPadding(new Insets(20, 0, 0, 0));
            list.getChildren().add(empty);
            return list;
        }

        boolean isOwner = isOwner();
        int index = 1;
        for (TrackDTO track : data.tracks){
            list.getChildren().add(buildTrackRow(track, index++, data.likedIds.contains(track.getId()), isOwner, data));
        }
        return list;
    }

    private HBox buildTrackRow(TrackDTO track, int index, boolean initialLiked, boolean isOwner, PlaylistData data){
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");

        //numero
        Label lblIndex = new Label(String.valueOf(index));
        lblIndex.setStyle("-fx-text-fill: gray; -fx-font-size: 12px; -fx-min-width: 25px;");

        //miniatura
        StackPane thumbPane = new StackPane();
        thumbPane.setPrefSize(40, 40);
        Rectangle thumbBg = new Rectangle(40, 40);
        thumbBg.setArcWidth(6);
        thumbBg.setArcHeight(6);
        thumbBg.setFill(Color.web("#2a2a2a"));

        boolean hasCover = false;
        if (track.getCoverPath() != null) {
            try {
                String safePath = track.getCoverPath().replace(" ", "%20");
                Image img = new Image("file:" + safePath);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(40);
                    iv.setFitHeight(40);
                    iv.setPreserveRatio(false);
                    Rectangle clip = new Rectangle(40, 40);
                    clip.setArcWidth(6);
                    clip.setArcHeight(6);
                    iv.setClip(clip);
                    thumbPane.getChildren().add(iv);
                    hasCover = true;
                }
            } catch (Exception ignored) {}
        }
        if (!hasCover) {
            Label note = new Label("♪");
            note.setStyle("-fx-font-size: 16px; -fx-text-fill: #B39DDB;");
            thumbPane.getChildren().addAll(thumbBg, note);
        }

        //titulo y artista en columna
        VBox titleBox = new VBox(2);
        Label lblTitle = new Label(track.getTitle());
        lblTitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #eeeeee; -fx-font-weight: bold;");
        Label lblArtist = new Label(track.getUploaderUsername());
        lblArtist.setStyle("-fx-font-size: 11px; -fx-text-fill: #B39DDB; -fx-cursor: hand;");
        lblArtist.setOnMouseClicked(e -> {
            e.consume();
            if (contentArea != null && track.getUploaderId() != null) {
                Runnable goBack = () -> {
                    contentArea.getChildren().clear();
                    contentArea.getChildren().add(new PlaylistDetailView(playlist, contentArea, onBack));
                };
                contentArea.getChildren().clear();
                contentArea.getChildren().add(new ProfileView(track.getUploaderId(), contentArea, goBack));
            }
        });
        titleBox.getChildren().addAll(lblTitle, lblArtist);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        
        Button btnLike = buildLikeButton(track, initialLiked);
        
        Button btnRemove = null;
        if (isOwner) {
            btnRemove = buildRemoveButton(track, row, data);
        }

        row.getChildren().addAll(lblIndex, thumbPane, titleBox, btnLike);
        if (btnRemove != null) {
            row.getChildren().add(btnRemove);
        }

        
        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: rgba(179,157,219,0.12); -fx-background-radius: 8; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;"));

        
        final Button finalBtnRemove = btnRemove;
        row.setOnMouseClicked(e -> {
            Object target = e.getTarget();
            if (target == btnLike || (finalBtnRemove != null && target == finalBtnRemove)) return;
            if (contentArea == null) return;
            com.example.core.AudioPlayer.getInstance().play(track, data.tracks);
            Runnable goPlaylist = () -> {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(new PlaylistDetailView(playlist, contentArea, onBack));
            };
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new TrackDetailView(track, initialLiked, goPlaylist, contentArea));
        });

        return row;
    }
    
    private Button buildLikeButton(TrackDTO track, boolean initialLiked) {
        SVGPath heartEmpty = new SVGPath();
        heartEmpty.setContent("M12.1 18.55l-.1.1l-.11-.1C7.14 14.24 4 11.39 4 8.5C4 6.5 5.5 5 7.5 5c1.54 0 3.04 1 3.57 2.36h1.86C13.46 6 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5c0 2.89-3.14 5.74-7.9 10.05M16.5 3c-1.74 0-3.41.81-4.5 2.08C10.91 3.81 9.24 3 7.5 3C4.42 3 2 5.41 2 8.5c0 3.77 3.4 6.86 8.55 11.53L12 21.35l1.45-1.32C18.6 15.36 22 12.27 22 8.5C22 5.41 19.58 3 16.5 3");
        heartEmpty.setFill(Color.WHITE);

        SVGPath heartFilled = new SVGPath();
        heartFilled.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.27 2 8.5C2 5.41 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.41 22 8.5c0 3.77-3.4 6.86-8.55 11.53z");
        heartFilled.setFill(Color.web("#B39DDB"));

        heartEmpty.setVisible(!initialLiked);
        heartFilled.setVisible(initialLiked);
        StackPane icon = new StackPane(heartEmpty, heartFilled);

        Button btn = new Button();
        btn.setGraphic(icon);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4px;");

        btn.setOnAction(e -> {
            e.consume();
            btn.setDisable(true);
            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return trackService.toggleLike(Session.getInstance().getUserId(), track.getId());
                }
            };
            task.setOnSucceeded(ev -> {
                boolean liked = task.getValue();
                heartEmpty.setVisible(!liked);
                heartFilled.setVisible(liked);
                btn.setDisable(false);
            });
            task.setOnFailed(ev -> btn.setDisable(false));
            new Thread(task).start();
        });
        return btn;
    }

    private Button buildRemoveButton(TrackDTO track, HBox row, PlaylistData data) {
        SVGPath trashIcon = new SVGPath();
        trashIcon.setContent("M7.616 20q-.691 0-1.153-.462T6 18.384V6H5.5q-.213 0-.356-.144T5 5.499t.144-.356T5.5 5H9q0-.31.23-.54q.23-.23.54-.23h4.46q.31 0 .54.23q.23.23.23.54h3.5q.213 0 .356.144t.144.357t-.144.356T18.5 6H18v12.385q0 .69-.462 1.152T16.384 20zM17 6H7v12.385q0 .269.173.442t.443.173h8.769q.269 0 .442-.173t.173-.442zM9.808 17q.213 0 .357-.144t.143-.356V8.5q0-.213-.144-.356T9.807 8t-.356.144t-.143.356V16.5q0 .213.144.356t.357.144m4.385 0q.213 0 .356-.144t.143-.356V8.5q0-.213-.144-.356T14.193 8t-.357.144t-.143.356V16.5q0 .213.144.356t.357.144M7 6v13z");
        trashIcon.setFill(Color.web("#e74c3c"));
        trashIcon.setScaleX(0.85);
        trashIcon.setScaleY(0.85);

        Button btn = new Button();
        btn.setGraphic(trashIcon);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 4px;");

        btn.setOnAction(e -> {
            e.consume();
            btn.setDisable(true);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    playlistService.removeTrackFromPlaylist(playlist.getId(), track.getId());
                    return null;
                }
            };
            task.setOnSucceeded(ev -> {
                VBox parent = (VBox) row.getParent();
                parent.getChildren().remove(row);
                data.tracks.remove(track);
                renumberRows(parent);
                if (data.tracks.isEmpty()) {
                    Label empty = new Label("This playlist is empty.");
                    empty.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
                    empty.setPadding(new Insets(20, 0, 0, 0));
                    parent.getChildren().add(empty);
                }
            });
            task.setOnFailed(ev -> btn.setDisable(false));
            new Thread(task).start();
        });
        return btn;
    }

    private void renumberRows(VBox list) {
        int i = 1;
        for (var node : list.getChildren()) {
            if (node instanceof HBox row && !row.getChildren().isEmpty()) {
                if (row.getChildren().get(0) instanceof Label lbl) {
                    lbl.setText(String.valueOf(i++));
                }
            }
        }
    }

    private void editCover() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Cover Art");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selected = fc.showOpenDialog((Stage) this.getScene().getWindow());
        if (selected == null) return;

        Task<PlaylistDTO> task = new Task<>() {
            @Override
            protected PlaylistDTO call() throws Exception {
                return playlistService.uploadCover(playlist.getId(), Session.getInstance().getUserId(), selected);
            }
        };
        task.setOnSucceeded(e -> {
            playlist = task.getValue();
            // recargamos la vista para que muestre la portada nueva
            this.getChildren().clear();
            this.getChildren().add(new Label("Updating cover..."));
            loadContent();
        });
        task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Could not update cover", task.getException().getMessage()));
        new Thread(task).start();
    }

    private void confirmAndDelete() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete playlist \"" + playlist.getName() + "\"? This cannot be undone.",
                ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirm delete");
        confirm.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    playlistService.deletePlaylist(playlist.getId(), Session.getInstance().getUserId());
                    return null;
                }
            };
            task.setOnSucceeded(e -> onBack.run());
            task.setOnFailed(e -> showAlert(Alert.AlertType.ERROR, "Could not delete playlist", task.getException().getMessage()));
            new Thread(task).start();
        });
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert a = new Alert(type, content == null ? "" : content, ButtonType.OK);
        a.setHeaderText(header);
        a.showAndWait();
    }

    private boolean isOwner() {
        Long me = Session.getInstance().getUserId();
        return me != null && playlist.getCreatorId() != null && me.equals(playlist.getCreatorId());
    }
    
    private static class PlaylistData {
        List<TrackDTO> tracks;
        Set<Long> likedIds;
        PlaylistData(List<TrackDTO> tracks, Set<Long> likedIds) {
            this.tracks = tracks;
            this.likedIds = likedIds;
        }
    }
}