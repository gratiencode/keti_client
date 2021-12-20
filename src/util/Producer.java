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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author eroot
 */
public class Producer<T> implements Runnable {

    BlockingQueue<T> siniaup;
    BlockingQueue<T> siniadown;
    KetiAPI keti;
    Datastorage<T> database;
    Class<T> cls;

    public Producer(BlockingQueue<T> siniaup, BlockingQueue<T> sinidown, KetiAPI keti, Nitrite db, Class<T> cls) {
        this.siniaup = siniaup;
        this.siniadown = sinidown;
        this.keti = keti;
        this.database = new Datastorage(db, cls);
        this.cls = cls;
    }

    @Override
    public void run() {
        if (cls.isAssignableFrom(Tiers.class)) {
            try {
                List<Tiers> tiersNet = keti.getTiers().execute().body();
                List<T> tiersLoc = database.findAll();
                for (T tl : tiersLoc) {
                    if (!tiersNet.contains(tl)) {
                        siniaup.put(tl);
                    }
                }
                for (Tiers t : tiersNet) {
                    if (!tiersLoc.contains(t)) {
                        siniadown.put((T) t);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (cls.isAssignableFrom(Vehicule.class)) {
            try {
                List<Vehicule> vehNet = keti.getVehicules().execute().body();
                List<T> vehLoc = database.findAll();
                for (T tl : vehLoc) {
                    if (!vehNet.contains(tl)) {
                        siniaup.put(tl);
                    }
                }
                for (Vehicule t : vehNet) {
                    if (!vehLoc.contains(t)) {
                        siniadown.put((T) t);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (cls.isAssignableFrom(Voyage.class)) {

        } else if (cls.isAssignableFrom(Succursale.class)) {

            keti.getSucursales().enqueue(new Callback<List<Succursale>>() {
                @Override
                public void onResponse(Call<List<Succursale>> call, Response<List<Succursale>> rspns) {
                    if (rspns.isSuccessful()) {
                        List<Succursale> vehNet = rspns.body();
                        List<T> vehLoc = database.findAll();
                        for (T tl : vehLoc) {
                            if (!vehNet.contains(tl)) {
                                try {
                                    siniaup.put(tl);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        for (Succursale t : vehNet) {
                            if (!vehLoc.contains(t)) {
                                try {
                                    siniadown.put((T) t);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Succursale>> call, Throwable thrwbl) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });

        } else if (cls.isAssignableFrom(Transporter.class)) {

        } else if (cls.isAssignableFrom(Payer.class)) {

        } else if (cls.isAssignableFrom(Contacts.class)) {

        } else if (cls.isAssignableFrom(User.class)) {

        } else if (cls.isAssignableFrom(Comptefin.class)) {

        } else if (cls.isAssignableFrom(Marchandise.class)) {

            keti.getMarchandises().enqueue(new Callback<List<Marchandise>>() {
                @Override
                public void onResponse(Call<List<Marchandise>> call, Response<List<Marchandise>> rspns) {
                    if (rspns.isSuccessful()) {
                        List<Marchandise> vehNet = rspns.body();
                        List<T> vehLoc = database.findAll();
                        for (T tl : vehLoc) {
                            if (!vehNet.contains(tl)) {
                                try {
                                    siniaup.put(tl);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        for (Marchandise t : vehNet) {
                            if (!vehLoc.contains(t)) {
                                try {
                                    siniadown.put((T) t);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Marchandise>> call, Throwable thrwbl) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });

        } else if (cls.isAssignableFrom(Retirer.class)) {

        } else if (cls.isAssignableFrom(Creancier.class)) {

        } else if (cls.isAssignableFrom(Depense.class)) {

        }

    }

}
