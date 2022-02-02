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
                        case Succursale:
                            Datastorage<Succursale> sucdb = new Datastorage<>(localdb, Succursale.class);
                            keti.getSucursales().enqueue(new Callback<List<Succursale>>() {
                                @Override
                                public void onResponse(Call<List<Succursale>> call, Response<List<Succursale>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        List<Succursale> sucs = rspns.body();
                                        sucs.forEach(s -> {
                                            sucdb.insertIfNotExist(s, s.getUid());
                                        });
                                    }
                                    //To change body of generated methods, choose Tools | Templates.
                                }

                                @Override
                                public void onFailure(Call<List<Succursale>> call, Throwable thrwbl) {
                                    thrwbl.printStackTrace();
                                }
                            });
                            break;
                        case Comptefin: {
                            //sync up/down
                            Datastorage<Comptefin> cptdb = new Datastorage<>(localdb, Comptefin.class);
                            keti.getFinanceAccounts().enqueue(new Callback<List<Comptefin>>() {
                                @Override
                                public void onResponse(Call<List<Comptefin>> call, Response<List<Comptefin>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        List<Comptefin> lcpt = rspns.body();
                                        lcpt.forEach(C -> {
                                            cptdb.insertIfNotExist(C, C.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Comptefin>> call, Throwable thrwbl) {
                                    thrwbl.printStackTrace();
                                }
                            });

                            break;
                        }
                        case Contacts: {
                            Datastorage<Contacts> contdb = new Datastorage<>(localdb, Contacts.class);
                            keti.getContacts().enqueue(new Callback<List<Contacts>>() {
                                @Override
                                public void onResponse(Call<List<Contacts>> call, Response<List<Contacts>> rspns) {
                                    System.err.println("Response sync down contact " + rspns.message());
                                    if (rspns.isSuccessful()) {
                                        List<Contacts> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            contdb.insertIfNotExist(s, s.getUid());
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
                            Datastorage<Depense> depdb = new Datastorage<>(localdb, Depense.class);
                            keti.getDepenses().enqueue(new Callback<List<Depense>>() {
                                @Override
                                public void onResponse(Call<List<Depense>> call, Response<List<Depense>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        List<Depense> lcpt = rspns.body();
                                        lcpt.forEach(C -> {
                                            depdb.insertIfNotExist(C, C.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Depense>> call, Throwable thrwbl) {
                                    thrwbl.printStackTrace();
                                }
                            });
                            break;
                        }
                        case Marchandise: {
                            Datastorage<Marchandise> msedb = new Datastorage<>(localdb, Marchandise.class);
                            keti.getMarchandises().enqueue(new Callback<List<Marchandise>>() {
                                @Override
                                public void onResponse(Call<List<Marchandise>> call, Response<List<Marchandise>> rspns) {

                                    if (rspns.isSuccessful()) {
                                        List<Marchandise> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            msedb.insertIfNotExist(s, s.getUid());
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
                            Datastorage<Payer> pdb = new Datastorage<>(localdb, Payer.class);
                            keti.getPayers().enqueue(new Callback<List<Payer>>() {
                                @Override
                                public void onResponse(Call<List<Payer>> call, Response<List<Payer>> rspns) {
                                    System.err.println("Response down pay " + rspns.message());
                                    if (rspns.isSuccessful()) {
                                        List<Payer> lpr = rspns.body();
                                        lpr.forEach(p -> {
                                            pdb.insertIfNotExist(p, p.getUid());
                                        });
                                    }

                                }

                                @Override
                                public void onFailure(Call<List<Payer>> call, Throwable thrwbl) {
                                    System.err.println("Erruer in sync payer down " + thrwbl.getMessage());
                                }
                            });
                            break;
                        }
                        case Retirer: {
                            Datastorage<Retirer> retr = new Datastorage<>(localdb, Retirer.class);
                            keti.getRetirers().enqueue(new Callback<List<Retirer>>() {
                                @Override
                                public void onResponse(Call<List<Retirer>> call, Response<List<Retirer>> rspns) {
                                    System.err.println("Response down retrait " + rspns.message());
                                    if (rspns.isSuccessful()) {
                                        List<Retirer> lpr = rspns.body();
                                        lpr.forEach(p -> {
                                            retr.insertIfNotExist(p, p.getUid());
                                        });
                                    }

                                }

                                @Override
                                public void onFailure(Call<List<Retirer>> call, Throwable thrwbl) {
                                    System.err.println("Erruer in sync retirer down " + thrwbl.getMessage());
                                }
                            });
                            break;
                        }
                        case Tiers: {

                            Datastorage<Tiers> trsdb = new Datastorage<>(localdb, Tiers.class);
                            Datastorage<Contacts> ctkt = new Datastorage<>(localdb, Contacts.class);
                            keti.getTiers().enqueue(new Callback<List<Tiers>>() {
                                @Override
                                public void onResponse(Call<List<Tiers>> call, Response<List<Tiers>> rspns) {

                                    if (rspns.isSuccessful()) {
                                        List<Tiers> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            try {
                                                trsdb.insertIfNotExist(s, s.getUid());
                                                List<Contacts> body = keti.findContacts(s.getUid()).execute().body();

                                                body.forEach((c) -> {
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
                                    if (rspns.isSuccessful()) {
                                        List<Transporter> ltr = rspns.body();
                                        ltr.forEach(t -> {
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
                            Datastorage<User> uldb = new Datastorage<>(localdb, User.class);
                            keti.getUsers().enqueue(new Callback<List<User>>() {
                                @Override
                                public void onResponse(Call<List<User>> call, Response<List<User>> rspns) {
                                    System.err.println("Log sync down user " + rspns.message());
                                    if (rspns.isSuccessful()) {
                                        List<User> lu = rspns.body();
                                        lu.forEach(u -> {
                                            uldb.insertIfNotExist(u, u.getUid());
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<User>> call, Throwable thrwbl) {
                                    System.err.println("Erruer in sync user down " + thrwbl.getMessage());
                                }
                            });
                            break;
                        }
                        case Vehicule: {
                            Datastorage<Vehicule> vhcldb = new Datastorage<>(localdb, Vehicule.class);
                            keti.getVehicules().enqueue(new Callback<List<Vehicule>>() {
                                @Override
                                public void onResponse(Call<List<Vehicule>> call, Response<List<Vehicule>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        List<Vehicule> lsuc = rspns.body();
                                        lsuc.forEach((s) -> {
                                            vhcldb.insertIfNotExist(s, s.getUid());
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
                                    if (rspns.isSuccessful()) {
                                        List<Voyage> ltr = rspns.body();
                                        ltr.forEach(t -> {
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

    public void syncUp() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture result;
        result = ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Types t : Types.values()) {
                    switch (t) {
                        case Succursale:
                            Datastorage<Succursale> sucdb = new Datastorage<>(localdb, Succursale.class);
                            List<Succursale> ls = sucdb.findAll();
                            keti.syncSucursal(ls).enqueue(new Callback<List<Succursale>>() {
                                @Override
                                public void onResponse(Call<List<Succursale>> call, Response<List<Succursale>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        System.out.println("Sync down fincompte succesfully ");
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Succursale>> call, Throwable thrwbl) {
                                    thrwbl.printStackTrace();
                                }
                            });
                            break;
                        case Comptefin: {
                            //sync up/down
                            Datastorage<Comptefin> cptdb = new Datastorage<>(localdb, Comptefin.class);
                            List<Comptefin> comptefins = cptdb.findAll();
                            keti.syncFinAccount(comptefins).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                    if (rspns.isSuccessful()) {
                                        System.out.println("Sync down fincompte succesfully ");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable thrwbl) {
                                    thrwbl.printStackTrace();
                                }
                            });
                            break;
                        }
                        case Contacts: {
                            Datastorage<Contacts> ctxdb = new Datastorage<>(localdb, Contacts.class);
                            List<Contacts> ctkt = ctxdb.findAll();
                            keti.syncContact(ctkt).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                    if (rspns.isSuccessful()) {
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
                            Datastorage<Depense> ctxdb = new Datastorage<>(localdb, Depense.class);
                            List<Depense> comptefins = ctxdb.findAll();
                            keti.syncDepense(comptefins).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                    if (rspns.isSuccessful()) {
                                        System.out.println("Sync down Depense succesfully ");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable thrwbl) {
                                    thrwbl.printStackTrace();
                                }
                            });
                            break;
                        }
                        case Marchandise: {
                            Datastorage<Marchandise> ctxdb = new Datastorage<>(localdb, Marchandise.class);
                            List<Marchandise> lm = ctxdb.findAll();
                            keti.syncGoods(lm).enqueue(new Callback<List<Marchandise>>() {
                                @Override
                                public void onResponse(Call<List<Marchandise>> call, Response<List<Marchandise>> rspns) {
                                    if (rspns.isSuccessful()) {
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
                            Datastorage<Payer> paydb = new Datastorage<>(localdb, Payer.class);
                            List<Payer> ctkt = paydb.findAll();
                            keti.syncPayer(ctkt).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                    System.out.println("response server " + rspns.message());
                                    if (rspns.isSuccessful()) {
                                        System.out.println("Payer synced successfully");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable thrwbl) {
                                    System.out.println("Erreur de sync Payer"); //To change body of generated methods, choose Tools | Templates.
                                }
                            });
                            break;
                        }
                        case Retirer: {
                            Datastorage<Retirer> rtrdb = new Datastorage<>(localdb, Retirer.class);
                            List<Retirer> ctkt = rtrdb.findAll();
                            keti.syncRetirer(ctkt).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                    System.out.println("response server rtr " + rspns.message());
                                    if (rspns.isSuccessful()) {
                                        System.out.println("Retirer synced successfully");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable thrwbl) {
                                    System.out.println("Erreur de sync Retirer"); //To change body of generated methods, choose Tools | Templates.
                                }
                            });
                            break;
                        }
                        case Tiers: {
                            Datastorage<Tiers> ctxdb = new Datastorage<>(localdb, Tiers.class);
                            List<Tiers> ltr = ctxdb.findAll();
                            keti.syncTiers(ltr).enqueue(new Callback<List<Tiers>>() {
                                @Override
                                public void onResponse(Call<List<Tiers>> call, Response<List<Tiers>> rspns) {
                                    System.out.println("Up sync tier rspns " + rspns.message());
                                    if (rspns.isSuccessful()) {
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
                                    System.out.println("Up syns trans " + rspns.message());
                                    if (rspns.isSuccessful()) {
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
                            Datastorage<Vehicule> vhldb = new Datastorage<>(localdb, Vehicule.class);
                            List<Vehicule> vls = vhldb.findAll();
                            keti.syncVehicule(vls).enqueue(new Callback<List<Vehicule>>() {
                                @Override
                                public void onResponse(Call<List<Vehicule>> call, Response<List<Vehicule>> rspns) {
                                    if (rspns.isSuccessful()) {
                                        System.out.println("Vehicule synced up succesfuly " + rspns.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Vehicule>> call, Throwable thrwbl) {
                                    thrwbl.printStackTrace();
                                }
                            });
                            break;
                        }
                        case Voyage: {
                            Datastorage<Voyage> vdb = new Datastorage<>(localdb, Voyage.class);
                            List<Voyage> lvg = vdb.findAll();
                            keti.syncVoyage(lvg).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> rspns) {
                                    System.out.println("Voyage synced up succesfuly " + rspns.message());
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

    public void start() {
        syncDown();
        syncUp();
    }

}
