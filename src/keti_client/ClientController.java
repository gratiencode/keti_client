/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Contacts;
import model.Tiers;
import okhttp3.ResponseBody;
import org.dizitart.no2.Nitrite;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.DataId;
import util.Datastorage;
import util.ScreensChangeListener;
import util.TierView;
import util.Token;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class ClientController implements Initializable, ScreensChangeListener {

    UIController controler;
    private String token;
    KetiAPI keti;
    Preferences pref;
    private Nitrite localDatabase;
    int rowsDataCount = 20;

    Datastorage<Tiers> cltdb;
    Datastorage<Contacts> contactdb;
    List<TierView> tiersTab = new ArrayList<>();

    @FXML
    Label count;
    @FXML
    TextField nom_Clt;
    @FXML
    TextField prenom_clt;
    @FXML
    TextField adresse;
    @FXML
    ComboBox<String> type_tiers;
    @FXML
    TextField phone1;
    @FXML
    TextField phone2;
    @FXML
    TextField phone3;
    @FXML
    ImageView notify;
    @FXML
    TableView<TierView> tblTiers;
    @FXML
    TableColumn<TierView, String> colNom;
    @FXML
    TableColumn<TierView, String> colprenom;
    @FXML
    TableColumn<TierView, String> colAdresse;
    @FXML
    TableColumn<TierView, String> colType;
    @FXML
    TableColumn<TierView, String> colcontacts;
    @FXML
    ComboBox<Integer> rowPP;
    @FXML
    Pagination pagination;

    private static ClientController instance;

    public static ClientController getInstance() {
        return instance;
    }

    public ClientController() {
        instance = this;
    }

    @Override
    public void onScreenChange(UIController cont) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configTable();
        configComboBoxes();
        pagination.setPageFactory(this::createDataPage);
    }

    private void configTable() {
        colNom.setCellValueFactory((TableColumn.CellDataFeatures<TierView, String> param) -> new SimpleStringProperty(param.getValue().getTiers().getNom()));
        colprenom.setCellValueFactory((TableColumn.CellDataFeatures<TierView, String> param) -> new SimpleStringProperty(param.getValue().getTiers().getPrenom()));
        colAdresse.setCellValueFactory((TableColumn.CellDataFeatures<TierView, String> param) -> new SimpleStringProperty(param.getValue().getTiers().getAdresse()));
        colType.setCellValueFactory((TableColumn.CellDataFeatures<TierView, String> param) -> new SimpleStringProperty(param.getValue().getTiers().getTypetiers()));
        colcontacts.setCellValueFactory((TableColumn.CellDataFeatures<TierView, String> param) -> new SimpleStringProperty(param.getValue().getContacts().getPhone()));
        tblTiers.getColumns().clear();
        tblTiers.getColumns().addAll(colNom, colprenom, colAdresse, colType, colcontacts);
        tblTiers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void configComboBoxes() {
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowPP.setItems(rows);
        rowPP.getSelectionModel().select(0);
        ObservableList<String> r = FXCollections.observableArrayList(Arrays.asList("Client", "Fournisseur", "Associé", "Créditeur divers", "Débiteur Divers", "Particulier"));
        type_tiers.setItems(r);
        type_tiers.getSelectionModel().select(0);
    }

    private Node createDataPage(int pgindex) {
        try {
            int offset = pgindex * rowsDataCount;
            int limit = Math.min(offset + rowsDataCount, tiersTab.size());
            tblTiers.setItems(FXCollections.observableArrayList(tiersTab.subList(offset, limit)));
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return tblTiers;
    }
    
    @FXML private void selectRowPerPage(ActionEvent evt){
        ComboBox cbx=(ComboBox)evt.getSource();
        rowsDataCount=(int)cbx.getSelectionModel().getSelectedItem();
         pagination.setPageFactory(this::createDataPage);
        try {
            refresh();
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Row set to "+rowsDataCount);
    }

    public void setToken(String token) {
        this.token = token;
        keti = KetiHelper.createService(token);
        KetiHelper.setOnTokenRefreshCallback((Token var1) -> {
            this.token = var1.getToken();
            pref.put("KetiToken", token);
        });
    }

    public void setLocalDatabase(Nitrite localDatabase) {
        this.localDatabase = localDatabase;
        this.cltdb = new Datastorage<>(this.localDatabase, Tiers.class);
        this.contactdb = new Datastorage<>(this.localDatabase, Contacts.class);
        try {
            refresh();
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void createTiers(ActionEvent evt) {
        String type = type_tiers.getSelectionModel().getSelectedItem();
        if ((type.isEmpty() || nom_Clt.getText().isEmpty()
                || prenom_clt.getText().isEmpty()
                || adresse.getText().isEmpty()) && (phone1.getText().isEmpty()
                || phone2.getText().isEmpty() || phone3.getText().isEmpty())) {
            return;
        }
        Tiers tier = new Tiers(DataId.generate());
        tier.setNom(nom_Clt.getText());
        tier.setAdresse(adresse.getText());
        tier.setPrenom(prenom_clt.getText());
        tier.setTypetiers(type);
        Tiers t = cltdb.insert(tier);
        String tvc = "";
        List<Contacts> listC = new ArrayList<>();

        if (!phone1.getText().isEmpty()) {
            Contacts c1 = new Contacts(DataId.generate());
            c1.setIdTiers(t);
            c1.setPhone(phone1.getText());
            Contacts c = contactdb.insert(c1);
            listC.add(c);
            tvc += phone1.getText() + ",";
        }
        if (!phone2.getText().isEmpty()) {
            Contacts c2 = new Contacts(DataId.generate());
            c2.setIdTiers(t);
            c2.setPhone(phone2.getText());
            Contacts c = contactdb.insert(c2);
            listC.add(c);
            tvc += phone2.getText() + ",";
        }
        if (!phone3.getText().isEmpty()) {
            Contacts c3 = new Contacts(DataId.generate());
            c3.setIdTiers(t);
            c3.setPhone(phone3.getText());
            Contacts c = contactdb.insert(c3);
            listC.add(c);
            tvc += phone3.getText() + ",";
        }
        //tvc = tvc.replace(tvc.charAt(tvc.lastIndexOf(",")), ' ').trim();
        Contacts c = new Contacts();
        c.setIdTiers(t);
        c.setPhone(tvc);
        if (!"".equals(tvc) && t != null) {
            TierView tvu = new TierView();
            tvu.setContacts(c);
            tvu.setTiers(t);
            tblTiers.getItems().add(tvu);
            MainUI.notify(notify, "Succès", "Tiers créé avec succès", 3);
            Call<Tiers> createTier = keti.createTier(tier);
            Call<List<Contacts>> createContacts = keti.createContacts(listC);
            try {
                createTier.execute();
                createContacts.execute();
            } catch (IOException ex) {
                Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @FXML
    public void modifyClient(MouseEvent evt) {
        String type = type_tiers.getSelectionModel().getSelectedItem();
        if ((type.isEmpty() || nom_Clt.getText().isEmpty()
                || prenom_clt.getText().isEmpty()
                || adresse.getText().isEmpty()) && (phone1.getText().isEmpty()
                || phone2.getText().isEmpty() || phone3.getText().isEmpty())) {
            return;
        }
        TierView tv = tblTiers.getSelectionModel().getSelectedItem();
        if (tv == null) {
            return;
        }
        Tiers tier = tv.getTiers();
        tier.setNom(nom_Clt.getText());
        tier.setAdresse(adresse.getText());
        tier.setPrenom(prenom_clt.getText());
        tier.setTypetiers(type);
        String tvc = "";

        Tiers uv = cltdb.update("uid", tier.getUid(), tier);
        List<Contacts> listC = new ArrayList<>();
        if (!phone1.getText().isEmpty()) {
            Contacts c1 = contactdb.findAllEquals("phone", tv.getContacts().getPhone().split(",")[0]).get(0);
            c1.setIdTiers(uv);
            c1.setPhone(phone1.getText());
            Contacts c = contactdb.update("phone", tv.getContacts().getPhone().split(",")[0], c1);
            listC.add(c);
            tvc += phone1.getText() + ",";
        }
        if (!phone2.getText().isEmpty()) {
            Contacts c2 = contactdb.findAllEquals("phone", tv.getContacts().getPhone().split(",")[1]).get(0);
            c2.setIdTiers(uv);
            c2.setPhone(phone2.getText());
            Contacts c = contactdb.update("phone", tv.getContacts().getPhone().split(",")[1], c2);
            listC.add(c);
            tvc += phone2.getText() + ",";
        }
        if (!phone3.getText().isEmpty()) {
            Contacts c3 = contactdb.findAllEquals("phone", tv.getContacts().getPhone().split(",")[2]).get(0);
            c3.setIdTiers(uv);
            c3.setPhone(phone3.getText());
            Contacts c = contactdb.update("phone", tv.getContacts().getPhone().split(",")[2], c3);
            listC.add(c);
            tvc += phone3.getText() + ",";
        }
        if (uv != null) {
            MainUI.notify(notify, "Succès", "Tiers modifié avec succès", 3);
            // refresh();
            //here come API update
            keti.updateTier(uv.getUid(), uv).enqueue(new Callback<Tiers>() {
                @Override
                public void onResponse(Call<Tiers> call, Response<Tiers> rspns) {
                    if (rspns.isSuccessful()) {
                        System.out.println("Update avec succes");
                    }
                }

                @Override
                public void onFailure(Call<Tiers> call, Throwable thrwbl) {
                    System.err.println("Ereur ");
                }
            });

            keti.updateContacts(listC).enqueue(new Callback<List<Contacts>>() {
                @Override
                public void onResponse(Call<List<Contacts>> call, Response<List<Contacts>> rspns) {
                    if (rspns.isSuccessful()) {
                        System.out.println("Update succesfully");
                    }
                }

                @Override
                public void onFailure(Call<List<Contacts>> call, Throwable thrwbl) {
                    System.err.println("Erreur update Contact" + thrwbl.getMessage());
                }
            });

        }

    }
    
    @FXML 
    public void refresh(MouseEvent evt){
        try {
            refresh();
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML 
    public void delete(MouseEvent evt){
        String tuid=tblTiers.getSelectionModel().getSelectedItem().getTiers().getUid();
        cltdb.delete("uid", tuid);
        List<Contacts> lc=contactdb.findAllEquals("idTiers", tuid);
        try {
            Response<Tiers> exec = keti.deleteTier(tuid).execute();
            ResponseBody body = keti.deleteContacts(lc).execute().body();
            contactdb.delete("id_tiers", tuid);
        } catch (IOException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh() throws IOException {
        tblTiers.getItems().clear();
        Call<List<Tiers>> tiers = keti.getTiers();
        Response<List<Tiers>> execute = tiers.execute();
        List<Tiers> lst=execute.body();
        for(Tiers t:lst){
            cltdb.insertIfNotExist(t,t.getUid());
            Call<List<Contacts>> contakt = keti.findContacts(t.getUid());
            Response<List<Contacts>> lcont = contakt.execute();
            List<Contacts> body = lcont.body();
            for(Contacts c:body){
                contactdb.insertIfNotExist(c,c.getUid());
            }
        }
        List<Tiers> lt=cltdb.findAll();
        for(Tiers t:lt){
            List<Contacts> cts = contactdb.findAllEquals("id_tiers", t.getUid());
             String ct="";
            for(Contacts c:cts){
                 ct+=c.getPhone()+",";
            }
            Contacts c=new Contacts();
            c.setPhone(ct);
            TierView tv=new TierView();
            tv.setTiers(t);
            tv.setContacts(c);
            tiersTab.add(tv);
            tblTiers.getItems().addAll(tiersTab); 
        }
        count.setText(tblTiers.getItems().size()+" Tiers");
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
    public void pick(MouseEvent evt) {
        TierView v = tblTiers.getSelectionModel().getSelectedItem();
        if (v == null) {
            return;
        }
        nom_Clt.setText(v.getTiers().getNom());
        prenom_clt.setText(v.getTiers().getPrenom());
        adresse.setText(v.getTiers().getAdresse());
        String ph[] = v.getContacts().getPhone().split(",");
        phone1.setText(ph.length > 0 ? ph[0] : "");
        phone2.setText(ph.length > 1 ? ph[1] : "");
        phone3.setText(ph.length > 2 ? ph[2] : "");
        type_tiers.getSelectionModel().select(v.getTiers().getTypetiers());
    }

    @FXML
    public void removeVehicule(MouseEvent evt) {

        ObservableList<TierView> selectedItems = tblTiers.getSelectionModel().getSelectedItems();
        int size = selectedItems.size();
        if (size == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "La selection est vide");
            alert.setTitle("Selectionez un element!");
            alert.setHeaderText(null);
            alert.show();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez vous vraiment supprimer le " + size + " elements selectionnés", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Attention!");
        alert.setHeaderText(null);

        Optional<ButtonType> showAndWait = alert.showAndWait();
        if (showAndWait.get() == ButtonType.YES) {
            selectedItems.forEach((v) -> {
                cltdb.delete("uid", v.getTiers().getUid());
                contactdb.delete("idTiers", v.getTiers().getUid());
            });
            tblTiers.getItems().removeAll(selectedItems);
            MainUI.notify(notify, "Succes", "Vehicule suprimer avec succes", 4);
            keti.deleteTier(selectedItems.get(0).getTiers().getUid()).enqueue(new Callback<Tiers>() {
                @Override
                public void onResponse(Call<Tiers> call, Response<Tiers> rspns) {
                    if (rspns.isSuccessful()) {
                        System.out.println("Suppression reussi");
                    }
                }

                @Override
                public void onFailure(Call<Tiers> call, Throwable thrwbl) {
                    System.err.println("Erreur " + thrwbl.getMessage());
                }
            });
        } else {
            alert.hide();
        }

    }

    public void filter(String query) {
        List<TierView> result = new ArrayList<>();
        for (TierView s : tiersTab) {
            if ((s.getTiers().getNom() + " " + s.getTiers().getPrenom() + ""
                    + " " + s.getTiers().getAdresse() + " " + s.getTiers().getTypetiers() + " "
                    + "" + s.getContacts().getPhone()).toUpperCase().contains(query.toUpperCase())) {
                result.add(s);
            }
        }
        tblTiers.getItems().clear();
        tblTiers.getItems().addAll(result);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(result.size() + " Clients");
            }
        });
    }
}
