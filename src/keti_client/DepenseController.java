/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keti_client;

import core.KetiAPI;
import core.KetiHelper;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import model.Comptefin;
import model.Depense;
import model.Payer;
import model.Succursale;
import model.Tiers;
import model.Transporter;
import model.Vehicule;
import org.dizitart.no2.Nitrite;
import retrofit2.Response;
import util.ComboBoxAutoCompletion;
import util.Constants;
import util.DataId;
import util.Datastorage;
import util.ScreensChangeListener;
import util.TransactionView;
import views.MainUI;

/**
 *
 * @author eroot
 */
public class DepenseController implements Initializable, ScreensChangeListener {

    Datastorage<Payer> payerStore;
    Datastorage<Depense> depenseStore;
    Datastorage<Transporter> transtore;
    Datastorage<Tiers> tiersStorage;
    Datastorage<Comptefin> cFinStore;
    Datastorage<Vehicule> vehistore;
    Tiers choosenTiers;
    Transporter choosenTrack;
    Vehicule choosenCar;
    private Comptefin compteFinSelected, compteDebitee;
    Depense choosenDepense;
    Succursale sucursale;
    String token;
    KetiAPI keti;
    @FXML
    ComboBox<Transporter> trackings;
    @FXML
    ComboBox<Depense> depenses;
    @FXML
    ComboBox<Vehicule> vehicule_cbx;
    @FXML
    ComboBox<Comptefin> dep_comptefin;
    @FXML
    TextField montant_depense;
    @FXML
    DatePicker dpkdvir;
    @FXML
    ComboBox<Comptefin> cb_vrmCredit;
    @FXML
    ComboBox<Comptefin> cb_vrmDebit;
    @FXML
    TextField montant_vire;
    @FXML
    TextField libelle_dep;
    @FXML
    ComboBox<Integer> rowPP;
    ObservableList<Transporter> transps;
    ObservableList<TransactionView> transviews;
    @FXML
    ComboBox<Tiers> clients_names;
    ObservableList<Tiers> tierss;
    @FXML
    ComboBox<Comptefin> comptes;
    ObservableList<Comptefin> accounts;
    ObservableList<Depense> depenseList;
    ObservableList<Vehicule> vehilist;
    @FXML
    CheckBox chkbx_depense_car;
    @FXML
    TextField libelle;
    @FXML
    TextField design_dep;
    @FXML
    Label count;
    @FXML
    TextField montantRecu;
    @FXML
    Label numeroPcs, identifiant;
    @FXML
    Label totalApayer;
    List<Payer> payers;
    @FXML
    Label aremborser;
    @FXML
    DatePicker dpk, dpkdep;
    @FXML
    TableColumn<TransactionView, String> datecol;
    @FXML
    TableColumn<TransactionView, String> libellecol;
    @FXML
    TableColumn<TransactionView, Number> debitcol;
    @FXML
    TableColumn<TransactionView, Number> creditcol;
    @FXML
    TableColumn<TransactionView, Number> soldecol;
    @FXML
    TableView<TransactionView> tbl_transaction;
    @FXML
    Pagination pagination;
    TransactionView trans;
    boolean isCarDep;
    int rowDataCount = 20;

    double somme = 0;

    private static DepenseController instance;

    public DepenseController() {
        instance = this;
    }

