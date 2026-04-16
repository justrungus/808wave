package com.example;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        showWelcome();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        centerWindow(stage);
    }

    

    private void centerWindow(Stage stage){
        Rectangle2D limitesPantalla = Screen.getPrimary().getVisualBounds();
        
        double centroX = (limitesPantalla.getWidth() - stage.getWidth()) / 2;
        double centroY = (limitesPantalla.getHeight() - stage.getHeight()) / 2;
        
        stage.setX(centroX);
        stage.setY(centroY);
    }

    public void showWelcome(){
        WelcomeView welcome = new WelcomeView(this::showLogin, this::showRegister);
        setRoot(welcome, 600, 400);
    }

    public void showLogin(){
        LoginView login = new LoginView(this::showWelcome, this::showMainApp);
        setRoot(login, 600, 400);
    }

    public void showRegister(){
        RegisterView register = new RegisterView(this::showWelcome, this::showMainApp);
        setRoot(register, 600, 450);
    }

    public void showMainApp(){
        MainController main = new MainController(); 
        setRoot(main, 1366, 769);
        
    }

    private void setRoot(javafx.scene.Parent root, double width, double height){
        if (scene == null){
            scene = new Scene(root, width, height);
            scene.setFill(Color.TRANSPARENT);

            String css = getClass().getResource("/com/example/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } else {
            scene.setRoot(root);
            primaryStage.setWidth(width);
            primaryStage.setHeight(height);
        }
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch();
    }
}
