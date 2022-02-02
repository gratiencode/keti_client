/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author eroot
 */
public class RetraitView {
    private String uid;
    private Date dateRetrait;
    private String client;
    private String marchandise;
    private int tracking;
    private double quantiteLoaded;
    private double valeurLoaded;
    private double quantiteRetire;
    private double quantiteRestant;
    private double valeurRetire;
    private double valeurRestant;

    public RetraitView() {
    }

    public RetraitView(String uid, Date dateRetrait, String client, String marchandise, int tracking, double quantiteLoaded, double valeurLoaded, double quantiteRetire, double quantiteRestant, double valeurRetire, double valeurRestant) {
        this.uid = uid;
        this.dateRetrait = dateRetrait;
        this.client = client;
        this.marchandise = marchandise;
        this.tracking = tracking;
        this.quantiteLoaded = quantiteLoaded;
        this.valeurLoaded = valeurLoaded;
        this.quantiteRetire = quantiteRetire;
        this.quantiteRestant = quantiteRestant;
        this.valeurRetire = valeurRetire;
        this.valeurRestant = valeurRestant;
    }

    public RetraitView(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getDateRetrait() {
        return dateRetrait;
    }

    public void setDateRetrait(Date dateRetrait) {
        this.dateRetrait = dateRetrait;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getMarchandise() {
        return marchandise;
    }

    public void setMarchandise(String marchandise) {
        this.marchandise = marchandise;
    }

    public int getTracking() {
        return tracking;
    }

    public void setTracking(int tracking) {
        this.tracking = tracking;
    }

    public double getQuantiteLoaded() {
        return quantiteLoaded;
    }

    public void setQuantiteLoaded(double quantiteLoaded) {
        this.quantiteLoaded = quantiteLoaded;
    }

    public double getValeurLoaded() {
        return valeurLoaded;
    }

    public void setValeurLoaded(double valeurLoaded) {
        this.valeurLoaded = valeurLoaded;
    }

    public double getQuantiteRetire() {
        return quantiteRetire;
    }

    public void setQuantiteRetire(double quantiteRetire) {
        this.quantiteRetire = quantiteRetire;
    }

    public double getQuantiteRestant() {
        return quantiteRestant;
    }

    public void setQuantiteRestant(double quantiteRestant) {
        this.quantiteRestant = quantiteRestant;
    }

    public double getValeurRetire() {
        return valeurRetire;
    }

    public void setValeurRetire(double valeurRetire) {
        this.valeurRetire = valeurRetire;
    }

    public double getValeurRestant() {
        return valeurRestant;
    }

    public void setValeurRestant(double valeurRestant) {
        this.valeurRestant = valeurRestant;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.uid);
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
        final RetraitView other = (RetraitView) obj;
        if (!Objects.equals(this.uid, other.uid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RetraitView{" + "uid=" + uid + ", dateRetrait=" + dateRetrait + ", client=" + client + ", marchandise=" + marchandise + ", tracking=" + tracking + ", quantiteLoaded=" + quantiteLoaded + ", valeurLoaded=" + valeurLoaded + ", quantiteRetire=" + quantiteRetire + ", quantiteRestant=" + quantiteRestant + ", valeurRetire=" + valeurRetire + ", valeurRestant=" + valeurRestant + '}';
    }
    
    
}
