package com.example;

import atlantafx.base.theme.PrimerLight;
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
    public void start(Stage stage) {

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        MainController raiz = new MainController();

        Scene scene = new Scene(raiz, 1366, 769);

        String css = getClass().getResource("/com/example/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        

        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT); 
        
        
        stage.setScene(scene);
        stage.show();
        
        centerWindow(stage);        
    }

    public static void main(String[] args) {
        launch();
    }

    private void centerWindow(Stage stage){
        Rectangle2D limitesPantalla = Screen.getPrimary().getVisualBounds();
        
        double centroX = (limitesPantalla.getWidth() - stage.getWidth()) / 2;
        double centroY = (limitesPantalla.getHeight() - stage.getHeight()) / 2;
        
        stage.setX(centroX);
        stage.setY(centroY);
    }

}
