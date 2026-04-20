package com.example.controllers;

import com.example.core.Router;
import com.example.models.User;
import com.example.services.AuthService;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegisterView extends VBox{
    private final AuthService authService = new AuthService();
    public RegisterView() {
        this.getStyleClass().add("glass-panel");
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
        this.setPrefSize(600, 400);

        Label lblTitle = new Label("REGISTER");
        lblTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #B39DDB;");

        String inputStyle = "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #B39DDB; -fx-prompt-text-fill: gray;";
        

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email Address");
        txtEmail.setMaxWidth(250);
        txtEmail.setStyle(inputStyle);
        txtEmail.getStyleClass().add("now-playing-title");


        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        txtUsername.setMaxWidth(250);
        txtUsername.setStyle(inputStyle);
        txtUsername.getStyleClass().add("now-playing-title");


        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setMaxWidth(250);
        txtPassword.setStyle(inputStyle);

        PasswordField txtConfirmPassword = new PasswordField();
        txtConfirmPassword.setPromptText("Confirm Password");
        txtConfirmPassword.setMaxWidth(250);
        txtConfirmPassword.setStyle(inputStyle);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 10 0 0 0;");

        Button btnBack = new Button("BACK");
        btnBack.getStyleClass().add("pill-button");
        

        Button btnRegister = new Button("SIGN UP");
        btnRegister.getStyleClass().add("pill-button");
        btnRegister.setStyle("-fx-background-color: #B39DDB; -fx-text-fill: #1e1e1e;");
        
        btnBack.setOnAction(e -> Router.navigateTo("WELCOME"));
        btnRegister.setOnAction(e -> {
            String user = txtUsername.getText();
            String mail = txtEmail.getText();
            String pass = txtPassword.getText();
            try{
                User newUser = authService.register(user, mail, pass);
                com.example.core.Session.getInstance().setUsername(newUser.getUsername());
                com.example.core.Router.navigateTo("MAIN");
            }catch (Exception ex) {
        
                System.err.println("Error en el registro: " + ex.getMessage());
            }
        });

        buttonBox.getChildren().addAll(btnBack, btnRegister);
        this.getChildren().addAll(lblTitle, txtEmail, txtUsername, txtPassword, txtConfirmPassword, buttonBox);
    }
}
