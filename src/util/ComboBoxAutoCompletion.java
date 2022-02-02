/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import model.Comptefin;
import model.Depense;
import model.Tiers;
import model.Transporter;
import model.Vehicule;

/**
 *
 * @author eroot
 */
public class ComboBoxAutoCompletion<T> implements EventHandler {

    private ComboBox<T> comboBox;
    final private ObservableList<T> data;
    private Integer sid;

    public ComboBoxAutoCompletion(final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        data = this.comboBox.getItems();
        performAutoCompletion();
    }

    public ComboBoxAutoCompletion(ComboBox<T> comboBox, Integer sid) {
        this.comboBox = comboBox;
        data = this.comboBox.getItems();
        this.sid = sid;
    }

    @Override
    public void handle(Event evt) {
        KeyEvent event = (KeyEvent) evt;
        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN
                || event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }
        if (event.getCode() == KeyCode.BACK_SPACE) {
            String str = this.comboBox.getEditor().getText();
            if (str != null && str.length() > 0) {
                str = str.substring(0, str.length() - 1);
            }
            if (str != null) {
                this.comboBox.getEditor().setText(str);
                moveCaret(str.length());
            }
            this.comboBox.getSelectionModel().clearSelection();
        }

        if (event.getCode() == KeyCode.ENTER && comboBox.getSelectionModel().getSelectedIndex() > -1) {
            return;
        }
        setItems();

    }

    private void setItems() {
        ObservableList<T> list = FXCollections.observableArrayList();
        for (T datum : this.data) {
            String s = this.comboBox.getEditor().getText().toUpperCase();
            if (datum instanceof Tiers) {
                Tiers tiers = (Tiers) datum;
                if (tiers.toString().toUpperCase().contains(s.toUpperCase())) {
                    list.add(datum);
                }
            } else if (datum instanceof Vehicule) {
                Vehicule vehicule = (Vehicule) datum;
                if (vehicule.toString().toUpperCase().contains(s.toUpperCase())) {
                    list.add(datum);
                }
            }else if (datum instanceof Transporter) {
                Transporter transporter = (Transporter) datum;
                if (String.valueOf(transporter.getTracking()).toUpperCase().contains(s.toUpperCase())) {
                    list.add(datum);
                }
            }else if (datum instanceof Comptefin) {
                Comptefin comptefin = (Comptefin) datum;
                if ((comptefin.getLibelle()+" "+comptefin.getSucursaleId().getNomSuccursale()).toUpperCase().contains(s.toUpperCase())) {
                    list.add(datum);
                }
            }else if (datum instanceof Depense) {
                Depense depense = (Depense) datum;
                if (depense.getLibelle().toUpperCase().contains(s.toUpperCase())) {
                    list.add(datum);
                }
            }

        }

        if (list.isEmpty()) {
            this.comboBox.hide();
        }
        this.comboBox.setItems(list);
        this.comboBox.show();
    }

    private void performAutoCompletion() {
        this.comboBox.setEditable(true);
        this.comboBox.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {//mean onfocus
                this.comboBox.show();
            }
        });

        this.comboBox.getEditor().setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    return;
                }
            }
            this.comboBox.show();
        });

        this.comboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            moveCaret(this.comboBox.getEditor().getText().length());
        });

        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(ComboBoxAutoCompletion.this);

        if (this.sid != null) {
            this.comboBox.getSelectionModel().select(this.sid);
        }

    }

    private void moveCaret(int textLength) {
        this.comboBox.getEditor().positionCaret(textLength);
    }

}
