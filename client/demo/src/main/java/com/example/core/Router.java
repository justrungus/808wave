package com.example.core;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Router {
    private static Stage window;
    private static final Map<String, Scene> routes = new HashMap<>();

    public static void initialize(Stage primaryStage){
        window = primaryStage;
    }
    
    public static void addRoute(String name, Scene scene) {
        routes.put(name, scene);
    }

    public static void navigateTo(String name) {
        Scene scene = routes.get(name);
        if (scene != null) {
            window.setScene(scene);
            window.show();
        } else {
            System.err.println(name + "Does not exist");
        }
    }
}
