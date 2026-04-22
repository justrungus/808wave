package com.example.components;

import com.example.core.Session;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class MainContentPanel extends VBox{

    private double xOffset = 0;
    private double yOffset = 0;

    private Label profileName;
    
    private StackPane contentArea;

    //vistas
    private UploadView uploadView;

    public MainContentPanel(){
        //estilo css custom del panel
        this.getStyleClass().add("glass-panel");

        //vistas
        uploadView = new UploadView();

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

        //main content area
        contentArea = new StackPane();
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        this.getChildren().add(contentArea);
    }

    
    //metodo para poder usar iconos
    private SVGPath makeIcon(String svgPathData) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPathData);
        
        icon.setFill(Color.web("#4a4a4a")); 
        return icon;
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

        String pathUpload = "M6.616 19q-.691 0-1.153-.462T5 17.384v-1.923q0-.213.143-.356t.357-.144t.357.144t.143.356v1.923q0 .231.192.424t.423.192h10.77q.23 0 .423-.192t.192-.424v-1.923q0-.213.143-.356t.357-.144t.357.144t.143.356v1.923q0 .691-.462 1.153T17.384 19zM11.5 6.927L9.529 8.898q-.146.146-.347.153t-.366-.159q-.16-.165-.163-.353q-.003-.189.163-.354l2.618-2.62q.132-.13.268-.183q.137-.053.298-.053t.298.053t.268.184l2.618 2.619q.147.146.154.344q.006.198-.153.363q-.166.166-.357.169t-.357-.163L12.5 6.927v8.15q0 .214-.143.357t-.357.143t-.357-.143t-.143-.357z";
        btnUpload.setGraphic(makeIcon(pathUpload));

        String pathHome = "M5 19v-8.692q0-.384.172-.727t.474-.565l5.385-4.078q.423-.323.966-.323t.972.323l5.385 4.077q.303.222.474.566q.172.343.172.727V19q0 .402-.299.701T18 20h-3.384q-.344 0-.576-.232q-.232-.233-.232-.576v-4.769q0-.343-.232-.575q-.233-.233-.576-.233h-2q-.343 0-.575.233q-.233.232-.233.575v4.77q0 .343-.232.575T9.385 20H6q-.402 0-.701-.299T5 19";
        String pathLibrary = "M12.192 14.692q.839 0 1.42-.58t.58-1.42v-5.73h1.962q.31 0 .54-.22q.229-.22.229-.55q0-.31-.23-.539t-.54-.23h-1.96q-.31 0-.54.23t-.23.54v5.038q-.248-.27-.556-.404q-.307-.135-.675-.135q-.838 0-1.419.581t-.58 1.42t.58 1.419t1.42.58M8.115 17q-.691 0-1.153-.462T6.5 15.385V4.615q0-.69.463-1.153T8.116 3h10.769q.69 0 1.153.462t.462 1.153v10.77q0 .69-.462 1.152T18.884 17zm-3 3q-.691 0-1.153-.462T3.5 18.385V7.115q0-.213.143-.356T4 6.616t.357.143t.143.357v11.269q0 .23.192.423t.423.192h11.27q.213 0 .356.143t.144.357t-.144.357t-.356.143z";

        btnLibrary.setGraphic(makeIcon(pathLibrary));
        btnHome.setGraphic(makeIcon(pathHome));

        btnHome.getStyleClass().add("pill-button");
        btnLibrary.getStyleClass().add("pill-button");
        btnUpload.getStyleClass().add("pill-button");

        leftPill.getChildren().addAll(btnHome, btnLibrary, btnUpload);


        //logica boton upload
        btnUpload.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(uploadView);
        });

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

        profileName = new Label("Guest");
        

        //botones
        Button btnNotifications = new Button();
        Button btnMaximize = new Button("🗖");
        Button btnClose = new Button();

        String pathNoti ="M5.5 18.77q-.213 0-.356-.145T5 18.268t.144-.356t.356-.143h1.116V9.846q0-1.96 1.24-3.447T11 4.546V4q0-.417.291-.708q.291-.292.707-.292t.709.292T13 4v.546q1.904.365 3.144 1.853t1.24 3.447v7.923H18.5q.213 0 .356.144q.144.144.144.357t-.144.356t-.356.143zm6.497 2.615q-.668 0-1.14-.475t-.472-1.14h3.23q0 .67-.475 1.142q-.476.472-1.143.472";
        btnNotifications.setGraphic(makeIcon(pathNoti));

        String pathClose = "m12 12.708l-5.246 5.246q-.14.14-.344.15t-.364-.15t-.16-.354t.16-.354L11.292 12L6.046 6.754q-.14-.14-.15-.344t.15-.364t.354-.16t.354.16L12 11.292l5.246-5.246q.14-.14.345-.15q.203-.01.363.15t.16.354t-.16.354L12.708 12l5.246 5.246q.14.14.15.345q.01.203-.15.363t-.354.16t-.354-.16z";
        btnClose.setGraphic(makeIcon(pathClose));
        //TO DO WHEN NOTIFICATION
        //M11.997 21.385q-.668 0-1.14-.475q-.472-.474-.472-1.14h3.23q0 .67-.475 1.143q-.476.472-1.143.472M5.5 18.769q-.213 0-.356-.144T5 18.268t.144-.356t.356-.143h1.116V9.846q0-1.96 1.24-3.447T11 4.546V4q0-.417.291-.708q.291-.292.707-.292t.709.292T13 4v.075q-.442.616-.683 1.342q-.24.727-.24 1.487q0 1.998 1.395 3.422t3.374 1.463h.27q.134 0 .268-.02v6H18.5q.213 0 .356.144q.144.144.144.357t-.144.356t-.356.143zm9.692-10.094q-.73-.728-.73-1.769t.728-1.772t1.77-.73t1.77.728t.732 1.77t-.729 1.771t-1.77.73t-1.77-.728

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

    public void refreshUserData(){
        String currentUserName = Session.getInstance().getUsername();
        if (currentUserName != null) {
        this.profileName.setText(currentUserName);
    }
    }
}
