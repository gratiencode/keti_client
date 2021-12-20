/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Marchandise;
import model.Succursale;
import model.Tiers;
import model.Transporter;
import model.Vehicule;
import model.Voyage;
import util.Constants;
import util.DataId;
import util.TransporterView;
import views.MainUI;

/**
 * FXML Controller class
 *
 * @author eroot
 */
public class PanierController implements Initializable {

    @FXML
    Label choosen_goods;
    @FXML
    Label total_sum;
    @FXML
    TextField quantite;
    @FXML
    TextField prix_unit;

    private Marchandise marchandise;
    private Vehicule vehicule;
    private Tiers tiers;
    private double price2pay = 0;
    private Integer tracking;
    private Succursale succursale;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void write(KeyEvent evt) {
        TextField tf = (TextField) evt.getSource();
        tf.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                try {
                    double pu = prix_unit.getText().isEmpty() ? 0 : Double.parseDouble(prix_unit.getText());
                    double qt = quantite.getText().isEmpty() ? 0 : Double.parseDouble(quantite.getText());
                    price2pay = (pu * qt);
                    total_sum.setText("Total : " + price2pay);
                } catch (NumberFormatException ex) {
                }
            }
        });
    }

    @FXML
    public void confirmChoice(ActionEvent evt) {
        if (quantite.getText().isEmpty() || prix_unit.getText().isEmpty()) {
            return;
        }
        TransporterView tv = new TransporterView();
        tv.setMarchandise(marchandise);
        Transporter t = new Transporter(DataId.generate());
        t.setDateTransport(new Date());
        t.setIdMarchandise(marchandise);
        t.setIdTiers(tiers);
        t.setIdVehicule(vehicule);
        t.setObservation("A retirer");
        t.setPaidPrice(0);
        t.setPriceToPay(price2pay);
        t.setQuantite(Double.parseDouble(quantite.getText()));
        t.setTracking(tracking);
        tv.setTransporter(t);
        Voyage v = new Voyage(DataId.generate());
        v.setDateArriver(new Date());
        v.setEtat(Constants.VEHICLE_LOADED);
        v.setVehiculeId(vehicule);
        v.setSuccursalleId(succursale);
        tv.setVoyage(v);
        TransporterController TC=TransporterController.getInstance();
        TC.addChoosenGoods(tv);
        TC.setTotalApayer(price2pay); 
        close(evt);

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

    public void setMarchandise(Marchandise marchandise) {
        this.marchandise = marchandise;
        choosen_goods.setText(this.marchandise.getNomMarchandise() + " " + this.marchandise.getDescription());
        prix_unit.setText(String.valueOf(this.marchandise.getPrix()));
    }

    public void setTiers(Tiers tiers) {
        this.tiers = tiers;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public void setTracking(Integer tracking) {
        this.tracking = tracking;
    }

    public void setSuccursale(Succursale succursale) {
        this.succursale = succursale;
    }

}
