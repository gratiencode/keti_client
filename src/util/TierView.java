/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Objects;
import model.Contacts;
import model.Tiers;

/**
 *
 * @author eroot
 */
public class TierView {
    private Tiers tiers;
    private Contacts contacts;

    public TierView(Tiers tiers, Contacts contacts) {
        this.tiers = tiers;
        this.contacts = contacts;
    }

    public TierView() {
    }

    public Tiers getTiers() {
        return tiers;
    }

    public void setTiers(Tiers tiers) {
        this.tiers = tiers;
    }

    public Contacts getContacts() {
        return contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.tiers);
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
        final TierView other = (TierView) obj;
        if (!Objects.equals(this.tiers, other.tiers)) {
            return false;
        }
        return true;
    }
    
    
}
