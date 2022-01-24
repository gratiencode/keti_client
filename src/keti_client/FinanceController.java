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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Comptefin;
import model.Succursale;
import org.dizitart.no2.Nitrite;
import retrofit2.Call;
import retrofit2.Response;
import util.DataId;
import util.Datastorage;
import util.ScreensChangeListener;
import views.MainUI;

/**
 * FXML Controller class
 *
 * @author eroot
 */
public class FinanceController implements Initializable, ScreensChangeListener {

    Datastorage<Comptefin> findb;
    @FXML
    Label count;
    KetiAPI keti;
    String token;

    @FXML
    TableView<Comptefin> table;
    @FXML
    TableColumn<Comptefin, String> collibelle;
    @FXML
    TableColumn<Comptefin, String> collnom_bak;
    @FXML
    TableColumn<Comptefin, String> collnumero_compte;
    @FXML
    TableColumn<Comptefin, String> colltype;
    @FXML
    TableColumn<Comptefin, Number> collsolde;
    @FXML
    Pagination pagination;
    @FXML
    ComboBox<Integer> rowPP;

    @FXML
    TextField libelle;
    @FXML
    TextField nom_bank;
    @FXML
    TextField numro_comte;
    @FXML
    ComboBox<String> type_compte;
    @FXML
    TextField solde_min;
    @FXML Button go_to_ecriture;

    ObservableList<Comptefin> comptes;

    int rowsDataCount = 20;

    private static FinanceController instance;
    private Succursale sucursaleId;

    public FinanceController() {
        instance = this;
    }

    public static FinanceController getInstance() {
        return instance;
    }

    public void setToken(Nitrite db, String token) {
        findb = new Datastorage<>(db, Comptefin.class);
        this.token = token;
        keti = KetiHelper.createService(token);
        refresh();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        config();
        pagination.setPageFactory(this::createDataPage);
    }

    private void config() {
        comptes = FXCollections.observableArrayList();
        collibelle.setCellValueFactory((TableColumn.CellDataFeatures<Comptefin, String> param) -> new SimpleStringProperty(param.getValue().getLibelle()));
        colltype.setCellValueFactory((TableColumn.CellDataFeatures<Comptefin, String> param) -> new SimpleStringProperty(param.getValue().getTypeDeCompte()));
        collnom_bak.setCellValueFactory((TableColumn.CellDataFeatures<Comptefin, String> param) -> new SimpleStringProperty(param.getValue().getNomBanque()));
        collnumero_compte.setCellValueFactory((TableColumn.CellDataFeatures<Comptefin, String> param) -> new SimpleStringProperty(param.getValue().getNumeroCompte()));
        collsolde.setCellValueFactory((TableColumn.CellDataFeatures<Comptefin, Number> param) -> new SimpleDoubleProperty(param.getValue().getSoldeMinimum()));
        configComboBoxes();
    }

    private void configComboBoxes() {
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowPP.setItems(rows);
        rowPP.getSelectionModel().select(0);
        ObservableList<String> r = FXCollections.observableArrayList(Arrays.asList("Caisse (Espece)", "Compte courrant", "Compte epargne", "Autres compte"));
        type_compte.setItems(r);
        type_compte.getSelectionModel().select(0);
    }

    @FXML
    public void createFinAccount(ActionEvent evt) {
        if (libelle.getText().isEmpty()
                || nom_bank.getText().isEmpty()
                || numro_comte.getText().isEmpty()
                || solde_min.getText().isEmpty() || type_compte.getSelectionModel().getSelectedItem() == null) {
            MainUI.notify(null, "Erreur", "Veuillez completer tous les champs", 4, "error");
            return;
        }
        Comptefin cpt = new Comptefin(DataId.generate());
        cpt.setSucursaleId(sucursaleId);
        cpt.setLibelle(libelle.getText());
        cpt.setNomBanque(nom_bank.getText());
        cpt.setNumeroCompte(numro_comte.getText());
        cpt.setSoldeMinimum(Double.valueOf(solde_min.getText()));
        cpt.setTypeDeCompte(type_compte.getSelectionModel().getSelectedItem().toString());
        createAccount(cpt);

    }

