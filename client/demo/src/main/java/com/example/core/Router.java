package com.example.core;

import java.util.HashMap;
import java.util.Map;

import com.example.controllers.MainController;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Router {

    private static Stage window;
    private static final Map<String, Scene> routes = new HashMap<>();

    public static void initialize(Stage primaryStage) {
        window = primaryStage;
    }

    public static void addRoute(String name, Scene scene) {
        routes.put(name, scene);
    }

    public static void navigateTo(String name) {
        Scene scene = routes.get(name);
        if (scene != null) {

            // Si vamos a la pantalla principal...
            if (name.equals("MAIN")) {
                // 1. Comprobamos si la raíz es un MainController
                if (scene.getRoot() instanceof MainController) {
                    // 2. Creamos la variable y hacemos el cast manualmente
                    MainController controller = (MainController) scene.getRoot();
                    // 3. Llamamos al método
                    controller.refreshUserData();
                }
            }
        }

        window.setScene(scene);
        window.show();

    }
}
