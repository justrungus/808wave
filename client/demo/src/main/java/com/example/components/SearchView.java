package com.example.components;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.core.Config;
import com.example.core.Session;
import com.example.models.PlaylistDTO;
import com.example.models.TrackDTO;
import com.example.models.User;
import com.example.services.PlaylistService;
import com.example.services.TrackService;
import com.example.services.UserService;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SearchView extends VBox {

    private final TrackService trackService = new TrackService();
    private final UserService userService = new UserService();
    private final PlaylistService playlistService = new PlaylistService();
    private final StackPane contentArea;
    private final boolean fullResults;
    private final String query;

    private static final int PREVIEW_LIMIT = 3;

    public SearchView(String query, boolean fullResults, StackPane contentArea) {
        this.query = query;
        this.fullResults = fullResults;
        this.contentArea = contentArea;

        this.setSpacing(0);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lblLoading = new Label("Searching...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
        this.getChildren().add(lblLoading);

        search();
    }

    private void search() {
        Long userId = Session.getInstance().getUserId();

        Task<SearchData> task = new Task<>() {
            @Override
            protected SearchData call() throws Exception {
                List<User> users = userService.searchUsers(query);
                List<TrackDTO> tracks = trackService.searchTracks(query);
                List<PlaylistDTO> playlists = playlistService.searchPlaylists(query);
                List<TrackDTO> liked = userId != null ? trackService.getLikedTracks(userId) : List.of();
                Set<Long> likedIds = liked.stream().map(TrackDTO::getId).collect(Collectors.toSet());
                return new SearchData(users, tracks, playlists, likedIds);
            }
        };

        task.setOnSucceeded(e -> render(task.getValue()));
        task.setOnFailed(e -> {
            this.getChildren().clear();
            Label err = new Label("Search failed.");
            err.setStyle("-fx-text-fill: #e74c3c;");
            this.getChildren().add(err);
        });

        new Thread(task).start();
    }

    private void render(SearchData data) {
        this.getChildren().clear();

        Label lblTitle = new Label(fullResults ? "Results for \"" + query + "\"" : "\"" + query + "\"");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #eeeeee;");

        VBox content = new VBox(30);
        content.getChildren().add(lblTitle);

        Runnable goBack = () -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new SearchView(query, fullResults, contentArea));
        };

        // USUARIOS
        List<User> users = fullResults ? data.users
                : data.users.stream().limit(PREVIEW_LIMIT).collect(Collectors.toList());
        if (!users.isEmpty()) {
            content.getChildren().add(buildSectionTitle("Users", data.users.size(), !fullResults));
            VBox userList = new VBox(6);
            for (User u : users) {
                userList.getChildren().add(buildUserRow(u));
            }
            content.getChildren().add(userList);
        }

        // TRACKS
        List<TrackDTO> tracks = fullResults ? data.tracks
                : data.tracks.stream().limit(PREVIEW_LIMIT).collect(Collectors.toList());
        if (!tracks.isEmpty()) {
            content.getChildren().add(buildSectionTitle("Tracks", data.tracks.size(), !fullResults));
            FlowPane trackGrid = new FlowPane();
            trackGrid.setHgap(12);
            trackGrid.setVgap(12);
            for (TrackDTO t : tracks) {
                boolean liked = data.likedIds.contains(t.getId());
                trackGrid.getChildren().add(new TrackCard(t, liked, contentArea, goBack, data.tracks));
            }
            content.getChildren().add(trackGrid);
        }

        // PLAYLISTS
        List<PlaylistDTO> playlists = fullResults ? data.playlists
                : data.playlists.stream().limit(PREVIEW_LIMIT).collect(Collectors.toList());
        if (!playlists.isEmpty()) {
            content.getChildren().add(buildSectionTitle("Playlists", data.playlists.size(), !fullResults));
            FlowPane playlistGrid = new FlowPane();
            playlistGrid.setHgap(12);
            playlistGrid.setVgap(12);
            for (PlaylistDTO pl : playlists) {
                playlistGrid.getChildren().add(new PlaylistCard(pl, contentArea, goBack));
            }
            content.getChildren().add(playlistGrid);
        }

        if (users.isEmpty() && tracks.isEmpty() && playlists.isEmpty()) {
            Label empty = new Label("No results for \"" + query + "\"");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");
            content.getChildren().add(empty);
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        this.getChildren().add(scroll);
    }

    private HBox buildSectionTitle(String title, int total, boolean isPreview) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(title.toUpperCase());
        lbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: gray;");
        row.getChildren().add(lbl);

        if (isPreview && total > PREVIEW_LIMIT) {
            Label seeAll = new Label("See all (" + total + ") →");
            seeAll.setStyle("-fx-font-size: 11px; -fx-text-fill: #B39DDB; -fx-cursor: hand;");
            seeAll.setOnMouseClicked(e -> {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(new SearchView(query, true, contentArea));
            });
            row.getChildren().add(seeAll);
        }

        return row;
    }

    private HBox buildUserRow(User user) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 10, 6, 10));
        row.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");

        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(36, 36);
        Circle bg = new Circle(18, Color.web("#B39DDB"));
        avatarPane.getChildren().add(bg);

        if (user.getProfilePicturePath() != null) {
            try {
                String safePath = (Config.SERVER_URL + "/" + user.getProfilePicturePath()).replace(" ", "%20");
                Image img = new Image(safePath);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(36);
                    iv.setFitHeight(36);
                    iv.setPreserveRatio(false);
                    Circle clip = new Circle(18, 18, 18);
                    iv.setClip(clip);
                    avatarPane.getChildren().clear();
                    avatarPane.getChildren().add(iv);
                }
            } catch (Exception ignored) {}
        }

        Label lblName = new Label(user.getUsername());
        lblName.setStyle("-fx-font-size: 13px; -fx-text-fill: #eeeeee; -fx-font-weight: bold;");

        row.getChildren().addAll(avatarPane, lblName);

        row.setOnMouseEntered(e -> row.setStyle(
                "-fx-background-color: rgba(179,157,219,0.12); -fx-background-radius: 8; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle(
                "-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;"));

        final Long uid = user.getUserId();
        row.setOnMouseClicked(e -> {
            contentArea.getChildren().clear();
            Runnable goBack = () -> {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(new SearchView(query, fullResults, contentArea));
            };
            contentArea.getChildren().add(new ProfileView(uid, contentArea, goBack));
        });

        return row;
    }

    private static class SearchData {
        List<User> users;
        List<TrackDTO> tracks;
        List<PlaylistDTO> playlists;
        Set<Long> likedIds;

        SearchData(List<User> users, List<TrackDTO> tracks,
                   List<PlaylistDTO> playlists, Set<Long> likedIds) {
            this.users = users;
            this.tracks = tracks;
            this.playlists = playlists;
            this.likedIds = likedIds;
        }
    }
}