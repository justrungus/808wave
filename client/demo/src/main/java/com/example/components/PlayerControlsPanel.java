package com.example.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class PlayerControlsPanel extends HBox{
    

    public PlayerControlsPanel(){
        this.getStyleClass().add("glass-panel");
        HBox pill = mainPill();
        HBox.setHgrow(pill, Priority.ALWAYS);
        this.setPadding(new Insets(20, 20, 20, 20));

        this.getChildren().add(
            pill
        );
    }

    //metodo para poder usar iconos
    private SVGPath makeIcon(String svgPathData) {
        SVGPath icon = new SVGPath();
        icon.setContent(svgPathData);
        
        icon.setFill(Color.web("#B39DDB")); 
        icon.setScaleX(1.3);
        icon.setScaleY(1.3);
        return icon;
    }

    private HBox mainPill(){
        HBox mainPill = new HBox(5);
        // mainPill.getStyleClass().add("pill-box");
        mainPill.setAlignment(Pos.CENTER);

        Button btnVolume = new Button();
        Button btnLoop = new Button();
        Button btnPlay = new Button();
        Button btnBack = new Button();
        Button btnNext = new Button();
        Button btnSettings = new Button();
        Button btnShare = new Button();
        Button btnLike = new Button();
        Button btnDownload = new Button();

        btnVolume.getStyleClass().add("pill-button");
        btnLoop.getStyleClass().add("pill-button");
        btnPlay.getStyleClass().add("pill-button");
        btnBack.getStyleClass().add("pill-button");
        btnNext.getStyleClass().add("pill-button");
        btnSettings.getStyleClass().add("pill-button");
        btnShare.getStyleClass().add("pill-button");
        btnLike.getStyleClass().add("pill-button");
        btnDownload.getStyleClass().add("pill-button");

        String pathVol = "M18.692 11.975q0-2.056-1.11-3.749q-1.109-1.693-2.978-2.524q-.202-.098-.3-.278t-.012-.362q.093-.208.317-.268t.45.039q2.099.979 3.366 2.893t1.267 4.249t-1.267 4.249t-3.366 2.893q-.226.099-.45.039t-.317-.267q-.086-.183.012-.363t.3-.278q1.869-.83 2.979-2.524t1.11-3.749M7.73 14H5.116q-.349 0-.578-.23t-.23-.578v-2.384q0-.349.23-.578t.578-.23H7.73l2.685-2.685q.244-.244.568-.116t.324.476v8.65q0 .348-.324.476t-.568-.116zm8.077-2q0 .82-.302 1.555q-.302.736-.846 1.278q-.173.13-.378.041t-.205-.312V9.389q0-.223.205-.313t.377.041q.545.548.847 1.306T15.808 12";
        btnVolume.setGraphic(makeIcon(pathVol));

        String pathLoop ="m5.927 18.192l1.735 1.735q.146.146.153.344q.006.198-.153.363q-.166.166-.357.169t-.357-.162l-2.382-2.383q-.131-.131-.184-.268q-.053-.136-.053-.298t.053-.298t.184-.267l2.382-2.383q.146-.146.347-.153t.367.159q.16.165.162.354t-.162.354l-1.735 1.734h10.765q.27 0 .443-.173t.173-.442v-2.885q0-.213.143-.356t.357-.144t.357.144t.143.356v2.885q0 .671-.472 1.143t-1.144.472zM18.073 6.808H7.308q-.27 0-.442.173q-.174.173-.174.442v2.885q0 .213-.143.357t-.357.143t-.356-.143t-.144-.357V7.423q0-.671.472-1.143t1.144-.472h10.765l-1.734-1.735q-.147-.146-.153-.344t.153-.363q.165-.166.356-.169q.192-.003.357.163l2.383 2.382q.13.131.183.268q.053.136.053.298t-.053.298q-.052.136-.183.267l-2.383 2.383q-.146.146-.347.153t-.366-.159q-.16-.165-.163-.354t.163-.354z";
        btnLoop.setGraphic(makeIcon(pathLoop));
        
        String pathPlay ="M9 15.714V8.287q0-.368.242-.588t.566-.22q.106 0 .214.028q.109.027.215.083l5.842 3.733q.186.13.28.298q.093.167.093.379t-.093.379t-.28.298l-5.843 3.733q-.105.055-.214.083q-.108.028-.214.028q-.323 0-.566-.22T9 15.714";
        btnPlay.setGraphic(makeIcon(pathPlay));
        
        String pathBack ="M6.73 16.116V7.885q0-.214.144-.357q.143-.144.357-.144t.357.144t.143.356v8.231q0 .214-.143.357q-.144.143-.357.143t-.357-.143q-.143-.143-.143-.357m9.277-.332l-4.662-3.112q-.186-.13-.27-.295T10.992 12t.084-.376q.084-.164.27-.295l4.662-3.112q.106-.08.217-.105t.237-.025q.323 0 .565.217t.242.59v6.212q0 .373-.242.59q-.242.218-.565.218q-.125 0-.237-.025t-.217-.106";
        btnBack.setGraphic(makeIcon(pathBack));
        
        String pathNext ="M16.27 16.116V7.885q0-.214.143-.357q.143-.144.356-.144t.357.144t.143.356v8.231q0 .214-.143.357t-.357.143t-.356-.143q-.144-.143-.144-.357m-9.538-1.01v-6.21q0-.374.242-.591q.243-.218.566-.218q.125 0 .236.025t.217.106l4.662 3.112q.187.13.27.295t.084.376t-.084.376q-.083.164-.27.295l-4.662 3.112q-.105.08-.217.106t-.237.025q-.323 0-.565-.218q-.242-.217-.242-.59";
        btnNext.setGraphic(makeIcon(pathNext));
        
        String pathSettings ="M10.96 21q-.349 0-.605-.229q-.257-.229-.319-.571l-.263-2.092q-.479-.145-1.036-.454q-.556-.31-.947-.664l-1.915.824q-.317.14-.644.03t-.504-.415L3.648 15.57q-.177-.305-.104-.638t.348-.546l1.672-1.25q-.045-.272-.073-.559q-.03-.288-.03-.559q0-.252.03-.53q.028-.278.073-.626l-1.672-1.25q-.275-.213-.338-.555t.113-.648l1.06-1.8q.177-.287.504-.406t.644.021l1.896.804q.448-.373.97-.673q.52-.3 1.013-.464l.283-2.092q.061-.342.318-.571T10.96 3h2.08q.349 0 .605.229q.257.229.319.571l.263 2.112q.575.202 1.016.463t.909.654l1.992-.804q.318-.14.645-.021t.503.406l1.06 1.819q.177.306.104.638t-.348.547L18.36 10.92q.082.31.092.569t.01.51q0 .233-.02.491q-.019.259-.088.626l1.69 1.27q.275.213.358.546t-.094.638l-1.066 1.839q-.176.306-.513.415q-.337.11-.654-.03l-1.923-.824q-.467.393-.94.673t-.985.445l-.264 2.111q-.061.342-.318.571t-.605.23zm1.013-6.5q1.046 0 1.773-.727T14.473 12t-.727-1.773t-1.773-.727q-1.052 0-1.776.727T9.473 12t.724 1.773t1.776.727";
        btnSettings.setGraphic(makeIcon(pathSettings));
        
        String pathShare ="M6.616 21q-.691 0-1.153-.462T5 19.385v-8.77q0-.69.463-1.152T6.616 9H8.23q.213 0 .357.143t.143.357t-.143.357T8.23 10H6.616q-.231 0-.424.192T6 10.616v8.769q0 .23.192.423t.423.192h10.77q.23 0 .423-.192t.192-.423v-8.77q0-.23-.192-.423T17.384 10H15.77q-.213 0-.357-.143T15.27 9.5t.143-.357T15.77 9h1.615q.691 0 1.153.463T19 10.616v8.769q0 .69-.463 1.153T17.385 21zm5.027-5.643Q11.5 15.214 11.5 15V4.614L9.754 6.36q-.146.146-.344.153q-.199.006-.364-.16q-.16-.164-.162-.353t.162-.354l2.388-2.388q.132-.131.268-.184q.137-.053.298-.053t.298.053t.268.184l2.388 2.388q.14.14.15.342q.01.2-.15.366q-.166.165-.357.165t-.357-.165l-1.74-1.74V15q0 .214-.143.357T12 15.5t-.357-.143";
        btnShare.setGraphic(makeIcon(pathShare));
        
        String pathLike ="M20.385 9q.627 0 1.12.494T22 10.616v1.23q0 .137-.028.298q-.028.162-.083.298l-2.731 6.474q-.206.461-.693.773q-.486.311-1.003.311H9.269q-.671 0-1.143-.472t-.472-1.144V9.672q0-.323.133-.628t.351-.522l5.156-5.112q.222-.215.494-.27t.516.059t.35.373q.108.258.04.579L13.665 9zM4.615 20q-.67 0-1.143-.472Q3 19.056 3 18.385v-7.77q0-.67.472-1.143Q3.944 9 4.616 9h.423q.67 0 1.143.472q.472.472.472 1.144v7.788q0 .671-.472 1.133Q5.71 20 5.039 20z";
        btnLike.setGraphic(makeIcon(pathLike));
        
        String pathDownload ="M5.5 20h13q.213 0 .356.144t.144.357t-.144.356T18.5 21h-13q-.213 0-.356-.144T5 20.499t.144-.356T5.5 20m6.13-3.379q-.164-.08-.295-.242l-3.989-5.292q-.298-.404-.077-.851t.723-.447h1.643V3.808q0-.343.232-.576T10.442 3h3.097q.343 0 .575.232t.232.576v5.98h1.643q.502 0 .723.448q.22.447-.077.85l-4.008 5.293q-.122.161-.294.242q-.173.081-.356.081t-.347-.08";
        btnDownload.setGraphic(makeIcon(pathDownload));

        Label lblActualTime = new Label("0:00");
        lblActualTime.getStyleClass().add("slide-label");

        

        Label lblMaxTime = new Label("3:40");
        lblMaxTime.getStyleClass().add("slide-label");


        //slider con relleno

        Slider timeSlider = new Slider(0, 100, 50);
        timeSlider.getStyleClass().add("player-slider");
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMaxWidth(550);

        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Buscamos la pieza interna del slider que se llama "track" 
            javafx.scene.Node track = timeSlider.lookup(".track");
            
            if (track != null) {
                // Calculamos el porcentaje donde está la bolita
                double percent = (newValue.doubleValue() / timeSlider.getMax()) * 100;
                
                // Creamos un gradiente que corta exactamente en el porcentaje actual
                String estilo = "-fx-background-color: linear-gradient(to right, #B39DDB " + percent + "%, rgba(255,255,255,0.15) " + percent + "%);";
                
                // Le aplicamos el color directamente a la barra
                track.setStyle(estilo);
            }
        });


        mainPill.getChildren().addAll(btnVolume, btnLoop, btnPlay, btnBack, lblActualTime, timeSlider, lblMaxTime, btnNext, btnSettings, btnShare, btnLike, btnDownload);

        return mainPill;
    }

    




}
