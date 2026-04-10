package com.example.components;

import atlantafx.base.theme.Styles;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MainContentPanel extends VBox{

    private double xOffset = 0;
    private double yOffset = 0;

    public MainContentPanel(){
        //estilo css custom del panel
        this.getStyleClass().add("glass-panel");

        

        //barra superior para arrastrar
        HBox topBar = new HBox(7);
        topBar.setAlignment(Pos.CENTER);
        topBar.setStyle("-fx-cursor: hand; -fx-background-color: rgba(255, 0, 0, 0);");
        topBar.setPadding(new Insets(10, 10, 10, 10));
        makeDraggable(topBar);
        this.getChildren().add(topBar);

        topBar.getChildren().addAll(
            leftPill(),
            searchFieldPill(),
            rightPill()
        );



    }

    


    //metodo para hacer draggeable la aplicacion
    private void makeDraggable(Node node){
        node.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        node.setOnMouseDragged(event ->{
            Stage stage = (Stage) this.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }


    private HBox leftPill(){
        HBox leftPill = new HBox(5);
        leftPill.getStyleClass().add("pill-box");

        Button btnHome = new Button("Home");
        Button btnLibrary = new Button("Library");
        Button btnUpload = new Button("Upload");

        btnHome.getStyleClass().add("pill-button");
        btnLibrary.getStyleClass().add("pill-button");
        btnUpload.getStyleClass().add("pill-button");

        leftPill.getChildren().addAll(btnHome, btnLibrary, btnUpload);

        return leftPill;    
    }

    private TextField searchFieldPill(){
        TextField searchField = new TextField("Search bar");
        searchField.getStyleClass().add(Styles.ROUNDED);

        searchField.getStyleClass().add("search-pill");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        return searchField;
    }

    private HBox rightPill(){
        HBox rightPill = new HBox(10);
        rightPill.setAlignment(Pos.CENTER);
        rightPill.getStyleClass().add("pill-box");
        
        //avatar
        Circle avatar = new Circle(12);
        avatar.setFill(Color.LIGHTGREY);
        
        // avatar.setFill(new ImagePattern(new Image("file:ruta/a/tu/imagen.jpg")));
        // O si está en resources: new Image(getClass().getResource("/tu_imagen.png").toExternalForm())

        Label profileName = new Label("Username");

        //botones
        Button btnNotifications = new Button("!!");
        Button btnMaximize = new Button("🗖");
        Button btnClose = new Button("✖");

        btnNotifications.getStyleClass().add("pill-button");
        btnMaximize.getStyleClass().add("pill-button");
        btnClose.getStyleClass().add("pill-button");

        //logica botones
        // btnMaximize.setOnAction(event -> {
        //     Stage stage = (Stage) rightPill.getScene().getWindow();
        //     // Alterna entre maximizado y tamaño normal
        //     stage.setMaximized(!stage.isMaximized()); 
        // });

        // Cerrar aplicación
        btnClose.setOnAction(event -> {
            // Cierra la aplicacion
            Platform.exit(); 
        });
        rightPill.getChildren().addAll(avatar, profileName, btnNotifications, btnMaximize, btnClose);

        return rightPill;
    }
}
