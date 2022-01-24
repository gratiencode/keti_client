/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.Constants;

/**
 *
 * @author eroot
 */
public class Keti_client extends Application {

    public static Stage stagex;
    public static Parent rootx;

    private double xOffset = 0;
    private double yOffset = 0;

   

    @Override
    public void start(Stage stage) throws Exception {
        UIController uiController = new UIController();
        uiController.loadScreen(Constants.LOGIN, Constants.LOGIN_SCREEN);
        uiController.loadScreen(Constants.MAIN, Constants.MAIN_SCREEN);
        uiController.loadScreen(Constants.CAISSES, Constants.CAISSE_VIEW);
        uiController.loadScreen(Constants.CHARGEMENT, Constants.CHARGEMENT_VIEW);
        uiController.loadScreen(Constants.CLIENTS, Constants.CLIENT_VIEW);
        uiController.loadScreen(Constants.DEPENSE, Constants.DEPENSE_VIEW);
        uiController.loadScreen(Constants.RETRAIT, Constants.RETRAIT_VIEW);
        uiController.loadScreen(Constants.VEHICULES, Constants.VEHICULE_VIEW);
        uiController.loadScreen(Constants.SUCURSAL, Constants.SUCURSAL_VIEW);
        uiController.setScreen(Constants.LOGIN);

        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(uiController);
        Scene scene = new Scene(root);
        stagex = stage;
        rootx = root;
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.initStyle(StageStyle.UNDECORATED);

        root.setOnMousePressed((javafx.scene.input.MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged((javafx.scene.input.MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
