/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Window;
import model.Marchandise;
import model.Tiers;
import model.Transporter;
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class TransportDialog extends Dialog<Transporter> {

    @FXML
    ButtonType addVeh, addClient, addMse, chooseMse, unchooseMse, saveBtn, cancel;
    @FXML
    Label tracking;
    @FXML
    ListView<Marchandise> listmarch;
    @FXML
    TableView<Transporter> beforeSave;
    @FXML
    ComboBox<Vehicule> vehicules;
    @FXML
    ComboBox<Tiers> clients;
    private String token;
    private Nitrite localDatabase;

    public TransportDialog(Window owner) {
        DialogPane dialog = MainUI.element("transporter.fxml");
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        dialog.lookupButton(saveBtn);
       // dialog.lookupButton(addVeh).addEventFilter(ActionEvent.ANY,this::switchToVehicule);
        dialog.lookupButton(addClient);
        dialog.lookupButton(addMse);
        dialog.lookupButton(chooseMse);
        dialog.lookupButton(unchooseMse);
        dialog.lookupButton(cancel);
        setResizable(true);
        setTitle("Chargement");
        setDialogPane(dialog);
        

    }
    
    

    public TransportDialog() {
    }
    


    public void setToken(String token) {
        this.token = token;
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
    }
}
