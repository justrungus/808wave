package com.example;

import com.example.controllers.LoginView;
import com.example.controllers.MainController;
import com.example.controllers.RegisterView;
import com.example.controllers.WelcomeView;
import com.example.core.Router;

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

    

    @Override
    public void start(Stage primaryStage) {

        
        Router.initialize(primaryStage);

        
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        
        WelcomeView welcome = new WelcomeView();
        LoginView login = new LoginView();
        RegisterView register = new RegisterView();
        MainController main = new MainController();

        
        Router.addRoute("WELCOME", createScene(welcome, 600, 400));
        Router.addRoute("LOGIN", createScene(login, 600, 400));
        Router.addRoute("REGISTER", createScene(register, 600, 450));
        Router.addRoute("MAIN", createScene(main, 1366, 769));

        
        Router.navigateTo("WELCOME");

        centerWindow(primaryStage);
    }

    
    private Scene createScene(javafx.scene.Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.setFill(Color.TRANSPARENT);

        
        try {
            String css = getClass().getResource("/com/example/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Aviso: No se encontró style.css, continuando sin él.");
        }

        return scene;
    }

    private void centerWindow(Stage stage){
        Rectangle2D limitesPantalla = Screen.getPrimary().getVisualBounds();
        
        double centroX = (limitesPantalla.getWidth() - stage.getWidth()) / 2;
        double centroY = (limitesPantalla.getHeight() - stage.getHeight()) / 2;
        
        stage.setX(centroX);
        stage.setY(centroY);
    }

    public static void main(String[] args) {
        launch();
    }
}
