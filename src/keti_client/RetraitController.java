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
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Transporter;
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import util.ScreensChangeListener;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class RetraitController implements Initializable,ScreensChangeListener{
    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;
    private Nitrite localDatabase;
    
    ObjectRepository<Transporter> noSqlTransp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler=cont; 
    }

    public void setToken(String token) {
        this.token = token;
        keti=KetiHelper.createService(token);
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
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
