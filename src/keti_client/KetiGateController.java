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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Credentials;
import model.Succursale;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.Constants;
import util.LoginResult;
import util.ScreensChangeListener;
import util.Token;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class KetiGateController implements Initializable, ScreensChangeListener {

    UIController controler;

    @FXML
    private Button btn_connect;

    @FXML
    private TextField username_field;
    @FXML
    private PasswordField pswd_field;

    String token;
    ObjectRepository<Succursale> noSqlSucc;

    Nitrite db;

    //succursale
    @FXML
    private Button btn_save_succursale;
    @FXML
    private ImageView delete_sucursale;

    @FXML
    private ImageView update_sucursale;
    @FXML
    private ImageView notify_ok;

    @FXML
    private TextField nom_succ, adress_succ, directeur_succ;

    @FXML
    private TableView<Succursale> tbl_succs;
    @FXML
    TableColumn<Succursale, String> nomSuccursale;
    @FXML
    TableColumn<Succursale, String> adresse;
    @FXML
    TableColumn<Succursale, String> directeur;

    //end succursalle
    @FXML
    AnchorPane ppalPane;

    Stage pStage;
    KetiAPI keti;
    Preferences pref;

    public KetiGateController() {
        keti = KetiHelper.createService(null);
        pref = Preferences.userNodeForPackage(this.getClass());
    }

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void handleLoginAction(ActionEvent event) {
        if (pswd_field.getText().isEmpty() || username_field.getText().isEmpty()) {
            return;
        }

        Credentials cred = new Credentials(username_field.getText(), pswd_field.getText());

        keti.login(cred).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> rspns) {
                System.out.println("result " + rspns.message());
                if (rspns.isSuccessful()) {
                    LoginResult lr = rspns.body();
                    Token tokenx = lr.getToken();
                    token = tokenx.getToken();
                    pref.put("KetiToken", token);
                    controler.setScreen(Constants.MAIN);
                    pref.put("uname", cred.getUsername());
                    pref.put("pswd", cred.getPassword()); 
                    Platform.runLater(() -> {
                        MainUI.replaceView(this.getClass(), "mainui.fxml", 790, 1550, cred.getUsername(), token, lr);
                        Keti_client.stagex.close();
                    });

                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable thrwbl) {
                System.err.println("Erreur " + thrwbl.getMessage());
            }
        });

    }

    @FXML
    private void exit(MouseEvent event) {
        System.exit(0);
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @Override
    public void onScreenChange(UIController cont) {
        this.controler = cont;
    }

}
