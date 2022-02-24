/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Succursale;
import model.User;
import org.dizitart.no2.Nitrite;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.DataId;
import util.Datastorage;
import util.Role;
import views.MainUI;

/**
 * FXML Controller class
 *
 * @author eroot
 */
public class UtilisateurController implements Initializable {

    KetiAPI keti;
    User user;
    @FXML
    ListView<User> listUser;
    @FXML
    ComboBox<Succursale> cbx_Sucursale;
    @FXML
    ComboBox<Role> cbx_Role;
    @FXML
    CheckBox activate;
    @FXML
    TextField nom;
    @FXML
    TextField prenom, username;
    @FXML
    PasswordField pswd, pswd1;
    Datastorage<Succursale> datarsale;
    Datastorage<User> dataser;
    Succursale sucursale;
    Role role;
    String token;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configCombos();
        
    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(token);
    }
    ObservableList<User> users;

    public void setDatabase(Nitrite db) {
        this.datarsale = new Datastorage<>(db, Succursale.class);
        dataser = new Datastorage<>(db, User.class);
        ObservableList<Succursale> suclist = FXCollections.observableArrayList();
        suclist.setAll(datarsale.findAll());
        cbx_Sucursale.setItems(suclist);
        ObservableList<Role> roles = FXCollections.observableArrayList(Role.Comptable, Role.Directeur, Role.Associe, Role.Caissier, Role.Administrator);
        cbx_Role.setItems(roles);
        users = FXCollections.observableArrayList(dataser.findAll());
        listUser.setItems(users);
        activate.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    if (user != null) {
                        user.setActif(false);
                        keti.updateUser(user.getUid(), user)
                                .enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> rspns) {
                                        System.err.println("Resposne update user " + rspns.message());
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                MainUI.notify(null, "Succès", "Utilisateur modifié avec succès", 4, "info");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable thrwbl) {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                MainUI.notify(null, "Erreur", "Erreur : [\n" + thrwbl.getMessage() + "\n]", 16, "error");
                                            }
                                        });
                                    }
                                });
                    }
                }
            }
        });
    }

    @FXML
    public void save(Event evt) {
        if (nom.getText().isEmpty() || prenom.getText().isEmpty()
                || pswd.getText().isEmpty() || pswd1.getText().isEmpty() || username.getText().isEmpty()
                || cbx_Sucursale.getValue() == null
                || cbx_Role.getValue() == null) {
            MainUI.notify(null, "Erreur", "Veuillez completer tous les champs", 3, "error");
            return;
        }
        User user = new User(DataId.generate());
        user.setActif(true);
        user.setCreatedAt(new Date());
        user.setIdSucursale(cbx_Sucursale.getValue());
        user.setNom(nom.getText());
        user.setUsername(username.getText());
        user.setPrenom(prenom.getText());
        user.setRole(cbx_Role.getValue().name());
        if (!pswd.getText().equals(pswd1.getText())) {
            MainUI.notify(null, "Erreur", "Les deux mot de passes ne sont pas identiques", 3, "error");
            return;
        }
        user.setPassword(pswd.getText());
        keti.createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> rspns) {
                System.err.println(" User creation response " + rspns.message());
                if (rspns.isSuccessful()) {
                    User u = rspns.body();
                    users.add(u);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            MainUI.notify(null, "Succès", "Utilisateur créé avec succès", 4, "info");
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable thrwbl) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        MainUI.notify(null, "Erreur", "Erreur : [\n" + thrwbl.getMessage() + "\n]", 16, "error");
                    }
                });
            }
        });

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

    @FXML
    private void close(Event evt) {
        Node n = (Node) evt.getSource();
        Stage st = (Stage) n.getScene().getWindow();
        st.close();
    }

    private void configCombos() {
        cbx_Sucursale.setConverter(new StringConverter<Succursale>() {
            @Override
            public String toString(Succursale object) {
                return object == null ? null : object.getNomSuccursale() + " " + object.getAdresse();
            }

            @Override
            public Succursale fromString(String string) {
                return cbx_Sucursale.getItems()
                        .stream()
                        .filter(clt -> (clt.getNomSuccursale() + " " + clt.getAdresse())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });
        cbx_Role.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role object) {
                return object == null ? null : object.name();
            }

            @Override
            public Role fromString(String string) {
                return cbx_Role.getItems()
                        .stream()
                        .filter(v -> (v.name())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(null);
            }
        });
        listUser.setCellFactory((ListView<User> param) -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            setText(item.getNom() + " " + item.getPrenom() + " Role : " + item.getRole());
                        }
                    });
                }
            }

        });
        cbx_Sucursale.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Succursale> observable, Succursale oldValue, Succursale newValue) -> {
            sucursale = newValue;
        });
        cbx_Role.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Role> observable, Role oldValue, Role newValue) -> {
            role = newValue;
        });

        listUser.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends User> observable, User oldValue, User newValue) -> {
            user = newValue;
            nom.setText(user.getNom());
            prenom.setText(user.getPrenom());
            username.setText(user.getUsername());
            activate.setSelected(user.getActif());
            cbx_Role.getSelectionModel().select(Role.valueOf(user.getRole()));
            sucursale = datarsale.findById(user.getIdSucursale().getUid());
            cbx_Sucursale.getSelectionModel().select(sucursale);
        });
    }
}
