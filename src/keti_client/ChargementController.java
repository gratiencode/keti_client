/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.Marchandise;
import model.Succursale;
import model.Transporter;
import model.Voyage;
import org.dizitart.no2.Nitrite;
import util.Datastorage;
import util.ScreensChangeListener;
import util.Token;
import util.TransporterView;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class ChargementController implements Initializable, ScreensChangeListener {

    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;
    private static ChargementController instance;

    Datastorage<Transporter> transdb;
    Datastorage<Voyage> voyagedb;
    Datastorage<Marchandise> msedb;

    @FXML
    ComboBox<Integer> rowPP;
    @FXML
    Pagination pagination;
    @FXML
    Label total_global;
    @FXML
    DatePicker dpk_debut;
    @FXML
    DatePicker dpk_fin;

    @FXML
    TreeTableView<TransporterView> tbl_tranporterViews;
    @FXML
    TreeTableColumn<TransporterView, String> tricoldate;
    @FXML
    TreeTableColumn<TransporterView, String> tricolplaque;
    @FXML
    TreeTableColumn<TransporterView, String> tricolmodel;
    @FXML
    TreeTableColumn<TransporterView, String> tricolnom;
    @FXML
    TreeTableColumn<TransporterView, String> tricolprenom;
    @FXML
    TreeTableColumn<TransporterView, String> tricolmarch;
    @FXML
    TreeTableColumn<TransporterView, String> tricolquant;
    @FXML
    TreeTableColumn<TransporterView, String> tricoldette;
    @FXML
    TreeTableColumn<TransporterView, String> tricolpaye;
    @FXML
    TreeTableColumn<TransporterView, String> tricolrestant;
    @FXML
    TreeTableColumn<TransporterView, String> tricoltracking;
    @FXML
    TreeTableColumn<TransporterView, String> tricoletat;

    private Nitrite localDatabase;
    private Succursale currentSuccursale;

    ObservableList<TransporterView> tableContent;

    Label count;

    Pane main;

    @FXML
    private Button addVeh;

    public ChargementController() {
        instance=this;
    }
    public static ChargementController getInstance(){
        return instance;
    }
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pref = Preferences.userNodeForPackage(this.getClass());
        tableContent = FXCollections.observableArrayList();

    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

    public void viewTransUI(ActionEvent evt) {
        MainUI.floatDialog("transporter.fxml", 770, 730, token, localDatabase, currentSuccursale);
    }

    public void saveTransaction(List<TransporterView> translist) {
        System.out.println("Insertrans size "+translist.size());
        translist.forEach((TransporterView tr) -> {
            TransporterView t=new TransporterView();
            Transporter insertrans = transdb.insert(tr.getTransporter());
            System.out.println("trans "+insertrans.getUid());
            Voyage insertvoyage = voyagedb.insert(tr.getVoyage()); 
        });
    }

    public void updateTransporter(ActionEvent evt) {

    }

    public void deleteTransporter(ActionEvent evt) {

    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(this.token);
        KetiHelper.setOnTokenRefreshCallback((Token var1) -> {
            this.token = var1.getToken();
            pref.put("KetiToken", token);
        });
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
        transdb = new Datastorage<>(this.localDatabase, Transporter.class);
        voyagedb = new Datastorage<>(this.localDatabase, Voyage.class);
        msedb = new Datastorage<>(this.localDatabase, Marchandise.class);
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

    public void setCurrentSuccursale(Succursale currentSuccursale) {
        this.currentSuccursale = currentSuccursale;
    }

}
