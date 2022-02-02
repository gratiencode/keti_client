/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Succursale;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.event.ChangeInfo;
import org.dizitart.no2.event.ChangeType;
import org.dizitart.no2.event.ChangedItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.Datastorage;
import util.ScreensChangeListener;
import util.Token;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class SuccursalController implements Initializable, ScreensChangeListener {

    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;
    Datastorage<Succursale> bd;

    private static SuccursalController instance;

    public static SuccursalController getInstance() {
        return instance;
    }

    public SuccursalController() {
        instance = this;
    }

    @FXML
    private Button btn_save_succursale;
    @FXML
    private ImageView delete_sucursale;

    @FXML
    private ImageView update_sucursale, refresh;

    @FXML
    private Pagination suc_pagination;

    @FXML
    ComboBox<Integer> rowsPP;

    @FXML
    private TableView<Succursale> tbl_succs;
    @FXML
    TableColumn<Succursale, String> nomSuccursale;
    @FXML
    TableColumn<Succursale, String> adresse;
    @FXML
    TableColumn<Succursale, String> directeur;
    @FXML
    Label count;

    List<Succursale> suclist;

    int rowsDataCount = 20;

    @FXML
    private ImageView notify_ok;

    @FXML
    private TextField nom_succ, adress_succ, directeur_succ;
    @FXML
    private ImageView notify_ok1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pref = Preferences.userNodeForPackage(this.getClass());
        KetiHelper.setOnTokenRefreshCallback((Token var1) -> {
            token = var1.getToken();
            pref.put("KetiToken", token);
        });

        initSuccursaleTable();
        tbl_succs.getColumns().clear();
        tbl_succs.getColumns().addAll(nomSuccursale, adresse, directeur);
        tbl_succs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        suc_pagination.setPageFactory(this::createDataPage);
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowsPP.setItems(rows);
        rowsPP.getSelectionModel().select(0);

    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(token);
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        bd = new Datastorage<>(localDatabase, Succursale.class);
        suclist = bd.findAll();
        System.err.println("Sucurlist " + suclist.size());
        for (Succursale s : suclist) {
            tbl_succs.getItems().add(s);
        }
        bd.registerListener((ChangeInfo ci) -> {
            if (ci.getChangeType() == ChangeType.UPDATE) {
                Collection<ChangedItem> changedItems = ci.getChangedItems();
                System.out.println("Changes " + changedItems.size());
            }
        });
        sync();
    }

    public void filter(String query) {
        List<Succursale> result = new ArrayList<>();
        for (Succursale s : suclist) {
            if ((s.getAdresse() + " " + s.getDirecteur() + " " + s.getNomSuccursale()).toUpperCase().contains(query.toUpperCase())) {
                result.add(s);
            }
        }
        tbl_succs.getItems().clear();
        tbl_succs.getItems().addAll(result);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(result.size() + " Succursales");
            }
        });
    }

    @FXML
    private void populateAgencies(MouseEvent evt) {
        sync();
        datanize();
    }

    public void datanize() {
        suclist = bd.findAll();
        System.err.println("Sucurlist " + suclist.size());
        tbl_succs.getItems().clear();
        for (Succursale s : suclist) {
            tbl_succs.getItems().add(s);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(suclist.size() + " Succursales");
            }
        });

    }

    @FXML
    private void insertSucursalle(ActionEvent evt) {
        if (nom_succ.getText().isEmpty() && adress_succ.getText().isEmpty() && directeur_succ.getText().isEmpty()) {
            return;
        }

        Succursale succ = new Succursale(UUID.randomUUID().toString().replace("-", "").toLowerCase(), nom_succ.getText(), adress_succ.getText());
        succ.setDirecteur(directeur_succ.getText());
        Succursale afCount = bd.insert(succ);
        //affichage ku table

        if (afCount != null) {
            tbl_succs.getItems().add(succ);
            keti.createSuccursale(succ)
                    .enqueue(new Callback<Succursale>() {

                        @Override
                        public void onFailure(Call<Succursale> call, Throwable thrwbl) {

                        }

                        @Override
                        public void onResponse(Call<Succursale> call, retrofit2.Response<Succursale> rspns) {
                            if (rspns.isSuccessful()) {
                                Succursale s = rspns.body();

                            }
                        }
                    });
            MainUI.notify(notify_ok, "Succes", "Enregistrement de succursale reussi", 5,"Info");

        }

    }

    @FXML
    private void pickSuccursale(MouseEvent evt) {
        Succursale s = tbl_succs.getSelectionModel().getSelectedItem();
        nom_succ.setText(s.getNomSuccursale());
        adress_succ.setText(s.getAdresse());
        directeur_succ.setText(s.getDirecteur());
    }

    @FXML
    private void selectRowPerPage(ActionEvent evt) {
        ComboBox cbx = (ComboBox) evt.getSource();
        rowsDataCount = (int) cbx.getSelectionModel().getSelectedItem();
        suc_pagination.setPageFactory(this::createDataPage);
        datanize();
        System.out.println("Row set to " + rowsDataCount);
    }

    @FXML
    private void updateSuccursale(MouseEvent evt) {
        Succursale s = tbl_succs.getSelectionModel().getSelectedItem();
        s.setNomSuccursale(nom_succ.getText());
        s.setAdresse(adress_succ.getText());
        s.setDirecteur(directeur_succ.getText());
        Succursale i = bd.update("uid", s.getUid(), s);
        if (i != null) {
            MainUI.notify(notify_ok, "Success", "Succursale modifier avec succes", 3,"info");
            datanize();
            keti.updateSuccursale(s.getUid(), s).enqueue(new Callback<Succursale>() {
                @Override
                public void onResponse(Call<Succursale> call, Response<Succursale> rspns) {
                    System.out.println("Update " + rspns.code());
                }

                @Override
                public void onFailure(Call<Succursale> call, Throwable thrwbl) {
                }
            });
        }
    }

    private void sync() {
        
    }

    @FXML
    private void deleteSuccursale(MouseEvent evt) {

        ObservableList<Succursale> selectedItems = tbl_succs.getSelectionModel().getSelectedItems();
        int size = selectedItems.size();
        if (size == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "La selection est vide");
            alert.setTitle("Selectionez un element!");
            alert.setHeaderText(null);
            alert.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez vous vraiment supprimer le " + size + " elements selectionnés", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Attention!");
        alert.setHeaderText(null);

        Optional<ButtonType> showAndWait = alert.showAndWait();
        if (showAndWait.get() == ButtonType.YES) {
            selectedItems.forEach((s) -> {
                bd.delete("uid", s.getUid());
            });
            tbl_succs.getItems().removeAll(selectedItems);
            MainUI.notify(notify_ok, "Succes", "Succursale suprimer avec succes", 4,"info");
            keti.removeSucursals(selectedItems).enqueue(new Callback<List<Succursale>>() {
                @Override
                public void onResponse(Call<List<Succursale>> call, Response<List<Succursale>> rspns) {
                    System.err.println("Delete sucursale "+rspns.message());
                    if (rspns.isSuccessful()) {
                        System.err.println("Suppression reuusi");
                    }
                }

                @Override
                public void onFailure(Call<List<Succursale>> call, Throwable thrwbl) {
                    System.err.println("Erreur supression " + thrwbl.getMessage());
                }
            });
        } else {
            alert.hide();
        }

    }

    private void initSuccursaleTable() {
        nomSuccursale.setCellValueFactory((TableColumn.CellDataFeatures<Succursale, String> param) -> new SimpleStringProperty(param.getValue().getNomSuccursale()));
        adresse.setCellValueFactory((TableColumn.CellDataFeatures<Succursale, String> param) -> new SimpleStringProperty(param.getValue().getAdresse()));
        directeur.setCellValueFactory((TableColumn.CellDataFeatures<Succursale, String> param) -> new SimpleStringProperty(param.getValue().getDirecteur()));
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

    private Node createDataPage(int pgindex) {
        try {
            int offset = pgindex * rowsDataCount;
            int limit = Math.min(offset + rowsDataCount, suclist.size());
            tbl_succs.setItems(FXCollections.observableArrayList(suclist.subList(offset, limit)));
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return tbl_succs;
    }
}
