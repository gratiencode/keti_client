/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import com.sun.javafx.PlatformUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dizitart.no2.Nitrite;
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

    public static Nitrite initDatabase() {
        String path, fpath;
        if (PlatformUtil.isWindows()) {
            path = "C:\\Keti\\datastore";
            fpath = path + "\\nitrikdb.db";
        } else {
            path = "/home/" + System.getProperty("user.name") + "/Keti/datastore";
            fpath = path + "/nitrikdb.db";
        }
        File folder = new File(path);
        File file = null;
        boolean dir = folder.exists();
        if (!dir) {
            dir = folder.mkdir();
        }
        System.out.println("Droit Folder " + dir);
        if (dir) {
            file = new File(path+"/nitrikdb.db");
            // System.out.println("Droit "+file.canWrite());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(KetiGateController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return Nitrite.builder()
                .filePath(fpath)
                .compressed()
                .openOrCreate();
    }

    @Override
    public void start(Stage stage) throws Exception {
        UIController uiController = new UIController();
        uiController.loadScreen(Constants.LOGIN, Constants.LOGIN_SCREEN);
        uiController.loadScreen(Constants.CAISSES, Constants.CAISSE_VIEW);
        uiController.loadScreen(Constants.CHARGEMENT, Constants.CHARGEMENT_VIEW);
        uiController.loadScreen(Constants.PERFORMANCE, Constants.PERFORMANCE_VIEW);
        uiController.loadScreen(Constants.CLIENTS, Constants.CLIENT_VIEW);
        uiController.loadScreen(Constants.DEPENSE, Constants.DEPENSE_VIEW);
        uiController.loadScreen(Constants.RETRAIT, Constants.RETRAIT_VIEW);
        uiController.loadScreen(Constants.VEHICULES, Constants.VEHICULE_VIEW);
        uiController.loadScreen(Constants.SUCURSAL, Constants.SUCURSAL_VIEW);
        uiController.setScreen(Constants.LOGIN);

        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(uiController);
        Scene scene = new Scene(root,1112,565);
        stagex = stage;
        rootx = root;
        stage.setScene(scene);
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
        stage.centerOnScreen();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
