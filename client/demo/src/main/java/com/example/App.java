package com.example;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        MainController raiz = new MainController();

        Scene scene = new Scene(raiz, 1280, 720);

        String css = getClass().getResource("/com/example/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        

        stage.initStyle(StageStyle.TRANSPARENT); 
        stage.centerOnScreen();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
