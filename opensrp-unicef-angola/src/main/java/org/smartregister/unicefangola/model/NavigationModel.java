package org.smartregister.unicefangola.model;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.smartregister.unicefangola.contract.NavigationContract;
import org.smartregister.util.Utils;

import timber.log.Timber;

public class NavigationModel implements NavigationContract.Model {

    private static NavigationModel instance;

    public static NavigationModel getInstance() {
        if (instance == null)
            instance = new NavigationModel();

        return instance;
    }

    @Override
    public String getCurrentUser() {
        String currentUser = "";
        try {
            currentUser = Utils.getPrefferedName();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e); Timber.e(e);
        }

        return currentUser;
    }
}
