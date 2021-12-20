/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import core.KetiAPI;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Comptefin;
import model.Contacts;
import model.Creancier;
import model.Depense;
import model.Marchandise;
import model.Payer;
import model.Retirer;
import model.Succursale;
import model.Tiers;
import model.Transporter;
import model.User;
import model.Vehicule;
import model.Voyage;
import org.dizitart.no2.Nitrite;

/**
 *
 * @author eroot
 */
public class Consumer<T> implements Runnable{
    
    
    BlockingQueue<T> siniaup;
    BlockingQueue<T> siniadown;
    KetiAPI keti;
    Datastorage<T> database;
    Class<T> cls;

    public Consumer(BlockingQueue<T> siniaup, BlockingQueue<T> siniadown, KetiAPI keti, Nitrite database, Class<T> cls) {
        this.siniaup = siniaup;
        this.siniadown = siniadown;
        this.keti = keti;
        this.database = new Datastorage<>(database,cls);
        this.cls = cls;
    }

    

    @Override
    public void run() {
         if (cls.isAssignableFrom(Tiers.class)) {
           while(true){
               try {
                   T t1=siniaup.take();//for upload
                   Tiers tn=(Tiers)t1;
                   Tiers tnr=keti.createTier(tn).execute().body();
                   T t2=siniadown.take();
                   T tlr=database.insert(t2);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               } catch (IOException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        } else if (cls.isAssignableFrom(Vehicule.class)) {
            while(true){
               try {
                   T t1=siniaup.take();//for upload
                   Vehicule tn=(Vehicule)t1;
                   Vehicule tnr=keti.createVehicule(tn).execute().body();
                   T t2=siniadown.take();
                   T tlr=database.insert(t2);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               } catch (IOException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        } else if (cls.isAssignableFrom(Voyage.class)) {

        } else if (cls.isAssignableFrom(Succursale.class)) {
            while(true){
               try {
                   T t1=siniaup.take();//for upload
                   Succursale tn=(Succursale)t1;
                   Succursale tnr=keti.createSuccursale(tn).execute().body();
                   T t2=siniadown.take();
                   T tlr=database.insert(t2);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               } catch (IOException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        } else if (cls.isAssignableFrom(Transporter.class)) {

        } else if (cls.isAssignableFrom(Payer.class)) {

        } else if (cls.isAssignableFrom(Contacts.class)) {

        } else if (cls.isAssignableFrom(User.class)) {

        } else if (cls.isAssignableFrom(Comptefin.class)) {

        } else if (cls.isAssignableFrom(Marchandise.class)) {
           while(true){
               try {
                   T t1=siniaup.take();//for upload
                   Marchandise tn=(Marchandise)t1;
                   Marchandise tnr=keti.createGoods(tn).execute().body();
                   T t2=siniadown.take();
                   T tlr=database.insert(t2);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               } catch (IOException ex) {
                   Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
        } else if (cls.isAssignableFrom(Retirer.class)) {

        } else if (cls.isAssignableFrom(Creancier.class)) {

        } else if (cls.isAssignableFrom(Depense.class)) {

        }
    }
    
}
