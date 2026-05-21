package com.example.components;

import java.io.File;
import java.util.List;

import com.example.core.Session;
import com.example.models.PlaylistDTO;
import com.example.models.ProfileDTO;
import com.example.models.TrackDTO;
import com.example.services.UserService;

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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ProfileView extends VBox {

    private final UserService userService = new UserService();
    private final Long targetUserId;
    private final StackPane contentArea;
    private final Runnable onBack;
    private ProfileDTO profile;

    public ProfileView(Long targetUserId, StackPane contentArea, Runnable onBack) {
        this.targetUserId = targetUserId;
        this.contentArea = contentArea;
        this.onBack = onBack;

        this.setSpacing(0);
        this.setPadding(new Insets(30));
        this.setAlignment(Pos.TOP_LEFT);

        Label lblLoading = new Label("Loading profile...");
        lblLoading.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
        this.getChildren().add(lblLoading);

        loadProfile();
    }

    private void loadProfile() {
        Long requesterId = Session.getInstance().getUserId();

        Task<ProfileDTO> task = new Task<>() {
            @Override
            protected ProfileDTO call() throws Exception {
                return userService.getProfile(targetUserId, requesterId);
            }
        };

        task.setOnSucceeded(e -> {
            profile = task.getValue();
            this.getChildren().clear();
            this.getChildren().add(buildScroll());
        });

        task.setOnFailed(e -> {
            this.getChildren().clear();
            Label err = new Label("Could not load the profile");
            err.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13px;");
            this.getChildren().add(err);
        });

        new Thread(task).start();
    }

    private ScrollPane buildScroll() {
        VBox content = new VBox(25);
        content.getChildren().addAll(
                buildBackButton(),
                buildHeader(),
                buildTracksSection(),
                buildPlaylistsSection()
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private Button buildBackButton() {
        Button btn = new Button("← Back");
        btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #B39DDB;" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;"
        );
        btn.setOnAction(e -> onBack.run());
        return btn;
    }

    private HBox buildHeader() {
        boolean isOwnProfile = isOwnProfile();

        StackPane avatarPane = buildAvatar(isOwnProfile);

        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label lblUsername = new Label(profile.getUsername());
        lblUsername.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #eeeeee;"
        );

        HBox counters = new HBox(25);
        counters.setAlignment(Pos.CENTER_LEFT);
        counters.getChildren().addAll(
                buildCounter(profile.getFollowersCount(), "Followers"),
                buildCounter(profile.getFollowingCount(), "Following"),
                buildCounter(profile.getTracks() != null ? profile.getTracks().size() : 0, "Tracks"),
                buildCounter(profile.getPlaylists() != null ? profile.getPlaylists().size() : 0, "Playlists")
        );

        infoBox.getChildren().addAll(lblUsername, counters);

        if (!isOwnProfile) {
            Button btnFollow = buildFollowButton();
            infoBox.getChildren().add(btnFollow);
        }

        HBox header = new HBox(25, avatarPane, infoBox);
        header.setPadding(new Insets(10, 0, 10, 0));
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private StackPane buildAvatar(boolean isOwnProfile) {
        StackPane pane = new StackPane();
        pane.setPrefSize(120, 120);
        pane.setMinSize(120, 120);

        Circle bg = new Circle(60, Color.web("#2a2a2a"));

        boolean hasPic = false;
        if (profile.getProfilePicturePath() != null) {
            try {
                String safePath = profile.getProfilePicturePath().replace(" ", "%20");
                Image img = new Image("file:" + safePath);
                if (!img.isError()) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(120);
                    iv.setFitHeight(120);
                    iv.setPreserveRatio(false);
                    Circle clip = new Circle(60, 60, 60);
                    iv.setClip(clip);
                    pane.getChildren().add(iv);
                    hasPic = true;
                }
            } catch (Exception ignored) {}
        }
        if (!hasPic) {
            bg.setFill(Color.web("#B39DDB"));
            pane.getChildren().add(bg);
        }

        if (isOwnProfile) {
            SVGPath pencilIcon = new SVGPath();
            pencilIcon.setContent("M20.71 7.04c.39-.39.39-1.04 0-1.41l-2.34-2.34c-.37-.39-1.02-.39-1.41 0l-1.84 1.83l3.75 3.75M3 17.25V21h3.75L17.81 9.93l-3.75-3.75z");
            pencilIcon.setFill(Color.WHITE);
            pencilIcon.setScaleX(1.2);
            pencilIcon.setScaleY(1.2);
            pencilIcon.setVisible(false);

            Rectangle overlayBg = new Rectangle(120, 120);
            overlayBg.setFill(Color.rgb(0, 0, 0, 0.5));
            Circle overlayClip = new Circle(60, 60, 60);
            overlayBg.setClip(overlayClip);
            overlayBg.setVisible(false);

            pane.getChildren().addAll(overlayBg, pencilIcon);
            pane.setStyle("-fx-cursor: hand;");

            pane.setOnMouseEntered(e -> {
                overlayBg.setVisible(true);
                pencilIcon.setVisible(true);
            });
            pane.setOnMouseExited(e -> {
                overlayBg.setVisible(false);
                pencilIcon.setVisible(false);
            });
            pane.setOnMouseClicked(e -> changeProfilePicture());
        }
        return pane;
    }

    private VBox buildCounter(int count, String label) {
        VBox box = new VBox(2);
        box.setAlignment(Pos.CENTER);

        Label lblCount = new Label(String.valueOf(count));
        lblCount.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #eeeeee;");

        Label lblLabel = new Label(label);
        lblLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

        box.getChildren().addAll(lblCount, lblLabel);
        return box;
    }

    private Button buildFollowButton() {
        boolean following = profile.isFollowing();
        Button btn = new Button(following ? "Unfollow" : "Follow");
        applyFollowStyle(btn, following);

        btn.setOnAction(e -> {
            btn.setDisable(true);
            boolean currentlyFollowing = "Unfollow".equals(btn.getText());
            Long myId = Session.getInstance().getUserId();

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    if (currentlyFollowing) {
                        userService.unfollow(myId, targetUserId);
                    } else {
                        userService.follow(myId, targetUserId);
                    }
                    return null;
                }
            };
            task.setOnSucceeded(ev -> {
                boolean nowFollowing = !currentlyFollowing;
                btn.setText(nowFollowing ? "Unfollow" : "Follow");
                applyFollowStyle(btn, nowFollowing);
                btn.setDisable(false);
                updateFollowersCount(nowFollowing ? 1 : -1);
            });
            task.setOnFailed(ev -> {
                btn.setDisable(false);
                showAlert("Could not update follow status");
            });
            new Thread(task).start();
        });
        return btn;
    }

    private void applyFollowStyle(Button btn, boolean following) {
        if (following) {
            btn.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.08);" +
                    "-fx-text-fill: #eeeeee;" +
                    "-fx-background-radius: 20;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 6 20 6 20;" +
                    "-fx-font-size: 13px;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: #B39DDB;" +
                    "-fx-text-fill: #1e1e1e;" +
                    "-fx-background-radius: 20;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 6 20 6 20;" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;"
            );
        }
    }

    private void updateFollowersCount(int delta) {
        try {
            ScrollPane scroll = (ScrollPane) this.getChildren().get(0);
            VBox content = (VBox) scroll.getContent();
            HBox header = (HBox) content.getChildren().get(1);
            VBox infoBox = (VBox) header.getChildren().get(1);
            HBox counters = (HBox) infoBox.getChildren().get(1);
            VBox followersBox = (VBox) counters.getChildren().get(0);
            Label lblCount = (Label) followersBox.getChildren().get(0);
            int current = Integer.parseInt(lblCount.getText());
            lblCount.setText(String.valueOf(current + delta));
        } catch (Exception ignored) {}
    }

    private VBox buildTracksSection() {
        List<TrackDTO> tracks = profile.getTracks();
        VBox section = new VBox(12);

        Label lblTitle = new Label("Tracks");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");
        section.getChildren().add(lblTitle);

        if (tracks == null || tracks.isEmpty()) {
            Label empty = new Label("No tracks uploaded yet.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
            section.getChildren().add(empty);
            return section;
        }

        FlowPane grid = new FlowPane();
        grid.setHgap(12);
        grid.setVgap(12);

        Runnable goProfile = () -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new ProfileView(targetUserId, contentArea, onBack));
        };

        for (TrackDTO track : tracks) {
            grid.getChildren().add(new TrackCard(track, false, contentArea, goProfile));
        }

        section.getChildren().add(grid);
        return section;
    }

    private VBox buildPlaylistsSection() {
        List<PlaylistDTO> playlists = profile.getPlaylists();
        VBox section = new VBox(12);

        Label lblTitle = new Label("Playlists");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");
        section.getChildren().add(lblTitle);

        if (playlists == null || playlists.isEmpty()) {
            Label empty = new Label("No playlists yet.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");
            section.getChildren().add(empty);
            return section;
        }

        FlowPane grid = new FlowPane();
        grid.setHgap(12);
        grid.setVgap(12);

        Runnable goProfile = () -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new ProfileView(targetUserId, contentArea, onBack));
        };

        for (PlaylistDTO pl : playlists) {
            grid.getChildren().add(new PlaylistCard(pl, contentArea, goProfile));
        }

        section.getChildren().add(grid);
        return section;
    }

    private void changeProfilePicture() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Profile Picture");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selected = fc.showOpenDialog((Stage) this.getScene().getWindow());
        if (selected == null) return;

        Task<com.example.models.User> task = new Task<>() {
            @Override
            protected com.example.models.User call() throws Exception {
                return userService.updateProfilePicture(targetUserId, selected);
            }
        };
        task.setOnSucceeded(e -> {
            com.example.models.User updated = task.getValue();
            Session.getInstance().setProfilePicturePath(updated.getProfilePicturePath());
            this.getChildren().clear();
            Label lbl = new Label("Updating...");
            lbl.setStyle("-fx-text-fill: gray;");
            this.getChildren().add(lbl);
            loadProfile();
        });
        task.setOnFailed(e -> showAlert("Could not update profile picture"));
        new Thread(task).start();
    }

    private void showAlert(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private boolean isOwnProfile() {
        Long myId = Session.getInstance().getUserId();
        return myId != null && myId.equals(targetUserId);
    }
}