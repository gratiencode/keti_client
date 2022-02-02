/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Date;
import java.util.Objects;
import model.Payer;

/**
 *
 * @author eroot
 */
public class TransactionView {
    private Date date;
    private String libelle;
    private String payerUid;
    private double debit;
    private double credit;
    private double solde;

    public TransactionView() {
    }

    public TransactionView(Date date, String libelle, String payerUid, double debit, double credit, double solde) {
        this.date = date;
        this.libelle = libelle;
        this.payerUid = payerUid;
        this.debit = debit;
        this.credit = credit;
        this.solde = solde;
    }
    

   

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getPayerUid() {
        return payerUid;
    }

    public void setPayerUid(String payerUid) {
        this.payerUid = payerUid;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.payerUid);
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
        final TransactionView other = (TransactionView) obj;
        if (!Objects.equals(this.payerUid, other.payerUid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TransactionView{" + "date=" + date + ", libelle=" + libelle + ", payerUid=" + payerUid + ", debit=" + debit + ", credit=" + credit + ", solde=" + solde + '}';
    }
    
    
    
}
