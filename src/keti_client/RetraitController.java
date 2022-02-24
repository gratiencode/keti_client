/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import model.Retirer;
import model.Tiers;
import model.Transporter;
import org.dizitart.no2.Nitrite;
import retrofit2.Call;
import retrofit2.Response;
import util.ComboBoxAutoCompletion;
import util.Constants;
import util.DataId;
import util.Datastorage;
import util.RetraitView;
import util.ScreensChangeListener;
import util.TransporterView;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class RetraitController implements Initializable, ScreensChangeListener {

    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;
    private Nitrite localDatabase;
    int rowDataCount = 20;
    Datastorage<Tiers> tierstore;
    Datastorage<Transporter> transtore;
    Datastorage<Retirer> retraitstore;
    @FXML
    Pagination pagination;
    @FXML
    ComboBox<Integer> rowPP;
    @FXML
    ComboBox<Tiers> clients_names_cbx;
    @FXML
    ComboBox<Transporter> transporters_names_cbx;
    @FXML
    ListView<TransporterView> transviewlist;
    @FXML
    TextField quantRetr;
    @FXML
    Label valueTracked;
    @FXML
    Label valueToRet;
    @FXML
    Label count;
    @FXML
    DatePicker dpkRetr;
    @FXML
    TableView<RetraitView> tbl_retrait;
    @FXML
    TableColumn<RetraitView, String> col_dateret;
    @FXML
    TableColumn<RetraitView, Number> col_trackret;
    @FXML
    TableColumn<RetraitView, String> col_marchandret;
    @FXML
    TableColumn<RetraitView, String> col_client;
    @FXML
    TableColumn<RetraitView, Number> col_quantTransret;
    @FXML
    TableColumn<RetraitView, Number> col_valTransret;
    @FXML
    TableColumn<RetraitView, Number> col_quantRetr;
    @FXML
    TableColumn<RetraitView, Number> col_valRetRet;
    @FXML
    TableColumn<RetraitView, Number> col_quantRestRet;
    @FXML
    TableColumn<RetraitView, Number> col_valRestRet;
    ObservableList<RetraitView> retraitsvu;
    ObservableList<TransporterView> transportview;
    ObservableList<Tiers> clients_cbx;
    ObservableList<Transporter> tranp_cbx;
    Tiers choosenClient;
    Transporter choosenTracking;

    private static RetraitController instance;

    public RetraitController() {
        instance = this;
    }

    public static RetraitController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cnfRowPPComboBox();
        retraitsvu = FXCollections.observableArrayList();
        clients_cbx = FXCollections.observableArrayList();
        tranp_cbx = FXCollections.observableArrayList();
        transportview = FXCollections.observableArrayList();
        configTab();
        configCombos();
        new ComboBoxAutoCompletion<>(clients_names_cbx);
        new ComboBoxAutoCompletion<>(transporters_names_cbx);
        dpkRetr.setValue(LocalDate.now());
        setPattern(dpkRetr);
        transviewlist.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(token);

    }

    private void configTab() {
        col_dateret.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, String> param) -> new SimpleStringProperty(Constants.DateFormateur.format(param.getValue().getDateRetrait())));
        col_marchandret.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, String> param) -> new SimpleStringProperty(param.getValue().getMarchandise()));
        col_client.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, String> param) -> new SimpleStringProperty(param.getValue().getClient()));
        col_trackret.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, Number> param) -> new SimpleIntegerProperty(param.getValue().getTracking()));
        col_quantTransret.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, Number> param) -> new SimpleDoubleProperty(param.getValue().getQuantiteLoaded()));
        col_valTransret.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, Number> param) -> new SimpleDoubleProperty(param.getValue().getValeurLoaded()));
        col_quantRetr.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, Number> param) -> new SimpleDoubleProperty(param.getValue().getQuantiteRetire()));
        col_quantRestRet.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, Number> param) -> new SimpleDoubleProperty(param.getValue().getQuantiteRestant()));
        col_valRestRet.setCellValueFactory((TableColumn.CellDataFeatures<RetraitView, Number> param) -> new SimpleDoubleProperty(param.getValue().getValeurRestant()));
        transviewlist.setCellFactory((ListView<TransporterView> parm) -> new ListCell<TransporterView>() {
            @Override
            protected void updateItem(TransporterView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(" " + item.getTransporter().getQuantite() + " "
                            + "" + item.getMarchandise().getNomMarchandise() + " " + item.getMarchandise().getDescription() + ""
                            + " pour " + item.getTransporter().getPriceToPay() + "$");
                }
            }
        });

    }

    private Node createDataPage(int pgindex) {
        try {
            int offset = pgindex * rowDataCount;
            int limit = Math.min(offset + rowDataCount, retraitsvu.size());
            tbl_retrait.setItems(FXCollections.observableArrayList(retraitsvu.subList(offset, limit)));
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return tbl_retrait;
    }

    private void cnfRowPPComboBox() {
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowPP.setItems(rows);
        rowPP.getSelectionModel().select(0);
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
        retraitstore = new Datastorage<>(this.localDatabase, Retirer.class);
        tierstore = new Datastorage<>(this.localDatabase, Tiers.class);
        transtore = new Datastorage<>(this.localDatabase, Transporter.class);

        clients_cbx.addAll(tierstore.findAll());
        tranp_cbx.addAll(transtore.findAll());
        populate();
        pagination.setPageFactory(this::createDataPage);
        clients_names_cbx.setItems(clients_cbx);
        transporters_names_cbx.setItems(tranp_cbx);
    }

    @FXML
    public void refresh(Event evt) {
        populate();
    }

    public RetraitView toRetraitView(Retirer r) {
        Tiers t = r.getTiersId();
        RetraitView rv = new RetraitView();
        rv.setClient(t.getPrenom() + " " + t.getNom());
        rv.setDateRetrait(r.getDateRet());
        rv.setMarchandise(r.getTransId().getIdMarchandise().getNomMarchandise() + ""
                + " " + r.getTransId().getIdMarchandise().getDescription());
        rv.setQuantiteLoaded(r.getTransId().getQuantite());
        rv.setQuantiteRetire(r.getQuantiteRet());
        rv.setQuantiteRestant(r.getQuantiteRest());
        rv.setTracking(r.getReference());
        rv.setUid(r.getUid());
        rv.setValeurLoaded(r.getTransId().getPriceToPay());
        rv.setValeurRestant(r.getValeurRest());
        rv.setValeurRetire(r.getValeurRet());
        return rv;
    }

    public void populate() {
        List<Retirer> retrs = retraitstore.findAll();
        retraitsvu.clear();
        for (Retirer r : retrs) {
            retraitsvu.add(toRetraitView(r));
        }
        Collections.reverse(retraitsvu);
        tbl_retrait.setItems(retraitsvu);

    }

    @FXML
    public void save(ActionEvent evt) {
        if (quantRetr.getText().isEmpty() || choosenClient == null || choosenTracking == null) {
            return;
        }

        double oldq = calcRetrait(choosenTracking.getUid());
        double qload = choosenTracking.getQuantite();
        double payload = choosenTracking.getPriceToPay();
        double qret = Double.parseDouble(quantRetr.getText());
        double rate = qret / qload;
        double qpretv = payload * rate;
        double valrest = payload - qpretv;
        double qrest = qload - (qret+oldq);
        if(qret>qrest){
            MainUI.notify(null, "Erreur", "Veuillez entrer une quanite valide", 4, "error");
            return;
        }
        Retirer r = new Retirer(DataId.generate());
        r.setDateRet(Constants.toUtilDate(dpkRetr.getValue()));
        r.setTiersId(choosenClient);
        r.setTransId(choosenTracking);
        r.setValeurRest(valrest<=0?0:valrest);
        r.setValeurRet(qpretv);
        r.setQuantiteRest(qrest<=0?0:qrest);
        r.setQuantiteRet(qret);
        r.setReference(choosenTracking.getTracking());
        Retirer retsaved = retraitstore.insert(r);
        if (retsaved != null) {
            choosenTracking.setObservation((qrest <= 0) ? "Rétiré" : (qrest == qload) ? "A rétirer" : "Retrait partiel");
            transtore.update("uid", choosenTracking.getUid(), choosenTracking);
            retraitsvu.add(toRetraitView(r));
            MainUI.notify(null, "Succès", "Retrait enregistré avec succès", 4, "Info");
        }
    }

    private double calcRetrait(String uid) {
        List<Retirer> oldret = findRetraits(retraitstore.findAll(), uid);
        double s = 0;

        for (Retirer r : oldret) {
            s += r.getQuantiteRet();
        }
        return s;
    }

    List<Retirer> findRetraits(List<Retirer> lr, String truid) {
        List<Retirer> result = new ArrayList<>();
        for (Retirer r : lr) {
            if (r.getTransId().getUid().equals(truid)) {
                result.add(r);
            }
        }
        return result;
    }

    private double cs(List<Transporter> cls) {
        double somme = 0;
        for (Transporter t : cls) {
            somme += t.getPriceToPay();
        }
        return somme;
    }

    private void configCombos() {
        transporters_names_cbx.setConverter(new StringConverter<Transporter>() {
            @Override
            public String toString(Transporter object) {
                return object == null ? null : "#" + object.getTracking();
            }

            @Override
            public Transporter fromString(String string) {
                return transporters_names_cbx.getItems()
                        .stream()
                        .filter(t -> (String.valueOf(t.getTracking()))
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(choosenTracking);

            }
        });
        clients_names_cbx.setConverter(new StringConverter<Tiers>() {
            @Override
            public String toString(Tiers object) {
                return object == null ? null : object.toString();
            }

            @Override
            public Tiers fromString(String string) {
                return clients_names_cbx.getItems()
                        .stream()
                        .filter(clt -> (clt.getPrenom() + " " + clt.getNom() + " " + clt.getAdresse())
                        .equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(choosenClient);
            }
        });
        clients_names_cbx.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tiers> observable, Tiers oldValue, Tiers newValue) -> {
            choosenClient = newValue;
        });

        transporters_names_cbx.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Transporter>() {
            @Override
            public void changed(ObservableValue<? extends Transporter> observable, Transporter oldValue, Transporter newValue) {
                choosenTracking = newValue;
                if (choosenTracking == null) {
                    return;
                }
                clients_names_cbx.getSelectionModel().select(choosenTracking.getIdTiers());
                int track = choosenTracking.getTracking();
                List<Transporter> tracks = transtore.findTracking(track);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        valueTracked.setText(tracks.size() + " Colis transportés à : $" + cs(tracks));
                    }
                });
                transviewlist.getItems().clear();
                for (Transporter t : tracks) {
                    TransporterView tv = new TransporterView();
                    tv.setMarchandise(t.getIdMarchandise());
                    tv.setTransporter(t);
                    transportview.add(tv);
                }
                transviewlist.setItems(transportview);
            }
        });
        transviewlist.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends TransporterView> observable, TransporterView oldValue, TransporterView newValue) -> {
            if (newValue == null) {
                return;
            }
            choosenTracking = newValue.getTransporter();
            double q = choosenTracking.getQuantite();
            double v = choosenTracking.getPriceToPay();
            double old = calcRetrait(choosenTracking.getUid());
            System.err.println("Retrait old " + old);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    double val = (old == 0) ? v : BigDecimal.valueOf(v * ((q - old) / q)).setScale(3, RoundingMode.HALF_UP).doubleValue();
                    valueToRet.setText((q - old) + " " + choosenTracking.getIdMarchandise().getNomMarchandise() + " à $" + val);
                }
            });

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
    Transporter tr;
    Retirer r;

    @FXML
    private void pickElementFromTable(Event evt) {
        RetraitView selectobj = tbl_retrait.getSelectionModel().getSelectedItem();
        if (selectobj == null) {
            return;
        }
        r = retraitstore.findById(selectobj.getUid());
        tr = transtore.findById(r.getTransId().getUid());
        transporters_names_cbx.getSelectionModel().select(tr);
    }

    @FXML
    private void delete(Event evt) {
        if (r != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez vous vraiment supprimer le retrait " + r.getReference() + " selectionné", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Attention!");
            alert.setHeaderText(null);
            Optional<ButtonType> showAndWait = alert.showAndWait();
            if (showAndWait.get() == ButtonType.YES) {
                retraitstore.delete("uid", r.getUid());
                MainUI.notify(null, "Succes", "Retrait suprimé avec succès", 4,"Info");
                keti.deleteRetirer(r.getUid()).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> rspns) {
                        if (rspns.isSuccessful()) {
                            System.out.println("Suppression reussi");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable thrwbl) {
                        System.err.println("Erreur " + thrwbl.getMessage());
                    }
                });
            } else {
                alert.hide();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "La selection est vide");
            alert.setTitle("Selectionez un element!");
            alert.setHeaderText(null);
            alert.show();
        }
    }

    @FXML
    private void selectRowPerPage(ActionEvent evt) {
        ComboBox cbx = (ComboBox) evt.getSource();
        rowDataCount = (int) cbx.getSelectionModel().getSelectedItem();
        pagination.setPageFactory(this::createDataPage);
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

    public void search(String query) {
        List<RetraitView> trv = new ArrayList<>();
        System.err.println("Transview " + retraitsvu.size());
        for (RetraitView rv : retraitsvu) {
            if ((rv.getClient() + " " + Constants.DateFormateur.format(rv.getDateRetrait()) + " " + rv.getMarchandise()).toUpperCase()
                    .contains(query.toUpperCase())) {
                trv.add(rv);
            }
        }
        tbl_retrait.getItems().clear();
        tbl_retrait.getItems().addAll(trv);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(trv.size() + " Retraits");
            }
        });
    }

}
