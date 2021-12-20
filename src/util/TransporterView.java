/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Objects;
import model.Marchandise;
import model.Transporter;
import model.Voyage;

/**
 *
 * @author eroot
 */
public class TransporterView {
    private Marchandise marchandise;
    private Transporter transporter;
    private Voyage voyage;

    public TransporterView() {
    }

    public TransporterView(Transporter transporter, Voyage voyage) {
        this.transporter = transporter;
        this.voyage = voyage;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }

    public Transporter getTransporter() {
        return transporter;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public Marchandise getMarchandise() {
        return marchandise;
    }

    public void setMarchandise(Marchandise marchandise) {
        this.marchandise = marchandise;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.transporter);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TransporterView other = (TransporterView) obj;
        if (!Objects.equals(this.transporter, other.transporter)) {
            return false;
        }
        return true;
    }

    
}
