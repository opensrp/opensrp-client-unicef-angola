package org.smartregister.unicefangola.interactor;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.repository.BaseRepository;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.StatsFragmentContract;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.AllConstants.SyncInfo.CACHED_HEIGHTS;
import static org.smartregister.AllConstants.SyncInfo.CACHED_RECURRING_SERVICE_RECORDS;
import static org.smartregister.AllConstants.SyncInfo.CACHED_VACCINES;
import static org.smartregister.AllConstants.SyncInfo.CACHED_WEIGHTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

public class CachedStatisticsInteractor implements StatsFragmentContract.Interactor {

    private AppExecutors appExecutors;

    private SQLiteDatabase database;

    private StatsFragmentContract.Presenter presenter;

    private Map<String, Integer> syncInfoMap = new HashMap<>();

    public CachedStatisticsInteractor(StatsFragmentContract.Presenter presenter) {
        this.presenter = presenter;
        appExecutors = new AppExecutors();
        database = DrishtiApplication.getInstance().getRepository().getReadableDatabase();
    }

    @Override
    public void fetchECSyncInfo() {
        syncInfoMap = new HashMap<>();
        syncInfoMap.put(CACHED_VACCINES, 0);
        syncInfoMap.put(CACHED_RECURRING_SERVICE_RECORDS, 0);
        syncInfoMap.put(CACHED_HEIGHTS, 0);
        syncInfoMap.put(CACHED_WEIGHTS, 0);

        String cachedVaccinesSql = "select count(*)  from vaccines WHERE sync_status != 'Synced'";
        String cachedRecurringServiceRecordsSql = "select count(*) from recurring_service_records WHERE sync_status != 'Synced'";

        Cursor cursor = null;

        try {


            cursor = database.rawQuery(cachedVaccinesSql, new String[]{});
            while (cursor.moveToNext()) {
                populateCachedVaccinesInfo(cursor);
            }
            cursor.close();


            cursor = database.rawQuery(cachedRecurringServiceRecordsSql, new String[]{});
            while (cursor.moveToNext()) {
                populateCachedRecurringServiceRecordInfo(cursor);
            }
            cursor.close();

            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    presenter.onECSyncInfoFetched(syncInfoMap);
                }
            });
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void populateCachedVaccinesInfo(Cursor cursor) {
        syncInfoMap.put(CACHED_VACCINES, cursor.getInt(0));
    }

    private void populateCachedRecurringServiceRecordInfo(Cursor cursor) {
        syncInfoMap.put(CACHED_RECURRING_SERVICE_RECORDS, cursor.getInt(0));
    }
}

