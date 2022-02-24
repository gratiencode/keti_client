/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;
import model.Comptefin;
import model.Depense;
import model.Payer;
import model.Succursale;
import model.Transporter;
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import util.Constants;
import util.Datastorage;
import util.Performance;
import util.ScreensChangeListener;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class PerformanceController implements Initializable, ScreensChangeListener {

    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;
    int rowsDataCount = 20;
    private Nitrite localDatabase;
    Datastorage<Payer> datadepense;
    Datastorage<Transporter> datatrans;
    Datastorage<Vehicule> datavehicule;
    Datastorage<Succursale> datarsale;
    @FXML
    Label count;
    @FXML
    Label entree;
    @FXML
    Label sortie;
    @FXML
    Label result;
    @FXML
    DatePicker dpk_debut;
    @FXML
    DatePicker dpk_fin;
    @FXML
    TreeTableView<Performance> tbl_perfomance;
    @FXML
    TreeTableColumn<Performance, String> colmois;
    @FXML
    TreeTableColumn<Performance, String> colsucursal;
    @FXML
    TreeTableColumn<Performance, String> colvehicule;
    @FXML
    TreeTableColumn<Performance, Number> colrecette;
    @FXML
    TreeTableColumn<Performance, Number> coldepense;
    @FXML
    TreeTableColumn<Performance, Number> colredperte;
    @FXML
    TreeTableColumn<Performance, Number> colredfact;
    @FXML
    TreeTableColumn<Performance, Number> colresult;

    @FXML
    Pagination pagination;

    TreeItem<Performance> rootView;
    ObservableList<TreeItem<Performance>> treeItems;
    ObservableList<Performance> contenttable;
    @FXML
    ComboBox<Integer> rowPP;

    public void setPattern(DatePicker dtpk) {
        dtpk.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    private static PerformanceController INSTANCE;

    public PerformanceController() {
        INSTANCE = this;
    }

    public static PerformanceController getInstance() {
        return INSTANCE;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPattern(dpk_fin);
        setPattern(dpk_debut);
        rootView = new TreeItem<>(new Performance());
        tbl_perfomance.setRoot(rootView);
        tbl_perfomance.setShowRoot(false);
        configTable();
        treeItems = FXCollections.observableArrayList();
        contenttable = FXCollections.observableArrayList();
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowPP.setItems(rows);
        rowPP.getSelectionModel().select(0);
        pagination.setPageFactory(this::createDataPage);
    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

    private void configTable() {

        colmois.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, String> param) -> new SimpleStringProperty(Constants.DateFormateur.format(param.getValue().getValue().getDate())));
        colvehicule.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, String> param) -> new SimpleStringProperty(param.getValue().getValue().getVehicule().getMarque() + " " + param.getValue().getValue().getVehicule().getPlaque()));
        colsucursal.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, String> param) -> new SimpleStringProperty(param.getValue().getValue().getSuccursale().getNomSuccursale()));
        colrecette.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getRecette()));
        coldepense.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getDepense()));
        colredperte.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getReductionPerte()));
        colredfact.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getReductionFacture()));
        colresult.setCellValueFactory((TreeTableColumn.CellDataFeatures<Performance, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getResultat()));
    }

    public void setToken(Nitrite db, String token) {
        datavehicule = new Datastorage<>(db, Vehicule.class);
        datadepense = new Datastorage<>(db, Payer.class);
        datatrans = new Datastorage<>(db, Transporter.class);
        datarsale = new Datastorage<>(db, Succursale.class);
        this.token = token;
        keti = KetiHelper.createService(token);
        System.err.println("Datarsale " + datarsale.findAll().size());
        refresh();
    }

    public void populate(List<Vehicule> lvh, List<Transporter> Listransp, List<Payer> listDep, List<Succursale> sucs, long date1, long date2) {
        treeItems.clear();
        for (Vehicule v : lvh) {

            double rct = calculateRecette(Listransp, v, date1, date2);
            //d'autres ligne du tree item
            double dep = calculateDepense(listDep, v, date1, date2);

            double redP = calculateRedPerte(Listransp, v, date1, date2);
            double rst = (rct - dep) - redP;
            Performance p = new Performance();
            p.setDate(new Date());
            System.err.println("de sum " + dep);
            p.setDepense(dep);
            p.setRecette(rct);
            Succursale sk = new Succursale();
            sk.setNomSuccursale("Toutes les succursales");
            p.setSuccursale(sk);
            p.setReductionPerte(redP);
            p.setResultat(rst);
            p.setVehicule(v);
            TreeItem<Performance> parent = new TreeItem<>(p);
            for (Succursale s : sucs) {
                Double dp = sumDepenseByVehiculeBySuccursale(listDep, s.getUid(), v.getPlaque(), date1, date2);
                Performance pkid = new Performance();
                pkid.setDate(new Date());
                pkid.setDepense(dp);
                pkid.setRecette(0d);
                pkid.setSuccursale(s);
                pkid.setReductionPerte(0);
                pkid.setResultat(0d);
                pkid.setVehicule(v);
                TreeItem<Performance> kid = new TreeItem<>(pkid);
                if (dp != 0) {
                    parent.getChildren().add(kid);
                }
            }
            if (!treeItems.contains(parent)) {
                treeItems.add(parent);
            }
        }

        rootView.getChildren().setAll(treeItems);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(rootView.getChildren().size() + " éléments");
                entree.setText("Entree : " + entreeTotal());
                sortie.setText("Sortie : " + sortieTotal());
                double rs = entreeTotal() - sortieTotal();
                if (rs <= 0) {
                    result.setTextFill(Paint.valueOf("#ff6666"));
                } else {
                    result.setTextFill(Paint.valueOf("#444444"));
                }
                result.setText("Resultat : " + rs);
            }
        });
    }

    double entreeTotal() {
        ObservableList<TreeItem<Performance>> ls = rootView.getChildren();
        double somme = 0;
        for (TreeItem<Performance> tp : ls) {
            Performance p = tp.getValue();
            somme += p.getRecette();
        }
        return somme;
    }

    double sortieTotal() {
        ObservableList<TreeItem<Performance>> ls = rootView.getChildren();
        double somme = 0;
        double sum = 0;
        for (TreeItem<Performance> tp : ls) {
            Performance p = tp.getValue();
            somme += p.getDepense();
            sum += p.getReductionPerte();
        }
        somme = somme + sum;
        return somme;
    }

    double calculateRecette(List<Transporter> listransp, Vehicule v, long date1, long date2) {
        double somme = 0;
        for (Transporter t : listransp) {
            if (date1 != 0 && date2 != 0) {
                long date = t.getDateTransport().getTime();
                if (t.getIdVehicule().getPlaque().equals(v.getPlaque())
                        && (date >= date1 && date <= date2)) {
                    somme += t.getPriceToPay();
                }
            } else {
                if (t.getIdVehicule().getPlaque().equals(v.getPlaque())) {
                    somme += t.getPriceToPay();
                }
            }
        }
        return somme;
    }

    double calculateRedPerte(List<Transporter> listransp, Vehicule v, long date1, long date2) {
        double somme = 0;
        for (Transporter t : listransp) {
            String obs = t.getObservation();
            if (date1 != 0 && date2 != 0) {
                long date = t.getDateTransport().getTime();
                if (t.getIdVehicule().getPlaque().equals(v.getPlaque())
                        && (date >= date1 && date <= date2)
                        && (obs.equalsIgnoreCase("Perdus") || obs.equalsIgnoreCase("Perte partielle"))) {
                    somme += t.getPriceToPay();
                }
            } else {
                if (t.getIdVehicule().getPlaque().equals(v.getPlaque())
                        && (obs.equalsIgnoreCase("Perdus") || obs.equalsIgnoreCase("Perte partielle"))) {
                    somme += t.getPriceToPay();
                }
            }
        }
        return somme;
    }

    private double sumDepenseByVehiculeBySuccursale(List<Payer> lt, String idSuc, String plaque, long date1, long date2) {
        double somme = 0;
        for (Payer t : lt) {
            Depense d = t.getDepenseId();
            Comptefin cpf = t.getCompteIdCredit();
            if (cpf == null) {
                continue;
            }
            Succursale x = cpf.getSucursaleId();
            if (d != null) {
                String libelle = t.getLibelle();
                if (date1 != 0 && date2 != 0) {
                    long date = t.getDatePayement().getTime();
                    if (libelle.toUpperCase().contains(plaque.toUpperCase())
                            && (date >= date1 && date <= date2) && x.getUid().equals(idSuc)) {
                        somme += t.getMontantPaye();
                        System.err.println("Log here OK ");
                    }
                } else {
                    System.err.println("ICI");
                    if (libelle.toUpperCase().contains(plaque.toUpperCase())
                            && x.getUid().equals(idSuc)) {
                        System.err.println("Log here OK ");
                        somme += t.getMontantPaye();
                    }
                }

            }
        }
        return somme;
    }

    double calculateDepense(List<Payer> lt, Vehicule v, long date1, long date2) {
        double somme = 0;

        for (Payer t : lt) {
            Depense d = t.getDepenseId();
            if (d != null) {

                if (date1 != 0 && date2 != 0) {
                    long date = t.getDatePayement().getTime();

                    if (t.getLibelle().toUpperCase().contains(v.getPlaque().toUpperCase())
                            && (date >= date1 && date <= date2)) {
                        somme += t.getMontantPaye();
                    }
                } else {
                    System.err.println("ICI");
                    if (t.getLibelle().toUpperCase().contains(v.getPlaque().toUpperCase())) {
                        System.err.println("Log here OK");
                        somme += t.getMontantPaye();
                    }
                }

            }
        }

        return somme;
    }

    private Node createDataPage(int pgindex) {
        try {
            int offset = pgindex * rowsDataCount;
            int limit = Math.min(offset + rowsDataCount, treeItems.size());
            rootView.getChildren().setAll(FXCollections.observableArrayList(treeItems.subList(offset, limit)));
            tbl_perfomance.setRoot(rootView);
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return tbl_perfomance;
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

    public void refresh() {
        populate(datavehicule.findAll(), datatrans.findAll(), datadepense.findAll(), datarsale.findAll(), 0, 0);
    }

    @FXML
    public void refresh(Event evt) {
        refresh();
    }

    @FXML
    public void selectRowPerPage(ActionEvent e) {

    }

    public void search(String query) {
        ObservableList<TreeItem<Performance>> result = FXCollections.observableArrayList();
        for (TreeItem<Performance> tp : treeItems) {
            Performance p = tp.getValue();
            Vehicule v = p.getVehicule();
            Succursale s = p.getSuccursale();
            if ((v.getPlaque() + " " + v.getModeleVehicule() + ""
                    + " " + s.getNomSuccursale() + " " + s.getAdresse()).toUpperCase().contains(query.toUpperCase())) {
                result.add(tp);
            }

        }
        rootView.getChildren().setAll(result);
    }

    @FXML
    public void pickDateAction(ActionEvent evt) {
        LocalDate d = dpk_debut.getValue();
        LocalDate f = dpk_fin.getValue();
        if (d != null && f != null) {
            long deb = Constants.dateInMillis(d);
            long fin = Constants.dateInMillis(f);
            populate(datavehicule.findAll(), datatrans.findAll(), datadepense.findAll(), datarsale.findAll(), deb, fin);
        }
    }

}