    public static DepenseController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dpk.setValue(LocalDate.now());
        setPattern(dpk);
        dpkdep.setValue(LocalDate.now());
        setPattern(dpkdep);
        dpkdvir.setValue(LocalDate.now());
        setPattern(dpkdvir);
        configTab();
        cnfRowPPComboBox();
        pagination.setPageFactory(this::createDataPage);
    }

    @FXML
    public void changeToNewDepense(ActionEvent evt) {
        depenses.setVisible(false);
        design_dep.setVisible(true);
        choosenDepense = null;
    }

    public void setPattern(DatePicker dtpk) {
        dtpk.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    public void setSuccursale(Succursale suc) {
        this.sucursale = suc;
    }

    public void setToken(Nitrite db, String token) {
        depenseStore = new Datastorage<>(db, Depense.class);
        cFinStore = new Datastorage<>(db, Comptefin.class);
        payerStore = new Datastorage<>(db, Payer.class);
        transtore = new Datastorage<>(db, Transporter.class);
        tiersStorage = new Datastorage<>(db, Tiers.class);
        vehistore = new Datastorage<>(db, Vehicule.class);
        this.token = token;
        keti = KetiHelper.createService(token);
        transps = FXCollections.observableArrayList();
        depenseList = FXCollections.observableArrayList();
        transviews = FXCollections.observableArrayList();
        transps.addAll(transtore.findAll());
        tierss = FXCollections.observableArrayList();
        vehilist = FXCollections.observableArrayList();
        tierss.addAll(tiersStorage.findAll());
        accounts = FXCollections.observableArrayList();
        accounts.addAll(cFinStore.findAll());
        vehilist.addAll(vehistore.findAll());
        payers = payerStore.findAll();
        depenseList.addAll(depenseStore.findAll());
        System.out.println("trans " + transps.size());
        trackings.itemsProperty().setValue(transps);
        clients_names.itemsProperty().setValue(tierss);
        comptes.itemsProperty().setValue(accounts);
        dep_comptefin.itemsProperty().setValue(accounts);
        cb_vrmCredit.itemsProperty().setValue(accounts);
        cb_vrmDebit.itemsProperty().setValue(accounts);
        depenses.itemsProperty().setValue(depenseList);
        vehicule_cbx.itemsProperty().setValue(vehilist);
        ComboBoxAutoCompletion<Transporter> comboBoxAutoCompletion = new ComboBoxAutoCompletion<>(trackings);
        ComboBoxAutoCompletion<Comptefin> comboBoxAutoCompletion1 = new ComboBoxAutoCompletion<>(comptes);
        ComboBoxAutoCompletion<Tiers> comboBoxAutoCompletion2 = new ComboBoxAutoCompletion<>(clients_names);
        new ComboBoxAutoCompletion<>(dep_comptefin);
        new ComboBoxAutoCompletion<>(depenses);
        new ComboBoxAutoCompletion<>(cb_vrmCredit);
        new ComboBoxAutoCompletion<>(cb_vrmDebit);
        new ComboBoxAutoCompletion<>(vehicule_cbx);
        configCombos();

    }

    public void refresh() {
        if (compteFinSelected != null) {
            fillTable(payerStore.findAll(), compteFinSelected);
        }
    }

    private Node createDataPage(int pgindex) {
        try {
            int offset = pgindex * rowDataCount;
            int limit = Math.min(offset + rowDataCount, transviews.size());
            tbl_transaction.setItems(FXCollections.observableArrayList(transviews.subList(offset, limit)));
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Page suivante non disponible");
        }
        return tbl_transaction;
    }

    private void cnfRowPPComboBox() {
        ObservableList<Integer> rows = FXCollections.observableArrayList(Arrays.asList(20, 25, 50, 100, 250, 500, 1000));
        rowPP.setItems(rows);
        rowPP.getSelectionModel().select(0);
    }

    private void configTrs() {
        clients_names.setConverter(new StringConverter<Tiers>() {
            @Override
            public String toString(Tiers object) {
                return object == null ? null : object.toString();
            }

            @Override
            public Tiers fromString(String string) {
                return clients_names.getItems()
                        .stream()
                        .filter(clt -> (clt.getPrenom() + " " + clt.getNom() + " " + clt.getAdresse())
                        .equalsIgnoreCase(string))
                        .findFirst()
                        .orElse(choosenTiers);
            }
        });
    }

    private void configCombos() {
        trackings.setConverter(new StringConverter<Transporter>() {
            @Override
            public String toString(Transporter object) {
                return object == null ? null : "#" + object.getTracking();
            }

            @Override
            public Transporter fromString(String string) {
                return trackings.getItems()
                        .stream()
                        .filter(t -> (String.valueOf(t.getTracking()))
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(choosenTrack);

            }
        });
        vehicule_cbx.setConverter(new StringConverter<Vehicule>() {
            @Override
            public String toString(Vehicule object) {
                return object == null ? null : object.getPlaque();
            }

            @Override
            public Vehicule fromString(String string) {
                return vehilist.stream()
                        .filter(v -> (String.valueOf(v.getPlaque()))
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(choosenCar);

            }
        });
        configTrs();
        comptes.setConverter(new StringConverter<Comptefin>() {
            @Override
            public String toString(Comptefin object) {
                return object == null ? null : object.getLibelle() + " " + object.getSucursaleId().getNomSuccursale();
            }

            @Override
            public Comptefin fromString(String string) {
                return comptes.getItems()
                        .stream()
                        .filter(c -> (c.getLibelle() + " " + c.getTypeDeCompte())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(compteFinSelected);
            }
        });
        dep_comptefin.setConverter(new StringConverter<Comptefin>() {
            @Override
            public String toString(Comptefin object) {
                return object == null ? null : object.getLibelle() + " " + object.getSucursaleId().getNomSuccursale();
            }

            @Override
            public Comptefin fromString(String string) {
                return dep_comptefin.getItems()
                        .stream()
                        .filter(c -> (c.getLibelle() + " " + c.getTypeDeCompte())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(compteFinSelected);
            }
        });
        cb_vrmCredit.setConverter(new StringConverter<Comptefin>() {
            @Override
            public String toString(Comptefin object) {
                return object == null ? null : object.getLibelle() + " " + object.getSucursaleId().getNomSuccursale();
            }

            @Override
            public Comptefin fromString(String string) {
                return cb_vrmCredit.getItems()
                        .stream()
                        .filter(c -> (c.getLibelle() + " " + c.getTypeDeCompte())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(compteFinSelected);
            }
        });
        cb_vrmDebit.setConverter(new StringConverter<Comptefin>() {
            @Override
            public String toString(Comptefin object) {
                return object == null ? null : object.getLibelle() + " " + object.getSucursaleId().getNomSuccursale();
            }

            @Override
            public Comptefin fromString(String string) {
                return cb_vrmDebit.getItems()
                        .stream()
                        .filter(c -> (c.getLibelle() + " " + c.getTypeDeCompte())
                        .equalsIgnoreCase(string))
                        .findFirst().orElse(compteDebitee);
            }
        });
        depenses.setConverter(new StringConverter<Depense>() {
            @Override
            public String toString(Depense object) {
                return object == null ? null : object.getLibelle();
            }

            @Override
            public Depense fromString(String string) {
                return depenses.getItems()
                        .stream()
                        .filter(d -> (d.getLibelle()).equalsIgnoreCase(string))
                        .findFirst().orElse(choosenDepense);
            }
        });
        chkbx_depense_car.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    isCarDep = true;
                    vehicule_cbx.setDisable(false);
                } else {
                    isCarDep = false;
                    vehicule_cbx.setDisable(true);
                }
            }
        });
        clients_names.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tiers> observable, Tiers oldValue, Tiers newValue) -> {
            choosenTiers = newValue;
        });
        vehicule_cbx.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Vehicule> observable, Vehicule oldValue, Vehicule newValue) -> {
            choosenCar = newValue;
        });
        depenses.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Depense>() {
            @Override
            public void changed(ObservableValue<? extends Depense> observable, Depense oldValue, Depense newValue) {
                choosenDepense = newValue;
                montant_depense.setText(String.valueOf(choosenDepense.getMontantFixe()));
            }
        });
        trackings.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Transporter>() {
            @Override
            public void changed(ObservableValue<? extends Transporter> observable, Transporter oldValue, Transporter newValue) {
                choosenTrack = newValue;
                if (choosenTrack == null) {
                    return;
                }
                clients_names.getSelectionModel().select(choosenTrack.getIdTiers());
                identifiant.setText("#" + choosenTrack.getTracking());
                libelle.setText("Paiement #" + Integer.toString(choosenTrack.getTracking()));
                List<Transporter> trs = transtore.findTracking(choosenTrack.getTracking());
                System.out.println("taill " + trs.size());
                somme = 0;
                for (Transporter t : trs) {
                    somme += t.getPriceToPay();
                    System.out.println(" somme " + somme);
                }
                totalApayer.setText("Total à payer $ " + somme);
            }
        });
        comptes.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Comptefin> observable, Comptefin oldValue, Comptefin newValue) -> {
            compteFinSelected = newValue;
            List<Payer> pays = payerStore.findAll();
            fillTable(pays, compteFinSelected);
        });
        cb_vrmCredit.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Comptefin> observable, Comptefin oldValue, Comptefin newValue) -> {
            compteFinSelected = newValue;
            List<Payer> pays = payerStore.findAll();
            fillTable(pays, compteFinSelected);
        });
        cb_vrmDebit.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Comptefin> observable, Comptefin oldValue, Comptefin newValue) -> {
            compteDebitee = newValue;
        });
        dep_comptefin.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Comptefin> observable, Comptefin oldValue, Comptefin newValue) -> {
            compteFinSelected = newValue;
            List<Payer> pays = payerStore.findAll();
            fillTable(pays, compteFinSelected);
        });
    }

    @FXML
    public void saveTransfert(ActionEvent evt) {
        if (compteFinSelected == null && compteDebitee == null && montant_vire.getText().isEmpty()) {
            return;
        }
        Succursale sucCompte = compteFinSelected.getSucursaleId();
        if(!sucCompte.equals(sucursale)){
            MainUI.notify(null, "Erreur", "Impossible de choisir un compte d'un autre succursale", 4, "error");
           return; 
        }
        Payer p = new Payer(DataId.generate());
        p.setCompteIdCredit(compteFinSelected);
        p.setCompteIdDebit(compteDebitee);
        p.setDatePayement(Constants.toUtilDate(dpkdvir.getValue()));
        p.setLibelle("Virement interne du " + Constants.DateFormateur.format(p.getDatePayement()));
        p.setMontantPaye(Double.parseDouble(montant_vire.getText()));
        p.setReference(0);
        Payer pvir = payerStore.insert(p);
        if (pvir != null) {
            MainUI.notify(null, "Succès", "Transfert enregistrée avec succès", 4, "info");
        }
    }

    @FXML
    public void calculateSum(KeyEvent evt) {
        montantRecu.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (montantRecu.getText().isEmpty()) {
                    totalApayer.setText("A Payer $" + somme);
                    aremborser.setText("A rembourser : $0");
                    return;
                }
                double in = Double.parseDouble(montantRecu.getText());
                double rst = somme - in;
                double dv = BigDecimal.valueOf(rst).setScale(3, RoundingMode.HALF_UP).doubleValue();
                totalApayer.setText("A Payer : $" + ((dv > 0) ? dv : 0));
                aremborser.setText("A rembourser : $" + Math.abs((dv < 0) ? dv : 0));
            }
        });

    }

    @Override
    public void onScreenChange(UIController cont) {

    }

    @FXML
    private void save(ActionEvent evt) {
        if (montantRecu.getText().isEmpty()) {
            return;
        }
        double montantIn = Double.parseDouble(montantRecu.getText());
        double pay = montantIn;
        List<Transporter> trs = transtore.findTracking(choosenTrack.getTracking());
        if (montantIn >= somme) {
            //solde tout
            for (Transporter t : trs) {
                double d = t.getPriceToPay();
                t.setPaidPrice(d);
                transtore.update("uid", t.getUid(), t);
            }
        } else {
            for (Transporter t : trs) {
                double d = t.getPriceToPay();
                if (montantIn >= d) {
                    t.setPaidPrice(d);
                    transtore.update("uid", t.getUid(), t);
                    montantIn = montantIn - d;
                } else {
                    t.setPaidPrice(montantIn);
                    transtore.update("uid", t.getUid(), t);
                    montantIn = 0;
                }
            }
        }

        Succursale sucCompte = compteFinSelected.getSucursaleId();
        if (!sucCompte.equals(sucursale)) {
            MainUI.notify(null, "Erreur", "Impossible de choisir un compte d'un autre succursale", 4, "error");
            return;
        }
        Payer p = new Payer(DataId.generate());
        p.setClientId(choosenTiers);
        p.setCompteIdDebit(compteFinSelected);
        p.setDatePayement(Date.from(dpk.getValue().atStartOfDay().toInstant(ZoneOffset.ofHours(2))));
        p.setLibelle(libelle.getText());
        p.setMontantPaye((pay - somme) >= 0 ? somme : pay);
        p.setReference(choosenTrack.getTracking());
        Payer pp = payerStore.insert(p);
        TransactionView tv = new TransactionView();
        tv.setCredit(0);
        tv.setDate(pp.getDatePayement());
        tv.setDebit(pp.getMontantPaye());
        tv.setLibelle(pp.getLibelle());
        tv.setPayerUid(pp.getUid());
        double solde = figureOutSolde(compteFinSelected);
        tv.setSolde(solde);
        if (pp != null) {
            MainUI.notify(null, "Succès", "Paiement enregistré avec succès", 4, "info");
            System.out.println("Payer inserted " + pp.getLibelle());
        }

    }

    private void configTab() {
        datecol.setCellValueFactory((TableColumn.CellDataFeatures<TransactionView, String> param) -> new SimpleStringProperty(Constants.DateFormateur.format(param.getValue().getDate())));
        libellecol.setCellValueFactory((TableColumn.CellDataFeatures<TransactionView, String> param) -> new SimpleStringProperty(param.getValue().getLibelle()));
        debitcol.setCellValueFactory((TableColumn.CellDataFeatures<TransactionView, Number> param) -> new SimpleDoubleProperty(param.getValue().getDebit()));
        creditcol.setCellValueFactory((TableColumn.CellDataFeatures<TransactionView, Number> param) -> new SimpleDoubleProperty(param.getValue().getCredit()));
        soldecol.setCellValueFactory((TableColumn.CellDataFeatures<TransactionView, Number> param) -> new SimpleDoubleProperty(param.getValue().getSolde()));
    }

    private double figureOutSolde(Comptefin comptefin) {
        List<Payer> credit = payerStore.findPayerCrediteur(comptefin.getUid());
        List<Payer> debit = payerStore.findPayerDebiteur(comptefin.getUid());
        double d = 0, c = 0;
        d = debit.stream().map((p) -> p.getMontantPaye()).reduce(d, (accumulator, _item) -> accumulator + _item);
        c = credit.stream().map((p) -> p.getMontantPaye()).reduce(c, (accumulator, _item) -> accumulator + _item);
        return (d - c);
    }

    private void fillTable(List<Payer> lp, Comptefin cfin) {
        System.err.println("List pay " + lp.size());

        double sc = 0, sd = 0;
        tbl_transaction.getItems().clear();
        for (Payer p : lp) {
            TransactionView t = new TransactionView();
            Comptefin c = p.getCompteIdCredit();
            Comptefin d = p.getCompteIdDebit();
            if (cfin.equals(c)) {
                t.setCredit(p.getMontantPaye());
                sc += p.getMontantPaye();
            } else if (cfin.equals(d)) {
                t.setDebit(p.getMontantPaye());
                sd += p.getMontantPaye();
            }
            if (cfin.equals(c) || cfin.equals(d)) {
                t.setDate(p.getDatePayement());
                t.setSolde((sd - sc));
                t.setLibelle(p.getLibelle());
                t.setPayerUid(p.getUid());
                transviews.add(t);
            }
            //trans 
        }

        Collections.reverse(transviews);
        tbl_transaction.setItems(transviews);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(transviews.size() + " éléments");
            }
        });

    }

    @FXML
    private void selectRowPerPage(ActionEvent evt) {
        ComboBox cbx = (ComboBox) evt.getSource();
        rowDataCount = (int) cbx.getSelectionModel().getSelectedItem();
        pagination.setPageFactory(this::createDataPage);
        if (compteFinSelected != null) {
            fillTable(payerStore.findAll(), compteFinSelected);
        }
    }

    @FXML
    private void saveDepense(ActionEvent evt) {
        String lbl = isCarDep ? libelle_dep.getText() + " pour " + choosenCar.getPlaque() : libelle_dep.getText();
        if (choosenDepense == null) {
            if (montant_depense.getText().isEmpty() && libelle_dep.getText().isEmpty() && design_dep.getText().isEmpty()) {
                return;
            }
            List<Depense> deplike = depenseStore.findAllEquals("libelle", design_dep.getText());
            Depense dep;
            if (deplike == null || deplike.isEmpty()) {
                dep = new Depense(DataId.generate());
                dep.setLibelle(design_dep.getText());
                dep.setMontantFixe(Double.valueOf(montant_depense.getText()));
            } else {
                dep = deplike.get(0);
            }
            Succursale sucCompte = compteFinSelected.getSucursaleId();
            if (!sucCompte.equals(sucursale)) {
                MainUI.notify(null, "Erreur", "Impossible de choisir un compte d'un autre succursale", 4, "error");
                return;
            }
            depenseStore.insert(dep);
            Payer p = new Payer(DataId.generate());
            p.setCompteIdCredit(compteFinSelected);
            p.setDatePayement(Constants.toUtilDate(dpkdep.getValue()));
            p.setDepenseId(dep);
            p.setLibelle(lbl);
            p.setMontantPaye(dep.getMontantFixe());
            p.setReference(0);
            payerStore.insert(p);
        } else {
            Succursale sucCompte = compteFinSelected.getSucursaleId();
            if (!sucCompte.equals(sucursale)) {
                MainUI.notify(null, "Erreur", "Impossible de choisir un compte d'un autre succursale", 4, "error");
                return;
            }
            choosenDepense.setMontantFixe(Double.parseDouble(montant_depense.getText()));
            Depense dep = depenseStore.update("uid", choosenDepense.getUid(), choosenDepense);
            Payer p = new Payer(DataId.generate());
            p.setCompteIdCredit(compteFinSelected);
            p.setDatePayement(Constants.toUtilDate(dpkdep.getValue()));
            p.setDepenseId(dep);
            p.setLibelle(lbl);
            p.setMontantPaye(dep.getMontantFixe());
            p.setReference(0);
            payerStore.insert(p);
        }
        MainUI.notify(null, "Succès", "Depense enregistrée avec succès", 4, "info");
        depenses.setVisible(true);
        design_dep.setVisible(false);
        choosenDepense = null;

    }

    @FXML
    private void refresh(Event evt) {
        refresh();
    }

    public void search(String query) {
        List<TransactionView> trv = new ArrayList<>();
        System.err.println("Transview " + transviews.size());
        for (TransactionView tv : transviews) {
            if ((tv.getLibelle() + " " + Constants.DateFormateur.format(tv.getDate())).toUpperCase()
                    .contains(query.toUpperCase())) {
                trv.add(tv);
            }
        }
        tbl_transaction.getItems().clear();
        tbl_transaction.getItems().addAll(trv);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                count.setText(trv.size() + " Elements");
            }
        });
    }

    @FXML
    private void delete(Event evt) {
        if (trans == null) {
            return;
        }
        payerStore.delete("uid", trans.getPayerUid());
        System.out.println("Pauid " + trans.getPayerUid());
        tbl_transaction.getItems().remove(trans);
        try {
            Response<Void> del = keti.deletePayer(trans.getPayerUid()).execute();
            System.out.println("Delete payer " + del.message());
            if (del.isSuccessful()) {
                MainUI.notify(null, "Succès", "Depense supprimée avec succès", 4, "info");
            }
        } catch (IOException ex) {
            Logger.getLogger(DepenseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void pickTrans(Event evt) {
        trans = tbl_transaction.getSelectionModel().getSelectedItem();
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

    public void setCompteFinSelected(Comptefin compteFinSelected) {
        this.compteFinSelected = compteFinSelected;
    }

}
