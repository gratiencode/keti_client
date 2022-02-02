/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.Marchandise;
import model.Succursale;
import model.Tiers;
import model.Transporter;
import model.Vehicule;
import model.Voyage;
import org.dizitart.no2.Nitrite;
import retrofit2.Response;
import util.Constants;
import util.DataId;
import util.Datastorage;
import util.KeyValueTrio;
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
    int rowsDataCount = 20;

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
    TreeTableColumn<TransporterView, String> tricolplaque_model;
    @FXML
    TreeTableColumn<TransporterView, String> tricolnom_prenom;
    @FXML
    TreeTableColumn<TransporterView, String> tricolMarch;
    @FXML
    TreeTableColumn<TransporterView, Number> tricolquantite;
    @FXML
    TreeTableColumn<TransporterView, Number> tricolapayer;
    @FXML
    TreeTableColumn<TransporterView, Number> tricolpayee;
    @FXML
    TreeTableColumn<TransporterView, Number> tricolrestant;
    @FXML
    TreeTableColumn<TransporterView, Number> tricoltracking;
    @FXML
    TreeTableColumn<TransporterView, String> tricolpostion_suc;//depart ou arrive
    @FXML
    TreeTableColumn<TransporterView, String> tricoletat;//a retirer

    private Nitrite localDatabase;
    private Succursale currentSuccursale;

    List<TransporterView> tableContent;
    TreeItem<TransporterView> rootView;
    ObservableList<TreeItem<TransporterView>> treeItems;

    @FXML
    Label count;

    Pane main;

    @FXML
    private Button addVeh;

    private ImageView notify;

    public ChargementController() {
        instance = this;
    }

    public static ChargementController getInstance() {
        return instance;
    }

    private void configTable() {
        tricolquantite.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getTransporter().getQuantite()));
        tricolapayer.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getTransporter().getPriceToPay()));
        tricolpayee.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, Number> param) -> new SimpleDoubleProperty(param.getValue().getValue().getTransporter().getPaidPrice()));
        tricolrestant.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, Number> param) -> {
            Transporter tr = param.getValue().getValue().getTransporter();
            return new SimpleDoubleProperty((tr.getPriceToPay() - tr.getPaidPrice()));
        });
        tricoldate.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, String> param) -> new SimpleStringProperty(Constants.DateFormateur.format(param.getValue().getValue().getTransporter().getDateTransport())));
        tricolplaque_model.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, String> param) -> {
            Vehicule v = param.getValue().getValue().getTransporter().getIdVehicule();
            return new SimpleStringProperty(v.getPlaque() + " " + v.getModeleVehicule());
        });
        tricolnom_prenom.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, String> param) -> {
            Tiers t = param.getValue().getValue().getTransporter().getIdTiers();
            return new SimpleStringProperty(t.getNom() + " " + t.getPrenom());
        });
        tricolMarch.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, String> param) -> {
            Marchandise t = param.getValue().getValue().getTransporter().getIdMarchandise();
            return new SimpleStringProperty(t.getNomMarchandise() + " " + t.getDescription());
        });

        tricoltracking.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, Number> param) -> new SimpleIntegerProperty(param.getValue().getValue().getTransporter().getTracking()));
        tricolpostion_suc.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, String> param) -> {
            Voyage v = param.getValue().getValue().getVoyage();
            Succursale s = v.getSuccursalleId();
            return new SimpleStringProperty(v.getEtat() + " à " + s.getNomSuccursale() + " " + s.getAdresse());
        });
        tricolpostion_suc.setCellFactory(new Callback<TreeTableColumn<TransporterView, String>, javafx.scene.control.TreeTableCell<util.TransporterView, java.lang.String>>() {
            @Override
            public TreeTableCell<TransporterView, String> call(TreeTableColumn<TransporterView, String> param) {
                return new TextFieldTreeTableCell<>();
            }
        });
        ObservableList<String> pos = FXCollections.observableArrayList("Chargé", "Arrivée", "Départ");
        tricolpostion_suc.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(pos));
        tricolpostion_suc.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<TransporterView, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<TransporterView, String> event) {
                TreeItem<TransporterView> treeItem = tbl_tranporterViews.getTreeItem(event.getTreeTablePosition().getRow());
                TransporterView tvu = treeItem.getValue();
                Voyage v = tvu.getVoyage();
                Voyage vg = new Voyage(DataId.generate());
                vg.setEtat(event.getNewValue());
                vg.setDateArriver(new Date());
                vg.setSuccursalleId(currentSuccursale);
                vg.setVehiculeId(v.getVehiculeId());
                voyagedb.insert(vg);
            }
        });
        tricoletat.setCellValueFactory((TreeTableColumn.CellDataFeatures<TransporterView, String> param) -> new SimpleStringProperty(param.getValue().getValue().getTransporter().getObservation()));
        tricoletat.setCellFactory(new Callback<TreeTableColumn<TransporterView, String>, TreeTableCell<TransporterView, String>>() {
            @Override
            public TreeTableCell<TransporterView, String> call(TreeTableColumn<TransporterView, String> param) {
                return new TextFieldTreeTableCell<>();
            }
        });
        ObservableList<String> ols = FXCollections.observableArrayList("A rétirer", "Rétiré", "Retrait partiel","Perdus","Perte partielle");
        tricoletat.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(ols));
        tricoletat.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<TransporterView, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<TransporterView, String> event) {
                TreeItem<TransporterView> transvu = tbl_tranporterViews.getTreeItem(event.getTreeTablePosition().getRow());
                Transporter t = transvu.getValue().getTransporter();
                String obs=event.getNewValue();
                if(obs.equalsIgnoreCase("Perdus")){
                    
                }else if(obs.equalsIgnoreCase("Perte partielle")){
                    
                }
                t.setObservation(obs);
                transdb.update("uid", t.getUid(), t);
//                try {
//                    Response<Transporter> execute = keti.updateTransporter(t.getUid(), t).execute();
//                    if(execute.isSuccessful()){
//                        System.out.println("Transport modifier avec succes");
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(ChargementController.class.getName()).log(Level.SEVERE, null, ex);
//                }
                System.out.println("Action de retirer ou pas ........");
            }
        });
        tbl_tranporterViews.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbl_tranporterViews.setEditable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pref = Preferences.userNodeForPackage(this.getClass());
        treeItems = FXCollections.observableArrayList();
        configTable();
        rootView = new TreeItem<>(new TransporterView(new Transporter(DataId.generate(), 0, 0, 0, new Date(), 0), new Voyage(DataId.generate(), new Date(), "root")));
        tbl_tranporterViews.setRoot(rootView);
        tbl_tranporterViews.setShowRoot(false);
        tableContent = new ArrayList<>();
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowPP.setItems(rows);
        rowPP.getSelectionModel().select(0);
        pagination.setPageFactory(this::createDataPage);
        notify = new ImageView(new Image(this.getClass().getResourceAsStream("/icons/cocher.png")));
        setPattern(dpk_fin);
        setPattern(dpk_debut);
    }

    @FXML
    private void selectRowPerPage(ActionEvent evt) {
        ComboBox cbx = (ComboBox) evt.getSource();
        rowsDataCount = (int) cbx.getSelectionModel().getSelectedItem();
        pagination.setPageFactory(this::createDataPage);
        refresh(null);
        System.out.println("Row set to " + rowsDataCount);
    }
   

    private Node createDataPage(int pgindex) {
        try {
            int offset = pgindex * rowsDataCount;
            int limit = Math.min(offset + rowsDataCount, treeItems.size());
            rootView.getChildren().setAll(FXCollections.observableArrayList(treeItems.subList(offset, limit)));
            tbl_tranporterViews.setRoot(rootView);
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return tbl_tranporterViews;
    }

    private KeyValueTrio<Double, List<TransporterView>, Double> findByClient(List<TransporterView> tierset, String idtier) {
        KeyValueTrio<Double, List<TransporterView>, Double> kvp = new KeyValueTrio<>();
        double sommeDue = 0;
        double sommePaid = 0;
        List<TransporterView> result = new ArrayList<>();
        for (TransporterView tr : tierset) {
            Tiers t = tr.getTransporter().getIdTiers();
            if (t.getUid().equalsIgnoreCase(idtier)) {
                if (!result.contains(tr)) {
                    sommeDue += tr.getTransporter().getPriceToPay();
                    sommePaid += tr.getTransporter().getPaidPrice();
                    result.add(tr);

                }
            }
        }
        kvp.setKey(sommeDue);
        kvp.setParam(sommePaid);
        kvp.setValue(result);
        return kvp;
    }

    public void refresh(List<TransporterView> ltv) {
        if (ltv == null) {
            List<Transporter> trs = transdb.findAll();
            List<Voyage> lvg = voyagedb.findAll();
            tableContent.clear();
            trs.forEach(t -> {
                TransporterView tv = new TransporterView();
                tv.setMarchandise(t.getIdMarchandise());
                tv.setTransporter(t);
                Voyage vg = findVoyageByVehicule(lvg, t.getIdVehicule());
                tv.setVoyage(vg);
                if (!tableContent.contains(tv)) {
                    tableContent.add(tv);
                }
            });
        } else {
            tableContent.clear();
            ltv.forEach(tv -> {
                if (!tableContent.contains(tv)) {
                    tableContent.add(tv);
                }
            });
        }
        treeItems.clear();
        double keti_sum = 0;
        List<Tiers> ltr = pickTiers();
        for (Tiers t : ltr) {
            KeyValueTrio<Double, List<TransporterView>, Double> data = findByClient(tableContent, t.getUid());
            List<TransporterView> m4clt = data.getValue();
            double sum2Pay = data.getKey();
            double sumPaid = data.getParam();
            TransporterView tv = null;
            if (m4clt.size() == 1) {
                tv = m4clt.get(0);
            } else if (m4clt.size() > 1) {
                tv = fromArg((m4clt.size() - 1), m4clt.get(0), sum2Pay, sumPaid);
            }

            TreeItem<TransporterView> titv = new TreeItem<>(tv);
            m4clt.forEach(tx -> {
                TreeItem<TransporterView> ti = new TreeItem<>(tx);
                titv.getChildren().add(ti);
            });
            if (!treeItems.contains(titv)) {
                treeItems.add(titv);
            }
            keti_sum += sum2Pay;
        }
        rootView.getChildren().setAll(treeItems);
        count.setText(pickTiers().size() + " Chargements");
        total_global.setText("Total global à payer : $" + keti_sum);
        System.err.println("Ex - " + pickTiers().size());
    }

    private List<Tiers> pickTiers() {
        List<Tiers> result = new ArrayList<>();
        tableContent.forEach(tr -> {
            Tiers t = tr.getTransporter().getIdTiers();
            if (!result.contains(t)) {
                result.add(t);
            }
        });
        return result;
    }

    private TransporterView fromArg(long cont,TransporterView tv, double sum2pay, double sumPaid) {
        TransporterView tvx = new TransporterView();//
        Marchandise mz = new Marchandise(tv.getMarchandise().getUid(), tv.getMarchandise().getNomMarchandise() + " et " + cont + " autres bagages");
        mz.setDescription(tv.getMarchandise().getDescription());
        tvx.setMarchandise(mz);
        Transporter t = new Transporter(DataId.generate());
        t.setIdMarchandise(mz);
        t.setDateTransport(new Date());
        t.setIdTiers(tv.getTransporter().getIdTiers());
        t.setIdVehicule(tv.getTransporter().getIdVehicule());
        t.setObservation(tv.getTransporter().getObservation());
        t.setPaidPrice(sumPaid);
        t.setPriceToPay(sum2pay);
        t.setQuantite(0);
        t.setTracking(tv.getTransporter().getTracking());
        tvx.setTransporter(t);
        Voyage v = new Voyage();
        v.setDateArriver(tv.getVoyage().getDateArriver());
        v.setEtat(Constants.VEHICLE_LOADED);
        v.setVehiculeId(tv.getVoyage().getVehiculeId());
        v.setSuccursalleId(tv.getVoyage().getSuccursalleId());
        tvx.setVoyage(v);
        return tvx;

    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

    public void viewTransUI(ActionEvent evt) {
        MainUI.floatDialog("transporter.fxml", 770, 730, token, localDatabase, currentSuccursale);
    }

    public void saveTransaction(List<TransporterView> translist) {
        System.out.println("Insertrans size " + translist.size());
        translist.forEach((TransporterView tr) -> {
            TransporterView t = new TransporterView();
            Transporter insertrans = transdb.insert(tr.getTransporter());
            System.out.println("trans " + insertrans.getUid());
            Voyage insertvoyage = voyagedb.insert(tr.getVoyage());
            t.setTransporter(insertrans);
            t.setVoyage(insertvoyage);
            t.setMarchandise(insertrans.getIdMarchandise());
            tableContent.add(t);
        });

        refresh(null);
    }

    @FXML
    public void refreshTransporter(MouseEvent evt) {
        refresh(null);
    }

    public void deleteTransporter(MouseEvent evt) {
        ObservableList<TreeItem<TransporterView>> selectedItems = tbl_tranporterViews.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "La selection est vide");
            alert.setTitle("Selectionez un element!");
            alert.setHeaderText(null);
            alert.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez vous vraiment supprimer le " + selectedItems.size() + " elements selectionnés ?", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Attention!");
        alert.setHeaderText(null);
        Optional<ButtonType> showAndWait = alert.showAndWait();
        if (showAndWait.get() == ButtonType.YES) {
            for (TreeItem<TransporterView> trv : selectedItems) {
                if (trv == null) {
                    break;
                }
                if (trv.isLeaf()) {
                    TransporterView tv = trv.getValue();
                    transdb.delete("uid", tv.getTransporter().getUid());
                    trv.getParent().getChildren().removeAll(trv);
                    try {
                        Response<Void> execute = keti.deleteLoads(tv.getTransporter().getUid()).execute();
                        if (execute.isSuccessful()) {
                            System.out.println("Trans view deleted succesfully");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ChargementController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
            MainUI.notify(null, "Succès", "Bagages suprimés avec succès", 4, "info");
        } else {
            alert.hide();
        }

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

        refresh(null);
    }

    Voyage findVoyageByVehicule(List<Voyage> lv, Vehicule vx) {
        List<Voyage> lrst = new ArrayList<>();
        for (Voyage v : lv) {
            if (v.getVehiculeId().getUid().equals(vx.getUid())) {
                lrst.add(v);
            }
        }
        return lrst.isEmpty() ? null : lrst.get(lrst.size() - 1);
    }

    @FXML
    public void pickDateAction(ActionEvent evt) {
        findByDate();
    }

    private void findByDate() {
        List<TransporterView> result = new ArrayList<>();
        LocalDate d = dpk_debut.getValue();
        LocalDate f = dpk_fin.getValue();
        if (d != null && f != null) {
            long deb = Constants.dateInMillis(d);
            long fin = Constants.dateInMillis(f);
            System.out.println(" de " + deb + " fi " + fin);
            for (TransporterView tv : tableContent) {
                long value = tv.getTransporter().getDateTransport().getTime();
                System.out.println("cd " + value);
                if (value >= deb && value <= fin) {
                    result.add(tv);
                }
            }
            System.out.println("result " + result.size());
            refresh(result);
        }

    }

    public void search(String query) {
        refresh(null);
        List<TransporterView> result = new ArrayList<>();
        for (TransporterView tv : tableContent) {
            Transporter t = tv.getTransporter();
            Voyage v = tv.getVoyage();
            Marchandise m = tv.getMarchandise();
            if ((t.getObservation() + " " + Constants.DateFormateur.format(t.getDateTransport()) + ""
                    + " " + t.getIdVehicule().getPlaque() + " " + t.getIdTiers().getNom() + " " + t.getIdTiers().getPrenom()
                    + " " + t.getIdTiers().getAdresse() + " " + t.getIdTiers().getTypetiers()
                    + "" + t.getIdVehicule().getMarque() + " " + t.getPriceToPay() + " " + t.getTracking() + ""
                    + " " + v.getEtat() + " " + v.getSuccursalleId().getNomSuccursale() + " "
                    + "" + m.getDescription() + " " + m.getNomMarchandise()).toUpperCase().contains(query.toUpperCase())) {
                result.add(tv);
            }

        }
        if (result.isEmpty()) {
            return;
        }
        refresh(result);
    }

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