    @FXML
    public void updateFinAccount(Event evt) {
        if (libelle.getText().isEmpty()
                || nom_bank.getText().isEmpty()
                || numro_comte.getText().isEmpty()
                || solde_min.getText().isEmpty() || type_compte.getSelectionModel().getSelectedItem() == null) {
            MainUI.notify(null, "Erreur", "Veuillez selectionner un élément du tableau", 4, "error");
            return;
        }
        Comptefin cpt = table.getSelectionModel().getSelectedItem();
        cpt.setLibelle(libelle.getText());
        cpt.setNomBanque(nom_bank.getText());
        cpt.setNumeroCompte(numro_comte.getText());
        cpt.setSoldeMinimum(Double.valueOf(solde_min.getText()));
        cpt.setSucursaleId(sucursaleId);
        cpt.setTypeDeCompte(type_compte.getSelectionModel().getSelectedItem());
        Comptefin c=findb.update("uid", cpt.getUid(), cpt);
        if(c!=null){
            MainUI.notify(null, "Succes", "Modification faite avec succes", 4, "info");
            refresh();
        }
    }

    @FXML
    public void deleteFinAccount(Event evt) {
        Comptefin cpt = table.getSelectionModel().getSelectedItem();
        if (cpt == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "La selection est vide");
            alert.setTitle("Selectionez un element!");
            alert.setHeaderText(null);
            alert.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez vous vraiment supprimer l'element selectionné ?", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Attention!");
        alert.setHeaderText(null);
        Optional<ButtonType> showAndWait = alert.showAndWait();
        if (showAndWait.get() == ButtonType.YES) {
            findb.delete("uid", cpt.getUid());
            table.getItems().removeAll(cpt);
            MainUI.notify(null, "Succes", "Element supprimé avec succès!", 4, "info");
            keti.deleteCompteFin(cpt.getUid())
                    .enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> rspns) {
                     if(rspns.isSuccessful()){
                         System.out.println("Deep delete runs successfully");
                     }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable thrwbl) {
                   thrwbl.printStackTrace();
                }
            });
            
        }
    }

    @FXML
    public void refreshFinAccount(Event evt) {
        refresh();
    }
    
    public void refresh(){
        List<Comptefin> lctf=findb.findAll();
        comptes.clear();
        comptes.addAll(lctf);
        table.setItems(comptes);   
        pagination.setPageFactory(this::createDataPage);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
               count.setText(comptes.size()+" Comptes");
            }
        });
    }

    @FXML
    private void pickAccount(MouseEvent evt) {
         Comptefin cpt = table.getSelectionModel().getSelectedItem();
        nom_bank.setText(cpt.getNomBanque());
       libelle.setText(cpt.getLibelle());
        numro_comte.setText(cpt.getNumeroCompte());
        solde_min.setText(String.valueOf(cpt.getSoldeMinimum()));
        type_compte.getSelectionModel().select(cpt.getTypeDeCompte());
        if(cpt!=null){
            go_to_ecriture.setDisable(false);
        }
        
    }
    
    @FXML public void gotoecriture(ActionEvent evt){
        MainController.getInstance().gotoecriture(table.getSelectionModel().getSelectedItem());
    }
    private void createAccount(Comptefin compte) {
        Comptefin insert = findb.insert(compte);
        if (insert != null) {
            MainUI.notify(null, "Succès", "Compte financier enregistré avec succès", 4, "Info");
        }
    }

    public void setSucursaleId(Succursale sucursaleId) {
        this.sucursaleId = sucursaleId;
    }

    private Node createDataPage(int pgindex) {
        try {
            int offset = pgindex * rowsDataCount;
            int limit = Math.min(offset + rowsDataCount, comptes.size());
            table.setItems(FXCollections.observableArrayList(comptes.subList(offset, limit)));
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return table;
    }

    @FXML
    private void selectRowPerPage(ActionEvent evt) {
        ComboBox cbx = (ComboBox) evt.getSource();
        rowsDataCount = (int) cbx.getSelectionModel().getSelectedItem();
        refresh();
        //pagination.setPageFactory(this::createDataPage);
        System.out.println("Row set to " + rowsDataCount);
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
    
    public void search(String query){
        List<Comptefin> result=new ArrayList<>();
        for(Comptefin c:comptes){
            if((c.getTypeDeCompte()+" "+c.getLibelle()+" "
                    + ""+c.getNumeroCompte()+""
                    + " "+c.getNomBanque()+" "+c.getSoldeMinimum()).toUpperCase().contains(query.toUpperCase())){
                result.add(c);
            }
        }
        table.getItems().clear();
        table.getItems().addAll(result);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(result.size()+" Comptes");
            }
        });
    }

    @Override
    public void onScreenChange(UIController cont) {
        
    }
}
