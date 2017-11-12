package com.example.owner.shoppinglist;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by owner on 11/10/17.
 */

public class ListApplication extends Application {
    private Realm realmList;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }

    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realmList = Realm.getInstance(config);
    }

    public void closeRealm() {
        realmList.close();
    }

    public Realm getRealmList() {
        return realmList;
    }
}
