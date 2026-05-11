package com.example.controllers;

import com.example.core.Router;
import com.example.core.Session;
import com.example.models.User;
import com.example.services.AuthService;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginView extends VBox{

    private final AuthService authService = new AuthService();

    public LoginView(){
        this.getStyleClass().add("glass-panel");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setPrefSize(600, 400);

        Label lblTitle = new Label("LOGIN");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username or Email");
        txtUsername.setMaxWidth(250);
        txtUsername.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #B39DDB; -fx-prompt-text-fill: gray;");
        txtUsername.getStyleClass().add("now-playing-title");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setMaxWidth(250);
        txtPassword.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #B39DDB; -fx-prompt-text-fill: gray;");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnBack = new Button("BACK");
        btnBack.getStyleClass().add("pill-button");

        Button btnLogin = new Button("LOGIN");
        btnLogin.getStyleClass().add("pill-button");
        btnLogin.setStyle("-fx-background-color: #B39DDB; -fx-text-fill: #1e1e1e;");

        buttonBox.getChildren().addAll(btnBack, btnLogin);
        this.getChildren().addAll(lblTitle, txtUsername, txtPassword, lblError, buttonBox);

        btnBack.setOnAction(e -> Router.navigateTo("WELCOME"));

        btnLogin.setOnAction(e -> {
            String user = txtUsername.getText();
            String pass = txtPassword.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Please fill in all fields.");
                return;
            }

            btnLogin.setDisable(true);
            lblError.setText("");

            Task<User> loginTask = new Task<>() {
                @Override
                protected User call() throws Exception {
                    return authService.login(user, pass);
                }
            };

            loginTask.setOnSucceeded(event -> {
                Session.getInstance().setUsername(loginTask.getValue().getUsername());
                Session.getInstance().setUserId(loginTask.getValue().getUserId());
                Router.navigateTo("MAIN");
                btnLogin.setDisable(false);
            });

            loginTask.setOnFailed(event -> {
                lblError.setText(loginTask.getException().getMessage());
                btnLogin.setDisable(false);
            });

            new Thread(loginTask).start();
        });
    }
}
