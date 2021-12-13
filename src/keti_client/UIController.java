/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import util.ScreensChangeListener;

/**
 *
 * @author eroot
 */
public class UIController extends AnchorPane{
    
    HashMap<String,Node> screens=new HashMap<>();
    String user;
    String token;

    public UIController() {
        super();
    }

    public void addScreen(String key, Node value) {
        screens.put(key, value);
    }

    public Node getScreen(String key) {
        return screens.get(key);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
    
    
    public boolean loadScreen(String name,String pathRes){
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource(pathRes));
            
            Parent loadScreen=(Parent)loader.load();
                   // FXMLLoader.load(getClass().getResource(pathRes));
            ScreensChangeListener scl=((ScreensChangeListener)loader.getController());
            scl.onScreenChange(this);
            addScreen(name, loadScreen);
            System.out.println("Screen count "+screens.size());
            return true;
        } catch (IOException ex) {
            Logger.getLogger(UIController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }
    
    public boolean setScreen(String name){
        if(screens.get(name)!=null){
            final DoubleProperty opacity= opacityProperty();
            if(!getChildren().isEmpty()){
                Timeline fadeOut=new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                       getChildren().remove(0);
                       getChildren().set(0, screens.get(name));
                       Timeline fadeIn=new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                               new KeyFrame(new Duration(800), new KeyValue(opacity, 1.0)));
                       fadeIn.play();
                    }
                }, new KeyValue(opacity, 0.0)));
                fadeOut.play();
            }else{
                setOpacity(0.0);
                getChildren().add(screens.get(name));
                Timeline fadeIn=new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                               new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0)));
                       fadeIn.play();
            }
            return true;
        }else{
            System.out.println("Ecran non encore pret");
           return false; 
        }
    }
    
    
    public boolean unloadScreen(String name){
        if(screens.remove(name)==null){
            System.out.println("Screen n'existait pas");
            return false;
        }else{
          return true;  
        }
    }
    
    
}
