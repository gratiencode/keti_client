/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import util.ScreensChangeListener;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class DepenseController implements Initializable, ScreensChangeListener{

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     
    }

    @Override
    public void onScreenChange(UIController cont) {
     
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
