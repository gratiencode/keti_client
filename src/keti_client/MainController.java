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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.Comptefin;
import model.Depense;
import model.Payer;
import model.Tiers;
import model.Transporter;
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import util.Constants;
import util.Datastorage;
import util.LoginResult;
import util.ScreensChangeListener;
import util.SyncEngine;

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
    private LoginResult longinResult;

     Nitrite db;
 

    @FXML
    private TextField searchField;

    private static String CURRENT_VIEW = "Dashboard-ui";

    @FXML
    private AnchorPane mainpane;

    @FXML
    private ImageView succursale;
    @FXML
    Label aretirer;
    @FXML
    Label nClt;
    @FXML
    Label nDepense;
    @FXML
    Label nCharg;
    @FXML
    private ImageView home, chargement, cclient, cvehicule, caisse, depense, dechargement, retrait, performance;
    @FXML
    private Label pane_title;
//    @FXML
//    private Label connected_user_;
    @FXML
    private AreaChart<?, ?> venteChart;
    Datastorage<Transporter> transdb;
    Datastorage<Tiers> tiers;
    Datastorage<Payer> dpenses;

    Pane topane;
    @FXML
    Pane showPane;
    @FXML
    PieChart piepane;
    @FXML
    SplitMenuButton user_connected;

    KetiAPI KETI;
    Preferences pref;

    SyncEngine engine;

    private static MainController instance;

    public MainController() {
        pref = Preferences.userNodeForPackage(Keti_client.class);
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

    private List<Tiers> pickTiers(List<Transporter> tc) {
        List<Tiers> result = new ArrayList<>();
        tc.forEach(tr -> {
            Tiers t = tr.getIdTiers();
            if (!result.contains(t)) {
                result.add(t);
            }
        });
        return result;
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
        installTooltips();
db=Keti_client.initDatabase();
     
 transdb = new Datastorage<>(db, Transporter.class);
        tiers = new Datastorage<>(db, Tiers.class);
        dpenses = new Datastorage(db, Payer.class);
    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

//    public static Nitrite initDb() {
//        if (PlatformUtil.isWindows()) {
//            db = Nitrite.builder()
//                    .filePath("C:\\Keti\\datastore\\nitrikdb.db")
//                    .compressed()
//                    .openOrCreate("admin", "Admin*22");
//        } else {
//            //" + System.getProperty("user.name") + "
//            File folder = new File("/home/" + System.getProperty("user.name") + "/Keti/datastore");
//            File file = null;
//            boolean dir = folder.exists();
//
//            if (!dir) {
//                dir = folder.mkdir();
//            }
//            System.out.println("Droit Folder " + dir);
//            if (dir) {
//                file = new File("/home/" + System.getProperty("user.name") + "/Keti/datastore/nitrikdb.db");
//                // System.out.println("Droit "+file.canWrite());
//                if (!file.exists()) {
//                    try {
//                        file.createNewFile();
//                    } catch (IOException ex) {
//                        Logger.getLogger(KetiGateController.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//
//            nb = Nitrite.builder()
//                    .compressed()
//                    .filePath(file);
//            db = nb.openOrCreate();
//
//        }
//        return db;
//       
//
//    }

    private void installTooltips() {
        Tooltip thome = new Tooltip();
        thome.setText("Tableau de bord");
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
        tdepense.setText("Ecritures");
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

    public void setToken(final String tken) {
        this.token = tken;
        System.out.println("Token " + tken);
        KETI = KetiHelper.createService(token);
        KetiHelper.setOnTokenRefreshCallback((Token var1) -> {
            this.token = var1.getToken();
            pref.put("KetiToken", token);
            System.err.println("Refresh token "+token);
        });
        SyncEngine se = new SyncEngine(KETI, db);
        se.start();

        double dep = figureOutExpense(dpenses.findAll());
        loadSaleChart();
        loadClientChart();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nDepense.setText(dep + "$");
            }
        });

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        user_connected.setText(username);
        //user_connected.getItems().clear();
        // connected_user_.setText(username);

    }

    public double figureOutExpense(List<Payer> payer) {
        double sommeDep = 0;
        LocalDate ld = LocalDate.now();
        int year = ld.getYear();
        int month = ld.getMonthValue();
        for (Payer p : payer) {
            Depense d = p.getDepenseId();
            if (d != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(p.getDatePayement());
                int pyear = cal.get(Calendar.YEAR);
                int pmonth = cal.get(Calendar.MONTH) + 1;
                if (pyear == year && month == pmonth) {
                    sommeDep += p.getMontantPaye();
                }
            }
        }
        return sommeDep;
    }

    private void loadSaleChart() {
        List<Tiers> trss = tiers.findAll();
        List<Transporter> ltr = transdb.findAll();
        List<Vehicule> lv = extractvehicules(ltr);
        for (Vehicule v : lv) {
            HashMap<String, Double> sume = sum(ltr, v.getPlaque());
            XYChart.Series ch = new XYChart.Series<>();
            ch.setName(v.getPlaque());
            for (Map.Entry<String, Double> mm : sume.entrySet()) {
                ch.getData().addAll(new XYChart.Data<>(Constants.getMonthName(mm.getKey()), mm.getValue()));
            }
            venteChart.setLegendVisible(true);
            venteChart.setLegendSide(Side.BOTTOM);
            if (!exist((ObservableList<Series<?, ?>>) venteChart.getData(), ch.getName())) {
                venteChart.getData().add(ch);
            }

        }
        nCharg.setText(String.valueOf(ltr.size()));
        aretirer.setText(String.valueOf(getNonRetiree(ltr)));
        nClt.setText(String.valueOf(trss.size()));
    }

    private boolean exist(ObservableList<Series<?, ?>> ls, String name) {
        for (Series s : ls) {
            if (s.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private long getNonRetiree(List<Transporter> ltr) {
        long i = 0;
        for (Transporter t : ltr) {
            if (t.getObservation().equalsIgnoreCase("A retirer")) {
                i++;
            }
        }
        return i;
    }

    private void loadClientChart() {
        List<Transporter> ltr = transdb.findAll();
        List<Tiers> lv = pickTiers(ltr);
        System.out.println("clts " + lv.size());
//        piepane.setPrefSize(356, 323);
        piepane.setLabelsVisible(true);
        piepane.setLegendVisible(false);

        for (Tiers v : lv) {
            if(v==null)continue;
            long sume = clientCount(ltr, v);
            PieChart.Data data = new PieChart.Data(v.getNom().charAt(0) + ". " + v.getPrenom(), sume);
            if (!existPie(piepane.getData(), data.getName())) {
                piepane.getData().add(data);
            }
        }

        dpenses = new Datastorage(db, Payer.class);
        double dep = figureOutExpense(dpenses.findAll());
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                nDepense.setText(dep + "$");
            }
        });

    }

    public boolean existPie(ObservableList<PieChart.Data> data, String name) {
        for (PieChart.Data d : data) {
            if (d.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public long clientCount(List<Transporter> ltr, Tiers t) {
        long count = 0;
        for (Transporter tr : ltr) {
            Tiers trs = tr.getIdTiers();
            if(trs==null)continue;
            if(t==null)continue;
            if (trs.getUid().equals(t.getUid())) {
                count++;
            }
        }
        return count;
    }

    public List<String> getMonths(List<Transporter> ltr) {
        List<String> result = new ArrayList<>();
        for (Transporter t : ltr) {
            String date = Constants.DateFormateur.format(t.getDateTransport());
            String ann = date.split("-")[0];
            String moi = date.split("-")[1];
            result.add(ann + "-" + moi);
        }
        return result;
    }

    HashMap<String, Double> sum(List<Transporter> ltr, String plak) {
        HashMap<String, Double> result = new HashMap<>();
        List<String> mon = getMonths(ltr);
        mon.forEach((s) -> {
            double som = 0;
            for (Transporter t : ltr) {
                Vehicule v = t.getIdVehicule();
                String mois = Constants.DateFormateur.format(t.getDateTransport());
                if (v.getPlaque().equals(plak) && mois.contains(s)) {
                    som += t.getPriceToPay();
                }
            }
            //  System.out.println("v = " + plak + " som " + som);
            result.put(s, som);
        });
        return result;
    }

    List<Vehicule> extractvehicules(List<Transporter> ltr) {
        List<Vehicule> rst = new ArrayList<>();
        for (Transporter t : ltr) {
            Vehicule v = t.getIdVehicule();
            if (!rst.contains(v)) {
                rst.add(v);
            }
        }
        return rst;
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
        Pane p = MainUI.getPage(this, Constants.VEHICULE_VIEW, token, db);
        System.out.println("elt " + mainpane);
        ObservableList<Node> nodes = mainpane.getChildren();
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
            Logger.getLogger(MainUI.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void switchToChargement(MouseEvent evt) {
        gotoChargements();
    }

    public void gotoChargements() {
        Pane p = MainUI.getPage(this, "chargement.fxml", token, db, longinResult.getSuccursale());
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Chargement");
        CURRENT_VIEW = Constants.CHARGEMENT;
    }

    @FXML
    public void switchToPerformance(MouseEvent me) {
        gotoPerformances();
    }

    public void gotoPerformances() {
        Pane p = MainUI.getPage(this, Constants.PERFORMANCE_VIEW, token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Performance");
        CURRENT_VIEW = Constants.PERFORMANCE;
    }

    @FXML
    public void switchToDashBoard(MouseEvent evt) {
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(showPane);
        pane_title.setText("Tableau de bord");
        loadSaleChart();
        loadClientChart();
    }

    @FXML
    public void switchToDepense(MouseEvent evt) {
        gotoecriture(null);
    }

    public void gotoecriture(Comptefin cpt) {
        Pane p = MainUI.getPage(this, "depenses.fxml", token, db, cpt, longinResult.getSuccursale());
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Ecritures");
        CURRENT_VIEW = Constants.DEPENSE;
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
        Pane p = MainUI.getPage(this, "finance.fxml", token, db, longinResult.getSuccursale());
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Finance");
        CURRENT_VIEW = Constants.CAISSES;
    }

    @FXML
    public void switchToRetrait(MouseEvent evt) {
        Pane p = MainUI.getPage(this, "retrait.fxml", token, db);
        mainpane.getChildren().remove(0);
        mainpane.getChildren().add(p);
        pane_title.setText("Retrait de marchandise");
        CURRENT_VIEW = Constants.RETRAIT;

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
                } else if (CURRENT_VIEW.equals(Constants.CLIENTS)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        ClientController.getInstance().refresh();
                    } else {
                        ClientController.getInstance().filter(searchField.getText());
                    }
                } else if (CURRENT_VIEW.equals(Constants.CHARGEMENT)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        ChargementController.getInstance().refresh(null);
                    } else {
                        ChargementController.getInstance().search(searchField.getText());
                    }
                } else if (CURRENT_VIEW.equals(Constants.PERFORMANCE)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        PerformanceController.getInstance().refresh();
                    } else {
                        PerformanceController.getInstance().search(searchField.getText());
                    }
                } else if (CURRENT_VIEW.equals(Constants.DEPENSE)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        DepenseController.getInstance().refresh();
                    } else {
                        DepenseController.getInstance().search(searchField.getText());
                    }
                } else if (CURRENT_VIEW.equals(Constants.CAISSES)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        FinanceController.getInstance().refresh();
                    } else {
                        FinanceController.getInstance().search(searchField.getText());
                    }
                } else if (CURRENT_VIEW.equals(Constants.RETRAIT)) {
                    System.out.println("Current view " + CURRENT_VIEW);
                    if (searchField.getText().isEmpty()) {
                        RetraitController.getInstance().populate();
                    } else {
                        RetraitController.getInstance().search(searchField.getText());
                    }
                }
            }
        });

    }

    @FXML
    public void search(KeyEvent evt) {
        search();
    }

    public void setLonginResult(LoginResult longinResult) {
        this.longinResult = longinResult;
        MenuItem m1 = new MenuItem("Utilisateurs");
        MenuItem m2 = new MenuItem("Quitter");
        String role = longinResult.getRole();
        user_connected.getItems().clear();
        if (role.equalsIgnoreCase("Administrator")
                || role.equalsIgnoreCase("Associe")
                || role.equalsIgnoreCase("Directeur")) {
            user_connected.getItems().add(m1);
        } else {
            user_connected.getItems().remove(m1);
        }
        user_connected.getItems().add(m2);
        m2.setOnAction(e -> {
            db.compact();
            db.commit();
            db.close();
            System.exit(0);
        });
        m1.setOnAction(e -> {
            //gestion des utilisateurs ici
            MainUI.floatDialog("utilisateurs.fxml", 603, 710, token, db);
        });
    }

}
