/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.sun.javafx.PlatformUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import keti_client.keti_UIController;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.event.ChangeListener;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import static org.dizitart.no2.objects.filters.ObjectFilters.eq;
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

    public List<T> findAllLike(String key, String value) {
        Cursor<T> find = repository.find(text(key, value));
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
