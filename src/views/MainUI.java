/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import com.sun.javafx.PlatformUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import keti_client.ChargementController;
import keti_client.ClientController;
import keti_client.Keti_client;
import keti_client.MainController;
import keti_client.SuccursalController;
import keti_client.UIController;
import keti_client.VehiculeController;
import keti_client.keti_UIController;
import org.controlsfx.control.Notifications;
import org.dizitart.no2.Nitrite;
import util.Constants;

/**
 *
 * @author eroot
 */
public class MainUI {

    private static double xOffset = 0;
    private static double yOffset = 0;
    public static Stage mainStage;

    public static void replaceView(Class theClass, String fxmlPath, int h, int w, String user, String token) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(theClass.getResource(fxmlPath));
            Parent main = fxmlLoader.load();
            MainController controller = fxmlLoader.<MainController>getController();
            controller.setUsername(user);
            controller.setToken(token);
            mainStage = new Stage();
            Scene scene = new Scene(main);
            mainStage.initStyle(StageStyle.UNDECORATED);
            mainStage.setScene(scene);
            mainStage.show();
            mainStage.getScene().setRoot(main);
            mainStage.getScene().getWindow().setHeight(h);
            mainStage.getScene().getWindow().setWidth(w);
            mainStage.getScene().getWindow().setX(1);
            mainStage.getScene().getWindow().setY(1);
            main.setOnMousePressed((javafx.scene.input.MouseEvent event) -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            main.setOnMouseDragged((javafx.scene.input.MouseEvent event) -> {
                mainStage.setX(event.getScreenX() - xOffset);
                mainStage.setY(event.getScreenY() - yOffset);
            });
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setShadowEffect(Node node) {
        if (Platform.isSupported(ConditionalFeature.EFFECT)) {
            node.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.8)));
        }
    }

    public static void removeShaddowEffect(Node node) {
        if (Platform.isSupported(ConditionalFeature.EFFECT)) {
            node.setEffect(null);
        }
    }
     public static <T> T element(String ress) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource(ress));
            return fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
     }
     
     public static Initializable getLoadedController(Initializable init, String ress){
         FXMLLoader fxmlLoader = new FXMLLoader(init.getClass().getResource(ress));
        try {
            fxmlLoader.load();
            return fxmlLoader.getController();
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
     }

    public static Pane getPage(Initializable init, String ress, String token, Nitrite db) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(init.getClass().getResource(ress));
            Pane main = fxmlLoader.load();
            switch (ress) {
                case Constants.SUCURSAL_VIEW:
                    SuccursalController controller = fxmlLoader.<SuccursalController>getController();
                    controller.setToken(token);
                    controller.setLocalDatabase(db);
                    
                    break;
                case Constants.CHARGEMENT_VIEW:
                    ChargementController ccontr = fxmlLoader.<ChargementController>getController();
                    ccontr.setToken(token);
                    ccontr.setLocalDatabase(db);
                    break;
                case Constants.CLIENT_VIEW:
                    ClientController cclt=fxmlLoader.<ClientController>getController();
                     cclt.setToken(token);
                     cclt.setLocalDatabase(db);break;
                case Constants.VEHICULE_VIEW:
                    VehiculeController vctrl=fxmlLoader.<VehiculeController>getController();
                    vctrl.setToken(token);
                    vctrl.setDatabase(db);
                    break;
                                       
            }

            return main;
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void notify(Node graph, String title, String message, double duration) {
        Notifications.create()
                .title(title)
                .graphic(graph)
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(duration))
                .show();
    }

    public static Nitrite initDatabase() {
        if (PlatformUtil.isWindows()) {
            Nitrite db = Nitrite.builder()
                    .filePath("C:\\Keti\\datastore\\nitrikdb.db")
                    .compressed()
                    .openOrCreate("admin", "Admin*22");
            return db;
        } else {
            File folder = new File("/home/" + System.getProperty("user.name") + "/Keti/datastore");
            File file = null;
            boolean dir = false;

            if (!folder.exists()) {
                dir = folder.mkdir();
            }
            if (dir) {
                file = new File("/home/" + System.getProperty("user.name") + "/Keti/datastore/nitrikdb.db");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException ex) {
                        Logger.getLogger(keti_UIController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            Nitrite db = Nitrite.builder()
                    .filePath(file)
                    .compressed()
                    .openOrCreate("admin", "Admin*22");
            return db;
        }

    }

}
