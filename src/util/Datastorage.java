/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.event.ChangeListener;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import static org.dizitart.no2.objects.filters.ObjectFilters.and;
import static org.dizitart.no2.objects.filters.ObjectFilters.elemMatch;
import static org.dizitart.no2.objects.filters.ObjectFilters.eq;
import static org.dizitart.no2.objects.filters.ObjectFilters.gte;
import static org.dizitart.no2.objects.filters.ObjectFilters.lte;
import static org.dizitart.no2.objects.filters.ObjectFilters.text;

/**
 *
 * @author eroot
 */
public class Datastorage<T> {

    Nitrite db;
    ObjectRepository<T> repository;

    public Datastorage(Nitrite db, Class<T> classs) {
        this.db = db;
        repository = this.db.getRepository(classs);
    }

    public void registerListener(ChangeListener changelistenr) {
        this.repository.register(changelistenr);
    }

    public Datastorage(Nitrite db, Class<T> classs, String key) {
        this.db = db;
        repository = this.db.getRepository(key, classs);
    }

    public Datastorage(String path, String username, String password) {
        File file = new File(path);
        db = Nitrite.builder()
                .filePath(file)
                .compressed()
                .autoCommitBufferSize(16)
                .openOrCreate(username, password);

    }

    public T insert(T obj) {
        repository.insert(obj);
        return obj;
    }

    public T insertIfNotExist(T obj, String uid) {
        T t = findById(uid);
        if (t == null) {
            repository.insert(obj);
        }
        return obj;
    }

    public T update(String key, String value, T obj) {
        repository.update(eq(key, value), obj);
        return obj;
    }

    public void delete(String key, String value) {
        repository.remove(eq(key, value));
    }

    public List<T> findAll() {
        Cursor<T> find = repository.find();
        return find.toList();
    }

    public T findById(String uid) {
        Cursor<T> t = repository.find(eq("uid", uid));
        List<T> rst = t.toList();
        return rst.isEmpty() ? null : rst.get(0);
    }

    public List<T> findAllEquals(String key, String value) {
        Cursor<T> find = repository.find(eq(key, value));
        return find.toList();
    }
    /**
     * This function must be used with chargement only
     * @param value
     * @return 
     */
    public List<T> findTracking(int value) {
        Cursor<T> find = repository.find(eq("tracking", value));
        return find.toList();
    }
    /**
     * this function is to be use only and only if object is an instance of payer as well as function findPayerCrediteur
     * @param value
     * @return 
     */
    public List<T> findPayerDebiteur(String value){
        Cursor<T> find = repository.find(eq("compteIdDebit", value));
        return find.toList();
    }
     /**
     * this function is to be use only and only if object is an instance of payer as well as function findPayerDebiteur
     * @param value
     * @return 
     */
    public List<T> findPayerCrediteur(String value){
        Cursor<T> find = repository.find(eq("compteIdCredit", value));
        return find.toList();
    }
    
    public List<T> findBetween(String key, long value1, long value2) {
        Cursor<T> find = repository.find(elemMatch(key, and(gte(key, value1),lte(key, value2))));
        return find.toList();
    }

    public List<T> findAllLike(String key, String value) {
        Cursor<T> find = repository.find(text(key, value+"*"));
        return find.toList();
    }

    public void sync() {
        ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        newSingleThreadScheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                //check web end points
            }
        }, 1, TimeUnit.MINUTES);

    }

}
