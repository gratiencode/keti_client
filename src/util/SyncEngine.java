/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import core.KetiAPI;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Comptefin;
import model.Contacts;
import model.Marchandise;
import model.Tiers;
import model.Transporter;
import model.Vehicule;
import model.Voyage;
import okhttp3.ResponseBody;
import org.dizitart.no2.Nitrite;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * @author eroot
 */
public class SyncEngine {

    private KetiAPI keti;
    Nitrite localdb;
    //Preferences pref=Preferences.userNodeForPackage(SyncEngine.class);

    public SyncEngine(KetiAPI keti, Nitrite localdb) {
        this.keti = keti;
        this.localdb = localdb;
    }

    public void syncDown() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture result = ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Types t : Types.values()) {
                    switch (t) {
                        case Comptefin: {
                            //sync up/down
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                           
                            break;
                        }
                        case Contacts: {
                            Datastorage<Contacts> sucdb = new Datastorage<>(localdb, Contacts.class);
                            keti.getContacts().enqueue(new Callback<List<Contacts>>() {
                                @Override
                                public void onResponse(Call<List<Contacts>> call, Response<List<Contacts>> rspns) {
                                    
                                    if (rspns.isSuccessful()) {
                                        List<Contacts> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            sucdb.insertIfNotExist(s, s.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Contacts>> call, Throwable thrwbl) {
                                
                                }
                            });
                            break;
                        }
                        case Creancier: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Depense: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Marchandise: {
                            Datastorage<Marchandise> sucdb = new Datastorage<>(localdb, Marchandise.class);
                            keti.getMarchandises().enqueue(new Callback<List<Marchandise>>() {
                                @Override
                                public void onResponse(Call<List<Marchandise>> call, Response<List<Marchandise>> rspns) {
                                    
                                    if (rspns.isSuccessful()) {
                                        List<Marchandise> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            sucdb.insertIfNotExist(s, s.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Marchandise>> call, Throwable thrwbl) {
                                   
                                }
                            });
                            break;
                        }
                        case Payer: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Retirer: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Tiers: {
                             
                            Datastorage<Tiers> sucdb = new Datastorage<>(localdb, Tiers.class);
                            Datastorage<Contacts> ctkt = new Datastorage<>(localdb, Contacts.class);
                            keti.getTiers().enqueue(new Callback<List<Tiers>>() {
                                @Override
                                public void onResponse(Call<List<Tiers>> call, Response<List<Tiers>> rspns) {
                                    
                                    if (rspns.isSuccessful()) {
                                        List<Tiers> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            try {
                                                sucdb.insertIfNotExist(s, s.getUid());
                                                List<Contacts> body = keti.findContacts(s.getUid()).execute().body();
                                               
                                                body.forEach((c)->{
                                                    ctkt.insertIfNotExist(c, c.getUid());
                                                });
                                            } catch (IOException ex) {
                                                Logger.getLogger(SyncEngine.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Tiers>> call, Throwable thrwbl) {
                                    System.err.println("Erruer in syncdown " + thrwbl.getMessage());
                                }
                            });
                            break;
                        }
                        case Transporter: {
                            Datastorage<Transporter> tdb = new Datastorage<>(localdb, Transporter.class);
                            keti.getTransports().enqueue(new Callback<List<Transporter>>() {
                                @Override
                                public void onResponse(Call<List<Transporter>> call, Response<List<Transporter>> rspns) {
                                    if(rspns.isSuccessful()){
                                        List<Transporter> ltr=rspns.body();
                                        ltr.forEach(t->{
                                           tdb.insertIfNotExist(t, t.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Transporter>> call, Throwable thrwbl) {
                                    System.err.println("Erruer in sync trans down " + thrwbl.getMessage());
                                }
                            });
                            break;
                        }
                        case User: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Vehicule: {
                            Datastorage<Vehicule> sucdb = new Datastorage<>(localdb, Vehicule.class);
                            keti.getVehicules().enqueue(new Callback<List<Vehicule>>() {
                                @Override
                                public void onResponse(Call<List<Vehicule>> call, Response<List<Vehicule>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        List<Vehicule> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            sucdb.insertIfNotExist(s, s.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Vehicule>> call, Throwable thrwbl) {
                                  
                                }
                            });
                            break;
                        }
                        case Voyage: {
                            Datastorage<Voyage> vdb = new Datastorage<>(localdb, Voyage.class);
                            keti.getVoyages().enqueue(new Callback<List<Voyage>>() {
                                @Override
                                public void onResponse(Call<List<Voyage>> call, Response<List<Voyage>> rspns) {
                                    if(rspns.isSuccessful()){
                                        List<Voyage> ltr=rspns.body();
                                        ltr.forEach(t->{
                                           vdb.insertIfNotExist(t, t.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Voyage>> call, Throwable thrwbl) {
                                    System.err.println("Erruer in sync voy down " + thrwbl.getMessage());
                                }
                            });
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }, 1, 10, TimeUnit.SECONDS);
    }
    public void syncUp(){
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture result = ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Types t : Types.values()) {
                    switch (t) {
                        case Comptefin: {
                            //sync up/down
                           
                            break;
                        }
                        case Contacts: {
                            Datastorage<Contacts> sucdb = new Datastorage<>(localdb, Contacts.class);
                            List<Contacts> ctkt=sucdb.findAll();
                            keti.syncContact(ctkt).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                   if(rspns.isSuccessful()){
                                       System.out.println("Contact synced successfuly");
                                   }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable thrwbl) {
                                    
                                }
                            });
                            break;
                        }
                        case Creancier: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Depense: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Marchandise: {
                            Datastorage<Marchandise> sucdb = new Datastorage<>(localdb, Marchandise.class);
                            List<Marchandise> lm=sucdb.findAll();
                            keti.syncGoods(lm).enqueue(new Callback<List<Marchandise>>() {
                                @Override
                                public void onResponse(Call<List<Marchandise>> call, Response<List<Marchandise>> rspns) {
                                   if(rspns.isSuccessful()){
                                       System.out.println("Marchandise synced successfully"); 
                                   }
                                }

                                @Override
                                public void onFailure(Call<List<Marchandise>> call, Throwable thrwbl) {
                                   
                                }
                            });
                            break;
                        }
                        case Payer: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Retirer: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Tiers: {
                            Datastorage<Tiers> sucdb = new Datastorage<>(localdb, Tiers.class);
                            List<Tiers> ltr=sucdb.findAll();
                            keti.syncTiers(ltr).enqueue(new Callback<List<Tiers>>() {
                                @Override
                                public void onResponse(Call<List<Tiers>> call, Response<List<Tiers>> rspns) {
                                    System.out.println("Up sync tier rspns "+rspns.message());
                                   if(rspns.isSuccessful()){
                                       System.out.println("Tiers synced successfuly");
                                   }
                                }

                                @Override
                                public void onFailure(Call<List<Tiers>> call, Throwable thrwbl) {
                                 
                                }
                            });
                            break;
                        }
                        case Transporter: {
                            Datastorage<Transporter> tdb = new Datastorage<>(localdb, Transporter.class);
                            keti.syncTransport(tdb.findAll()).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                    System.out.println("Up syns trans "+rspns.message());
                                    if(rspns.isSuccessful()){
                                        System.out.println("transporter up synced");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable thrwbl) {
                                    
                                }
                            });
                            break;
                        }
                        case User: {
                            BlockingQueue<Comptefin> sharedUp = new ArrayBlockingQueue<>(50);
                            BlockingQueue<Comptefin> sharedDown = new ArrayBlockingQueue<>(50);
                            break;
                        }
                        case Vehicule: {
                            Datastorage<Vehicule> sucdb = new Datastorage<>(localdb, Vehicule.class);
                            keti.getVehicules().enqueue(new Callback<List<Vehicule>>() {
                                @Override
                                public void onResponse(Call<List<Vehicule>> call, Response<List<Vehicule>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        List<Vehicule> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            sucdb.insertIfNotExist(s, s.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Vehicule>> call, Throwable thrwbl) {
                                
                                }
                            });
                            break;
                        }
                        case Voyage: {
                           Datastorage<Voyage> vdb = new Datastorage<>(localdb, Voyage.class);
                           List<Voyage> lvg=vdb.findAll();
                            keti.syncVoyage(lvg).enqueue(new Callback<ResponseBody>() {
                               @Override
                               public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                   System.out.println("Voyage synced up succesfuly "+rspns.message());
                               }

                               @Override
                               public void onFailure(Call<ResponseBody> call, Throwable thrwbl) {
                                   
                               }
                           });
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

}
