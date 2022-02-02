/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;


import java.util.Date;
import java.util.Objects;
import model.Succursale;
import model.Vehicule;

/**
 *
 * @author eroot
 */
public class Performance {
    private Date date;
    private Vehicule vehicule;
    private Succursale succursale;
    private double recette;
    private double depense;
    private double reductionPerte;
    private double reductionFacture;
    private double resultat;

    public Performance() {
    }

    public Performance(Date date, double recette, double depense, double resultat) {
        this.date = date;
        this.recette = recette;
        this.depense = depense;
        this.resultat = resultat;
    }

    public Performance(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    
    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getRecette() {
        return recette;
    }

    public void setRecette(double recette) {
        this.recette = recette;
    }

    public double getDepense() {
        return depense;
    }

    public void setDepense(double depense) {
        this.depense = depense;
    }

    public double getResultat() {
        return resultat;
    }

    public void setResultat(double resultat) {
        this.resultat = resultat;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.date);
        hash = 97 * hash + Objects.hashCode(this.vehicule);
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
        final Performance other = (Performance) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.vehicule, other.vehicule)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Performance{" + "date=" + date + ", vehicule=" + vehicule + ", recette=" + recette + ", depense=" + depense + ", resultat=" + resultat + '}';
    }

    public double getReductionPerte() {
        return reductionPerte;
    }

    public void setReductionPerte(double reductionPerte) {
        this.reductionPerte = reductionPerte;
    }

    public double getReductionFacture() {
        return reductionFacture;
    }

    public void setReductionFacture(double reductionFacture) {
        this.reductionFacture = reductionFacture;
    }

    public Succursale getSuccursale() {
        return succursale;
    }

    public void setSuccursale(Succursale succursale) {
        this.succursale = succursale;
    }

   
    
    
    
}
