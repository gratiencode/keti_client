/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Succursale;
import model.Tiers;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import util.ScreensChangeListener;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class DechargementController implements Initializable, ScreensChangeListener{

    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;
    private Nitrite localDatabase;
    
    ObjectRepository<Tiers> noSqlSucc;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
     
    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler=cont;
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
