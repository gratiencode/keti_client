/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Marchandise;
import org.dizitart.no2.Nitrite;
import util.DataId;
import util.Datastorage;
import views.MainUI;

/**
 * FXML Controller class
 *
 * @author eroot
 */
public class MarchandiseController implements Initializable {
    
    @FXML TextField nom_march;
    @FXML TextField descript_march;
    @FXML TextField prix_march;
    
    
    
    Nitrite localDatabase;
    Datastorage<Marchandise> marchdb;
    
    @FXML ImageView notify;
    
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }  
    

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
        marchdb = new Datastorage<>(this.localDatabase, Marchandise.class);
    }
    
    @FXML public void createGoods(ActionEvent evt){
        if(nom_march.getText().isEmpty() || descript_march.getText().isEmpty() || prix_march.getText().isEmpty()){
            return;
        }
        Marchandise m=new Marchandise(DataId.generate());
        m.setNomMarchandise(nom_march.getText());
        m.setDescription(descript_march.getText());
        m.setPrix(Double.valueOf(prix_march.getText()));
        Marchandise insert = marchdb.insert(m);
        if(insert!=null){
            MainUI.notify(notify, "Succès", "Marchandise créée avec succès", 3);
            TransporterController.getInstance().addGoodsItem(m);
            
        }
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
    
}
