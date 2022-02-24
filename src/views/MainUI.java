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
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import keti_client.ChargementController;
import keti_client.ClientController;
import keti_client.DepenseController;
import keti_client.FinanceController;
import keti_client.MainController;
import keti_client.MarchandiseController;
import keti_client.PanierController;
import keti_client.SuccursalController;
import keti_client.TransporterController;
import keti_client.VehiculeController;
import keti_client.KetiGateController;
import keti_client.PerformanceController;
import keti_client.RetraitController;
import keti_client.UtilisateurController;
import model.Comptefin;
import model.Marchandise;
import model.Succursale;
import model.Tiers;
import model.Vehicule;
import org.controlsfx.control.Notifications;
import org.dizitart.no2.Nitrite;
import util.Constants;
import util.LoginResult;

/**
 *
 * @author eroot
 */
public class MainUI {

    private static double xOffset = 0;
    private static double yOffset = 0;
    public static Stage mainStage;

    public static void replaceView(Class theClass, String fxmlPath, double h, double w, String user, String token, LoginResult loginr) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(theClass.getResource(fxmlPath));
            Parent main = fxmlLoader.load();
            MainController controller = fxmlLoader.<MainController>getController();
            controller.setUsername(user);
            controller.setToken(token);
            controller.setLonginResult(loginr);
            mainStage = new Stage();
            Scene scene = new Scene(main);
            mainStage.initStyle(StageStyle.UNDECORATED);
            mainStage.setScene(scene);
            mainStage.show();
            mainStage.getScene().setRoot(main);
            mainStage.getScene().getWindow().setHeight(h);
            mainStage.getScene().getWindow().setWidth(w);
//            mainStage.getScene().getWindow().setX(1);
//            mainStage.getScene().getWindow().setY(1);
            mainStage.centerOnScreen();
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

    public static double preferedWidth() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth() - (bounds.getWidth() * 0.12);
        return w;
    }

    public static double preferedHeight() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double h = bounds.getHeight() - (bounds.getHeight() * 0.2);
        return h;
    }

    public static void floatDialog(String res, int w, int h, String token, Nitrite db, Object... objs) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource(res));
        try {
            Parent load = fxmlLoader.load();
            switch (res) {
                case Constants.TRANSPORTER_DLG:
                    Succursale suk = (Succursale) objs[0];
                    TransporterController controller = fxmlLoader.<TransporterController>getController();
                    controller.setToken(token);
                    controller.setLocalDatabase(db);
                    controller.setCurrentSuccursale(suk);
                    break;
                case Constants.MARCHANDISE_DLG:
                    MarchandiseController mcontroller = fxmlLoader.<MarchandiseController>getController();
                    mcontroller.setLocalDatabase(db);
                    break;
                case Constants.ADDTOCART_DLG:
                    Tiers trs = (Tiers) objs[0];
                    Marchandise m = (Marchandise) objs[1];
                    Vehicule veh = (Vehicule) objs[2];
                    Integer track = (Integer) objs[3];
                    Succursale s = (Succursale) objs[4];
                    PanierController pcontroller = fxmlLoader.<PanierController>getController();
                    pcontroller.setMarchandise(m);
                    pcontroller.setTiers(trs);
                    pcontroller.setVehicule(veh);
                    pcontroller.setTracking(track);
                    pcontroller.setSuccursale(s);
                    break;
                case Constants.USERCREATOR_DLG:
                    UtilisateurController xcontroller = fxmlLoader.<UtilisateurController>getController();
                    xcontroller.setToken(token);
                    xcontroller.setDatabase(db);
                    break;

            }
            Scene scene = new Scene(load, w, h);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.centerOnScreen();
            load.setOnMousePressed((javafx.scene.input.MouseEvent event) -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            load.setOnMouseDragged((javafx.scene.input.MouseEvent event) -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
            stage.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Initializable getLoadedController(Initializable init, String ress) {
        FXMLLoader fxmlLoader = new FXMLLoader(init.getClass().getResource(ress));
        try {
            fxmlLoader.load();
            return fxmlLoader.getController();
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Pane getPage(Initializable init, String ress, String token, Nitrite db, Object... objs) {
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
                    Succursale sc = (Succursale) objs[0];
                    ChargementController chcontr = fxmlLoader.<ChargementController>getController();
                    chcontr.setToken(token);
                    chcontr.setLocalDatabase(db);
                    chcontr.setCurrentSuccursale(sc);
                    break;
                case Constants.CLIENT_VIEW:
                    ClientController cclt = fxmlLoader.<ClientController>getController();
                    cclt.setToken(token);
                    cclt.setLocalDatabase(db);
                    break;
                case Constants.VEHICULE_VIEW:
                    VehiculeController vctrl = fxmlLoader.<VehiculeController>getController();
                    vctrl.setToken(token);
                    vctrl.setDatabase(db);
                    break;
                case Constants.CAISSE_VIEW:
                    Succursale sci = (Succursale) objs[0];
                    FinanceController fc = fxmlLoader.<FinanceController>getController();
                    fc.setSucursaleId(sci);
                    fc.setToken(db, token);
                    break;
                case Constants.DEPENSE_VIEW:
                    Object o = objs[0];
                    sc = (Succursale) objs[1];
                    Comptefin cpt = o != null ? (Comptefin) o : null;
                    DepenseController dpc = fxmlLoader.<DepenseController>getController();
                    dpc.setCompteFinSelected(cpt);
                    dpc.setToken(db, token);
                    dpc.setSuccursale(sc);
                    break;
                case Constants.RETRAIT_VIEW:
                    RetraitController rc = fxmlLoader.<RetraitController>getController();
                    rc.setLocalDatabase(db);
                    rc.setToken(token);
                    break;
                case Constants.PERFORMANCE_VIEW:
                    PerformanceController pc = fxmlLoader.<PerformanceController>getController();
                    pc.setToken(db, token);
                    break;

            }

            return main;
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void notify(Node graph, String title, String message, double duration, String... type) {
        Notifications n = Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(duration));
        String tp = type[0];
        if (graph == null) {
            if (tp.equalsIgnoreCase("warning")) {
                n.showWarning();
            } else if (tp.equalsIgnoreCase("error")) {
                n.showError();
            } else {
                n.showInformation();
            }
        } else {
            n.graphic(graph);
            n.show();
        }
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
                        Logger.getLogger(KetiGateController.class.getName()).log(Level.SEVERE, null, ex);
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
