package org.smartregister.unicefangola.interactor;

import org.smartregister.unicefangola.application.UnicefAngolaApplication;
import org.smartregister.unicefangola.contract.NavigationContract;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import java.util.Date;

import timber.log.Timber;

public class NavigationInteractor implements NavigationContract.Interactor {

    private static NavigationInteractor instance;

    public static NavigationInteractor getInstance() {
        if (instance == null)
            instance = new NavigationInteractor();

        return instance;
    }

    @Override
    public Date sync() {
        Date syncDate = null;
        try {
            syncDate = new Date(getLastCheckTimeStamp());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e); Timber.e(e);
        }

        return syncDate;
    }

    private Long getLastCheckTimeStamp() {
        return UnicefAngolaApplication.getInstance().getEcSyncHelper().getLastCheckTimeStamp();
    }
}
