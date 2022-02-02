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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
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
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.DataId;
import util.Datastorage;
import util.ScreensChangeListener;
import util.Token;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class VehiculeController implements Initializable, ScreensChangeListener {

    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;

    Datastorage<Vehicule> vehiculesdb;
    List<Vehicule> vehicules;

    @FXML
    TextField plaque;
    @FXML
    TextField marque;
    @FXML
    TextField model;
    @FXML
    TextField nbreRoue;
    @FXML
    ComboBox<String> typeVehicule;
    @FXML
    ComboBox<Integer> rowPP;
    @FXML
    ImageView notify;

    @FXML
    TableView<Vehicule> tblVeh;

    @FXML
    TableColumn<Vehicule, String> colplaque;
    @FXML
    TableColumn<Vehicule, String> colmark;
    @FXML
    TableColumn<Vehicule, String> colmodel;
    @FXML
    TableColumn<Vehicule, String> colnbroue;
    @FXML
    TableColumn<Vehicule, String> coltype;
    @FXML
    Pagination pagination;
    @FXML
    Label count;

    int rowsDataCount = 20;
    
    private static VehiculeController instance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configColumns();
        configComboBoxes();
        pagination.setPageFactory(this::createDataPage);
    }

    public VehiculeController() {
        instance=this;
    }
    
    public static VehiculeController getInstance(){
        return instance;
    }
    
    

    private void configColumns() {
        colmark.setCellValueFactory((TableColumn.CellDataFeatures<Vehicule, String> param) -> new SimpleStringProperty(param.getValue().getMarque()));
        colplaque.setCellValueFactory((TableColumn.CellDataFeatures<Vehicule, String> param) -> new SimpleStringProperty(param.getValue().getPlaque()));
        colmodel.setCellValueFactory((TableColumn.CellDataFeatures<Vehicule, String> param) -> new SimpleStringProperty(param.getValue().getModeleVehicule()));
        colnbroue.setCellValueFactory((TableColumn.CellDataFeatures<Vehicule, String> param) -> new SimpleStringProperty(param.getValue().getNombreDeRoue()));
        coltype.setCellValueFactory((TableColumn.CellDataFeatures<Vehicule, String> param) -> new SimpleStringProperty(param.getValue().getTypeVehicule()));
        tblVeh.getColumns().clear();
        tblVeh.getColumns().addAll(colplaque, colmark, colmodel, coltype, colnbroue);
        tblVeh.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    private void configComboBoxes() {
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowPP.setItems(rows);
        rowPP.getSelectionModel().select(0);
        ObservableList<String> r = FXCollections.observableArrayList(Arrays.asList("Camion", "Camionette", "Voiture", "Bus", "Avion", "Bateau", "Train"));
        typeVehicule.setItems(r);
        typeVehicule.getSelectionModel().select(0);
    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
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
            int limit = Math.min(offset + rowsDataCount, vehicules.size());
            tblVeh.setItems(FXCollections.observableArrayList(vehicules.subList(offset, limit)));
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return tblVeh;
    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(token);
        KetiHelper.setOnTokenRefreshCallback((Token var1) -> {
            this.token = var1.getToken();
            pref.put("KetiToken", token);
        });
        syncDown();
    }

    public void setDatabase(Nitrite ntdb) {
        vehiculesdb = new Datastorage<>(ntdb, Vehicule.class);
        syncDown();
        refresh();
        
    }

    public void refresh() {
        vehicules = vehiculesdb.findAll();
        tblVeh.getItems().clear();
        for (Vehicule v : vehicules) {
            tblVeh.getItems().add(v);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(vehicules.size() + " Vehicules");
            }
        });
    }

    public void syncDown() {
        keti.getVehicules().enqueue(new Callback<List<Vehicule>>() {
            @Override
            public void onResponse(Call<List<Vehicule>> call, Response<List<Vehicule>> rspns) {
                if (rspns.isSuccessful()) {
                    List<Vehicule> lvs = rspns.body();
                    for (Vehicule v : lvs) {
                        if(vehiculesdb==null){
                            return;
                        }
                        vehiculesdb.insertIfNotExist(v, v.getUid()); 
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Vehicule>> call, Throwable thrwbl) {

            }
        });
    }

    @FXML
    public void createVehicule(ActionEvent evt) {
        String vt = typeVehicule.getSelectionModel().getSelectedItem();
        if (plaque.getText().isEmpty() || marque.getText().isEmpty() || model.getText().isEmpty() || nbreRoue.getText().isEmpty() || vt.isEmpty()) {
            return;
        }
        Vehicule v = new Vehicule(DataId.generate());
        v.setMarque(marque.getText());
        v.setModeleVehicule(model.getText());
        v.setNombreDeRoue(nbreRoue.getText());
        v.setPlaque(plaque.getText());
        v.setTypeVehicule(vt);
        Vehicule insert = vehiculesdb.insert(v);
        if (insert != null) {
            MainUI.notify(notify, "Succès", "Véhicule créé avec succès", 3);
            tblVeh.getItems().add(insert);
            //here come API creation
            keti.createVehicule(v).enqueue(new Callback<Vehicule>() {
                @Override
                public void onResponse(Call<Vehicule> call, Response<Vehicule> rspns) {
                    if (rspns.isSuccessful()) {
                        System.out.println(" created succesfuly ");
                    }
                }

                @Override
                public void onFailure(Call<Vehicule> call, Throwable thrwbl) {
                    System.err.println("Erreur " + thrwbl.getMessage());
                }
            });
        }
    }

    @FXML
    public void modifyVehicule(MouseEvent evt) {
        String vt = typeVehicule.getSelectionModel().getSelectedItem();
        if (plaque.getText().isEmpty() || marque.getText().isEmpty() || model.getText().isEmpty() || nbreRoue.getText().isEmpty() || vt.isEmpty()) {
            return;
        }
        Vehicule v = tblVeh.getSelectionModel().getSelectedItem();
        if (v == null) {
            return;
        }
        v.setMarque(marque.getText());
        v.setModeleVehicule(model.getText());
        v.setNombreDeRoue(nbreRoue.getText());
        v.setPlaque(plaque.getText());
        v.setTypeVehicule(vt);
        Vehicule uv = vehiculesdb.update("uid", v.getUid(), v);
        if (uv != null) {
            MainUI.notify(notify, "Succès", "Véhicule modifié avec succès", 3);
            refresh();
            //here come API update
            keti.updateVehicule(v.getUid(), v).enqueue(new Callback<Vehicule>() {
                @Override
                public void onResponse(Call<Vehicule> call, Response<Vehicule> rspns) {
                    if (rspns.isSuccessful()) {
                        System.out.println("Update avec succes");
                    }
                }

                @Override
                public void onFailure(Call<Vehicule> call, Throwable thrwbl) {
                    System.err.println("Ereur ");
                }
            });

        }

    }

    @FXML
    public void removeVehicule(MouseEvent evt) {

        ObservableList<Vehicule> selectedItems = tblVeh.getSelectionModel().getSelectedItems();
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
            selectedItems.forEach((v) -> {
                vehiculesdb.delete("uid", v.getUid());
            });
            tblVeh.getItems().removeAll(selectedItems);
            MainUI.notify(notify, "Succes", "Vehicule suprimer avec succes", 4,"Info");
            keti.deleteVehicule(selectedItems.get(0).getUid()).enqueue(new Callback<Vehicule>() {
                @Override
                public void onResponse(Call<Vehicule> call, Response<Vehicule> rspns) {
                    if (rspns.isSuccessful()) {
                        System.out.println("Suppression reussi");
                    }
                }

                @Override
                public void onFailure(Call<Vehicule> call, Throwable thrwbl) {
                    System.err.println("Erreur " + thrwbl.getMessage());
                }
            });
        } else {
            alert.hide();
        }

    }

    @FXML
    public void refresh(MouseEvent evt) {
        syncDown();
        refresh();
    }

    @FXML
    public void pickVehicule(MouseEvent evt) {
        Vehicule v = tblVeh.getSelectionModel().getSelectedItem();
        plaque.setText(v.getPlaque());
        marque.setText(v.getMarque());
        model.setText(v.getModeleVehicule());
        nbreRoue.setText(v.getNombreDeRoue());
        typeVehicule.getSelectionModel().select(v.getTypeVehicule());
    }
    
    public void filter(String query){
        List<Vehicule> result=new ArrayList<>();
        for(Vehicule s:vehicules){
            if((s.getMarque()+" "+s.getModeleVehicule()+" "+s.getNombreDeRoue()+" "+s.getPlaque()+" "+s.getTypeVehicule()).toUpperCase().contains(query.toUpperCase())){
                result.add(s);
            }
        }
        tblVeh.getItems().clear();
        tblVeh.getItems().addAll(result);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               count.setText(result.size()+" Vehicules");
            }
        });
    }

}
