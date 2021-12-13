/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import com.sun.javafx.PlatformUtil;
import core.KetiAPI;
import core.KetiHelper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.dizitart.no2.Nitrite;
import util.Constants;
import util.ScreensChangeListener;

import util.Token;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class MainController implements Initializable, ScreensChangeListener {

    UIController controler;
    String token;
    String username;

    Nitrite db;

    @FXML
    private TextField searchField;

    private static String CURRENT_VIEW = "Dashboard-ui";

    @FXML
    private AnchorPane mainpane;

    @FXML
    private ImageView succursale;

    @FXML
    private ImageView home, chargement, cclient, cvehicule, caisse, depense, dechargement, retrait,performance;
    @FXML
    private Label pane_title;
    @FXML
    private Label connected_user_;

    Pane topane;
    @FXML
    Pane showPane;

    KetiAPI KETI;
    Preferences pref;

    private static MainController instance;

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }

    @FXML
    private void exit(MouseEvent event) {
        db.compact();
        db.commit();
        db.close();
        System.exit(0);
    }

    @FXML
    private void onHoverHome(MouseEvent event) {
        ImageView img = (ImageView) event.getSource();
        MainUI.setShadowEffect(img);
    }

    @FXML
    private void onOutHome(MouseEvent event) {
        ImageView img = (ImageView) event.getSource();
        MainUI.removeShaddowEffect(img);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        KETI = KetiHelper.createService(token);
        KetiHelper.setOnTokenRefreshCallback((Token var1) -> {
            token = var1.getToken();
            pref.put("KetiToken", token);
        });
        installTooltips();
        initDb();
//        search();
        System.out.println("Initializing");
        // MainController.mainPane = mainpane;
        // MainController.connexion = connected_user_;
        //  initSuccursaleTable();
    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

    private void initDb() {
        if (PlatformUtil.isWindows()) {
            db = Nitrite.builder()
                    .filePath("C:\\Keti\\datastore\\nitrikdb.db")
                    .compressed()
                    .openOrCreate("admin", "Admin*22");
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
            db = Nitrite.builder()
                    .filePath(file)
                    .compressed()
                    .autoCommitBufferSize(16)
                    .openOrCreate("admin", "Admin*22");
        }

    }

    private void installTooltips() {
        Tooltip thome = new Tooltip();
        thome.setText("Accueil");
        thome.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(home, thome);
        Tooltip tclient = new Tooltip();
        tclient.setText("Clients");
        tclient.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(cclient, tclient);
        Tooltip tvehicule = new Tooltip();
        tvehicule.setText("Vehicules");
        tvehicule.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(cvehicule, tvehicule);
        Tooltip tchargement = new Tooltip();
        tchargement.setText("Chargements du vehicule");
        tchargement.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(chargement, tchargement);
        Tooltip tdepense = new Tooltip();
        tdepense.setText("Dépenses");
        tdepense.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(depense, tdepense);
        Tooltip tdechargement = new Tooltip();
        tdechargement.setText("Dechargements du vehicule");
        tdechargement.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(dechargement, tdechargement);

        Tooltip tcaisse = new Tooltip();
        tcaisse.setText("Comptes financiers");
        tcaisse.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(caisse, tcaisse);

        Tooltip tretrait = new Tooltip();
        tretrait.setText("Retrait des colis");
        tretrait.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(retrait, tretrait);
        Tooltip succ = new Tooltip();
        succ.setText("Agences");
        succ.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(succursale, succ);
         Tooltip perf = new Tooltip();
        perf.setText("Performances");
        perf.setStyle("-fx-font: normal bold 14 Langdon; "
                + "-fx-base: #EEEEEE; "
                + "-fx-text-fill: white;");
        Tooltip.install(performance, perf);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        KETI = KetiHelper.createService(token);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        connected_user_.setText(username);
    }

    @FXML
    public void switchToSuccursalle(MouseEvent evt) {
        Pane p = MainUI.getPage(this, "agencies.fxml", token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Succursale");
        CURRENT_VIEW = Constants.SUCURSAL;

    }

    @FXML
    public void switchToClients(MouseEvent evt) {
        gotoClients();
    }

    public void gotoClients() {
        Pane p = MainUI.getPage(this, Constants.CLIENT_VIEW, token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Clients");
        CURRENT_VIEW = Constants.CLIENTS;
    }

    @FXML
    public void switchToVehicule(MouseEvent evt) {
        gotoVehicule();
    }

    public void gotoVehicule() {
        Pane p = MainUI.getPage(this,Constants.VEHICULE_VIEW, token, db);
        System.out.println("elt " + mainpane);
        ObservableList<Node> nodes = getMainpane().getChildren();
        nodes.remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Vehicules");
        CURRENT_VIEW = Constants.VEHICULES;
    }

    private void setNode(Node node) {
        mainpane.getChildren().clear();
        mainpane.getChildren().add(node);
    }

    public void loadPage(Pane pane, String res) {
        try {
            pane = FXMLLoader.load(getClass().getResource(res));
            setNode(pane);
        } catch (IOException ex) {
            Logger.getLogger(MainUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void switchToChargement(MouseEvent evt) {
        gotoChargements();
    }

    public void gotoChargements() {
        Pane p = MainUI.getPage(this, "chargement.fxml", token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Chargement");
        CURRENT_VIEW = Constants.CHARGEMENT;
    }

    @FXML
    public void switchToDashBoard(MouseEvent evt) {
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(showPane);
        pane_title.setText("Tableau de bord");

    }

    @FXML
    public void switchToDepense(MouseEvent evt) {
        Pane p = MainUI.getPage(this, "depenses.fxml", token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Dépenses");

    }

    @FXML
    public void switchToDechargement(MouseEvent evt) {
        Pane p = MainUI.getPage(this, "dechargement.fxml", token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Déchargement");

    }

    @FXML
    public void switchToCaisse(MouseEvent evt) {
        Pane p = MainUI.getPage(this, "caisse.fxml", token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Finance");

    }

    @FXML
    public void switchToRetrait(MouseEvent evt) {
        Pane p = MainUI.getPage(this, "retrait.fxml", token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Retrait de marchandise");

    }

    private Pane getMainpane() {
        return mainpane;
    }

    private void search() {
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (CURRENT_VIEW.equals(Constants.SUCURSAL)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        SuccursalController.getInstance().datanize();
                    } else {
                        SuccursalController.getInstance().filter(searchField.getText());
                    }
                } else if (CURRENT_VIEW.equals(Constants.VEHICULES)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        VehiculeController.getInstance().refresh();
                    } else {
                        VehiculeController.getInstance().filter(searchField.getText());
                    }
                }else if (CURRENT_VIEW.equals(Constants.CLIENTS)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        VehiculeController.getInstance().refresh();
                    } else {
                        VehiculeController.getInstance().filter(searchField.getText());
                    }
                }else if (CURRENT_VIEW.equals(Constants.CHARGEMENT)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        VehiculeController.getInstance().refresh();
                    } else {
                        VehiculeController.getInstance().filter(searchField.getText());
                    }
                }else if (CURRENT_VIEW.equals(Constants.DECHARGEMENT)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        VehiculeController.getInstance().refresh();
                    } else {
                        VehiculeController.getInstance().filter(searchField.getText());
                    }
                }else if (CURRENT_VIEW.equals(Constants.DEPENSE)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        VehiculeController.getInstance().refresh();
                    } else {
                        VehiculeController.getInstance().filter(searchField.getText());
                    }
                }else if (CURRENT_VIEW.equals(Constants.CAISSES)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        VehiculeController.getInstance().refresh();
                    } else {
                        VehiculeController.getInstance().filter(searchField.getText());
                    }
                }else if (CURRENT_VIEW.equals(Constants.RETRAIT)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        VehiculeController.getInstance().refresh();
                    } else {
                        VehiculeController.getInstance().filter(searchField.getText());
                    }
                }
            }
        });

    }

    @FXML
    public void search(KeyEvent evt) {
        search();
    }

}
