package hu.ait.android.shoppinglist_zskluzacek;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Zachary on 11/7/2017.
 */

public class ShoppingListApplication extends Application {

    private Realm realmSL;

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
        realmSL = Realm.getInstance(config);
    }

    public void closeRealm() {
        realmSL.close();
    }

    public Realm getRealmSL() {
        return realmSL;
    }
}
