package com.example.components;

import com.example.core.Session;
import com.example.models.User;
import com.example.services.UserService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FriendsPanel extends VBox {

    private final UserService userService = new UserService();
    private ScheduledExecutorService scheduler;

    public FriendsPanel() {
        this.setSpacing(15);
        this.getStyleClass().add("glass-panel");
        this.setId("friends-sidebar");
        this.setMinWidth(200);
        this.setAlignment(Pos.TOP_CENTER);

        Label lblHeader = new Label("Friends");
        lblHeader.getStyleClass().add("friends-header");
        this.getChildren().add(lblHeader);

        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null && scheduler != null) {
                scheduler.shutdownNow();
            }
        });
    }

    public void reload() {
        Long userId = Session.getInstance().getUserId();
        if (userId == null) return;

        sendHeartbeat(userId);
        loadFriends(userId);

        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "friends-poll");
                t.setDaemon(true);
                return t;
            });
            scheduler.scheduleAtFixedRate(() -> {
                sendHeartbeat(userId);
                Platform.runLater(() -> loadFriends(userId));
            }, 30, 30, TimeUnit.SECONDS);
        }
    }

    private void sendHeartbeat(Long userId) {
        try { userService.heartbeat(userId); } catch (Exception ignored) {}
    }

    private void loadFriends(Long userId) {
        Task<Map<String, List<User>>> task = new Task<>() {
            @Override
            protected Map<String, List<User>> call() throws Exception {
                return userService.getFriendsWithStatus(userId);
            }
        };
        task.setOnSucceeded(e -> renderFriends(task.getValue()));
        task.setOnFailed(e -> {});
        new Thread(task).start();
    }

    private void renderFriends(Map<String, List<User>> data) {
        if (this.getChildren().size() > 1) {
            this.getChildren().remove(1, this.getChildren().size());
        }

        List<User> online = data.getOrDefault("online", List.of());
        List<User> offline = data.getOrDefault("offline", List.of());

        if (online.isEmpty() && offline.isEmpty()) {
            Label empty = new Label("No friends yet.\nFollow someone and\nget followed back!");
            empty.setStyle("-fx-text-fill: gray; -fx-font-size: 11px; -fx-text-alignment: center;");
            empty.setWrapText(true);
            this.getChildren().add(empty);
            return;
        }

        VBox listContainer = new VBox(8);

        if (!online.isEmpty()) {
            Label lblOnline = new Label("Online");
            lblOnline.getStyleClass().add("status-divider");
            listContainer.getChildren().add(lblOnline);
            for (User u : online) {
                listContainer.getChildren().add(new FriendItem(u, true));
            }
        }

        if (!offline.isEmpty()) {
            Label lblOffline = new Label("Offline");
            lblOffline.getStyleClass().add("status-divider");
            listContainer.getChildren().add(lblOffline);
            for (User u : offline) {
                listContainer.getChildren().add(new FriendItem(u, false));
            }
        }

        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        this.getChildren().add(scroll);
    }
}