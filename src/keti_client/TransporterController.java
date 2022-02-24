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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Marchandise;
import model.Succursale;
import model.Tiers;
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import util.ComboBoxAutoCompletion;
import util.Constants;
import util.Datastorage;
import util.TransporterView;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class TransporterController implements Initializable {

    Datastorage<Tiers> cltdb;
    Datastorage<Vehicule> vehidb;
    Datastorage<Marchandise> marchdb;
    private String token;
    private Nitrite localDatabase;
    KetiAPI keti;
    Integer track;
    @FXML
    ComboBox<Vehicule> vehicules_names;
    ObservableList<Vehicule> vehiculist;
    ObservableList<Marchandise> goods_array;
    ObservableList<TransporterView> translist;

    private Succursale currentSuccursale;
    Tiers choosenTier;
    Vehicule choosenVehicule;

    private static TransporterController instance;
    @FXML
    private ComboBox<Tiers> clients_names;
    @FXML
    private ListView<Marchandise> list_marchandise;
    @FXML
    private TableView<TransporterView> tbl_bfsave;
    @FXML
    TableColumn<TransporterView, String> colGoodsname;
    @FXML
    TableColumn<TransporterView, String> colQuantite;
    @FXML
    TableColumn<TransporterView, String> coltotalApayer;
    @FXML
    TextField search_goods;
    @FXML
    Label tracking, total;
    @FXML
    private Button addVeh;
    private ObservableList<Tiers> clt_list;
    private double totalApayer = 0;

    public TransporterController() {
        TransporterController.instance = this;
    }

    public static TransporterController getInstance() {
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(this.token);

    }

    private void configCombos() {
        clients_names.setConverter(new StringConverter<Tiers>() {
            @Override
            public String toString(Tiers object) {
                return object == null ? null : object.toString();
            }

            @Override
            public Tiers fromString(String string) {
                return clients_names.getItems()
                        .stream()
                        .filter(clt -> (clt.getPrenom() + " " + clt.getNom() + " " + clt.getAdresse())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(choosenTier);
            }
        });
        vehicules_names.setConverter(new StringConverter<Vehicule>() {
            @Override
            public String toString(Vehicule object) {
                return object == null ? null : object.toString();
            }

            @Override
            public Vehicule fromString(String string) {
                return vehicules_names.getItems()
                        .stream()
                        .filter(v -> (v.getPlaque() + " " + v.getMarque() + " " + v.getModeleVehicule())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });
        clients_names.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tiers> observable, Tiers oldValue, Tiers newValue) -> {
            choosenTier = newValue;
        });
        vehicules_names.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Vehicule> observable, Vehicule oldValue, Vehicule newValue) -> {
            choosenVehicule = newValue;
        });
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
        vehidb = new Datastorage<>(this.localDatabase, Vehicule.class);
        cltdb = new Datastorage<>(this.localDatabase, Tiers.class);
        marchdb = new Datastorage<>(this.localDatabase, Marchandise.class);
        vehiculist = FXCollections.observableArrayList();
        vehiculist.setAll(vehidb.findAll());
        vehicules_names.setItems(vehiculist);
        clt_list = FXCollections.observableList(cltdb.findAll());
        clients_names.itemsProperty().setValue(clt_list);
        goods_array = FXCollections.observableArrayList();
        goods_array.setAll(marchdb.findAll());
        list_marchandise.setItems(goods_array);
        new ComboBoxAutoCompletion<>(vehicules_names);
        new ComboBoxAutoCompletion<>(clients_names);
        configCombos();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list_marchandise.setCellFactory((ListView<Marchandise> param) -> new ListCell<Marchandise>() {
            @Override
            protected void updateItem(Marchandise item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNomMarchandise() + " " + item.getDescription());
                }
            }

        });

        track = ((int) (Math.random() * 1600000));
        tracking.setText("Tracking : " + track);
        colGoodsname.setCellValueFactory((TableColumn.CellDataFeatures<TransporterView, String> param) -> {
            Marchandise m = param.getValue().getMarchandise();
            return new SimpleStringProperty(m.getNomMarchandise() + " " + m.getDescription());
        });
        colQuantite.setCellValueFactory((TableColumn.CellDataFeatures<TransporterView, String> param) -> new SimpleStringProperty(String.valueOf(param.getValue().getTransporter().getQuantite())));
        coltotalApayer.setCellValueFactory((TableColumn.CellDataFeatures<TransporterView, String> param) -> new SimpleStringProperty(String.valueOf(param.getValue().getTransporter().getPriceToPay())));
        tbl_bfsave.getColumns().clear();
        tbl_bfsave.getColumns().addAll(colGoodsname, colQuantite, coltotalApayer);
        tbl_bfsave.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        translist = FXCollections.observableArrayList();
        tbl_bfsave.setItems(translist);
    }

    public void addGoodsItem(Marchandise m) {
        goods_array.add(m);
    }

    public void addTransViewItem(TransporterView transview) {
        translist.add(transview);
    }

    public void searchGoods() {
        search_goods.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {

            if (!search_goods.getText().isEmpty()) {
                ObservableList<Marchandise> rsult = FXCollections.observableArrayList();
                goods_array.forEach((m) -> {
                    if ((m.getDescription() + " " + m.getNomMarchandise() + " " + m.getPrix()).toUpperCase().contains(search_goods.getText().toUpperCase())) {
                        rsult.add(m);
                    }
                });
                list_marchandise.setItems(rsult);
            } else {
                list_marchandise.setItems(goods_array);
            }
        });
    }

    public void addChoosenGoods(TransporterView transview) {
        translist.add(transview);
    }

    @FXML
    public void removeMarchandise(ActionEvent event) {
        List<TransporterView> lst = tbl_bfsave.getSelectionModel().getSelectedItems();
        double d = 0;
        for (TransporterView t : lst) {
            d += t.getTransporter().getPriceToPay();
        }
        this.totalApayer -= d;
        total.setText("Total à payer : $" + this.totalApayer);
        translist.removeAll(lst);
    }

    
    
    @FXML
    public void chooseMarchandise(ActionEvent event) {
        Marchandise m = list_marchandise.getSelectionModel().getSelectedItem();
        if (m == null) {
            return;
        }
        MainUI.floatDialog(Constants.ADDTOCART_DLG, 533, 297, token,
                localDatabase, choosenTier, m,
                choosenVehicule, track, currentSuccursale);
    }

    @FXML
    private void addMarchandise(ActionEvent event) {
        MainUI.floatDialog(Constants.MARCHANDISE_DLG, 486, 400, token, localDatabase);
    }

    @FXML
    private void saveTransaction(ActionEvent event) {
        ChargementController.getInstance().saveTransaction(translist);
        close(event);
    }

    @FXML
    private void switchToClients(ActionEvent event) {
        MainController.getInstance().gotoClients();
        close(event);
    }

    @FXML
    private void switchToVehicule(ActionEvent event) {
        MainController.getInstance().gotoVehicule();
        close(event);
    }

    @FXML
    private void close(Event evt) {
        Node n = (Node) evt.getSource();
        Stage st = (Stage) n.getScene().getWindow();
        st.close();
    }

    @FXML
    public void onHoverHome(MouseEvent event) {
        ImageView img = (ImageView) event.getSource();
        MainUI.setShadowEffect(img);
    }

    @FXML
    public void onOutHome(MouseEvent event) {
        ImageView img = (ImageView) event.getSource();
        MainUI.removeShaddowEffect(img);
    }

    @FXML
    public void search(KeyEvent evt) {
        searchGoods();
    }

    public void setCurrentSuccursale(Succursale currentSuccursale) {
        this.currentSuccursale = currentSuccursale;
    }

    public void setTotalApayer(double totalApayer) {
        this.totalApayer += totalApayer;
        total.setText("Total à payer : $" + this.totalApayer);
    }

}
