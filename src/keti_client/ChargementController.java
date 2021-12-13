/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.Marchandise;
import model.Tiers;
import model.Transporter;
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import util.Datastorage;
import util.ScreensChangeListener;
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

    Datastorage<Transporter> transdb;
    Datastorage<Tiers> cltdb;
    Datastorage<Vehicule> vehidb;
    Datastorage<Marchandise> marchdb;

    @FXML
    TreeTableView<Transporter> tranporters;
    @FXML
    ComboBox<Integer> rowPP;
    @FXML
    ComboBox<String> vehicules_names;
    @FXML
    ComboBox<String> clients_names;
    @FXML
    ComboBox<String> goods_names;
    @FXML
    Pagination pagination;
    @FXML
    Label total_global;
    @FXML
    Label tracking;
    @FXML
    Label sous_total;
    @FXML
    DatePicker dpk_debut;
    @FXML
    DatePicker dpk_fin;
    @FXML
    TextField search_goods;
    @FXML
    ListView<Marchandise> list_marchandise;
    @FXML
    TableView<Transporter> transpanier;
    @FXML
    TableColumn<Transporter, String> colgoods_name;
    @FXML
    TableColumn<Transporter, String> colgoods_quant;
    @FXML
    TableColumn<Transporter, String> colgoods_prices;
    @FXML
    TreeTableColumn<Transporter, String> tricoldate;
    @FXML
    TreeTableColumn<Transporter, String> tricolplaque;
    @FXML
    TreeTableColumn<Transporter, String> tricolmodel;
    @FXML
    TreeTableColumn<Transporter, String> tricolnom;
    @FXML
    TreeTableColumn<Transporter, String> tricolprenom;
    @FXML
    TreeTableColumn<Transporter, String> tricolmarch;
    @FXML
    TreeTableColumn<Transporter, String> tricolquant;
    @FXML
    TreeTableColumn<Transporter, String> tricoldette;
    @FXML
    TreeTableColumn<Transporter, String> tricolpaye;
    @FXML
    TreeTableColumn<Transporter, String> tricolrestant;
    @FXML
    TreeTableColumn<Transporter, String> tricoltracking;

    private Nitrite localDatabase;

    @FXML
    TableView<Transporter> afterSave;
    Dialog dlg;
    @FXML
    Label count;

    Pane main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pref = Preferences.userNodeForPackage(this.getClass());
        dlg = new Dialog();

    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

    @FXML
    public void switchToClients(ActionEvent evt) {
        MainController.getInstance().gotoClients();
    }

    @FXML
    public void switchToVehicule(ActionEvent evt) {
        MainController.getInstance().gotoVehicule();
        dlg.close();
    }

    @FXML
    public void viewTransUI(ActionEvent evt) {
        DialogPane dp = MainUI.element("transporter.fxml");
        dlg.setDialogPane(dp);
        dlg.showAndWait().ifPresent(new Consumer() {
            @Override
            public void accept(Object t) {
                ButtonType bt = (ButtonType) t;
                if (bt.equals(ButtonType.OK)) {
                    System.out.println("BTn OK");
                } else {
                    System.err.println("Btn cancel");
                }

            }
        });
    }

    @FXML
    public void addMarchandise(ActionEvent evt) {

    }

    @FXML
    public void chooseMarchandise(ActionEvent evt) {

    }

    @FXML
    public void removeMarchandise(ActionEvent evt) {

    }

    @FXML
    public void saveTransport(ActionEvent evt) {

    }

    @FXML
    public void cancel(ActionEvent evt) {

    }

    @FXML
    public void searchGoods(ActionEvent evt) {

    }

    @FXML
    public void refresh(ActionEvent evt) {

    }

    @FXML
    public void updateTransporter(ActionEvent evt) {

    }

    @FXML
    public void deleteTransporter(ActionEvent evt) {

    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(this.token);
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
        transdb = new Datastorage<>(this.localDatabase, Transporter.class);
        vehidb=new Datastorage<>(this.localDatabase,Vehicule.class);
        cltdb=new Datastorage<>(this.localDatabase,Tiers.class);
        marchdb=new Datastorage<>(this.localDatabase,Marchandise.class);
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

}
