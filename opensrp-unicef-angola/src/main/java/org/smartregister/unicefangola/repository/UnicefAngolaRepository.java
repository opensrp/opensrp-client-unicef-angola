package org.smartregister.unicefangola.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.child.util.ChildDbMigrations;
import org.smartregister.child.util.Utils;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.domain.db.Column;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.growthmonitoring.repository.WeightForHeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.reporting.repository.IndicatorQueryRepository;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Hia2ReportRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.LocationTagRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.unicefangola.BuildConfig;
import org.smartregister.unicefangola.application.UnicefAngolaApplication;
import org.smartregister.util.DatabaseMigrationUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class UnicefAngolaRepository extends Repository {

    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    final private Context context;
//    private String appVersionCodePref = AppConstants.Pref.APP_VERSION_CODE;

    private final String SET_CLIENT_TABLE_VALIDATION_STATUS_TO_INVALID = "UPDATE client SET validationStatus = '%s'";

    public UnicefAngolaRepository(@NonNull Context context, @NonNull org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(),
                UnicefAngolaApplication.createCommonFtsObject(context), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository
                .createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository
                .createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
        ConfigurableViewsRepository.createTable(database);
        UniqueIdRepository.createTable(database);

        SettingsRepository.onUpgrade(database);
        WeightRepository.createTable(database);
        HeightRepository.createTable(database);
        VaccineRepository.createTable(database);
        WeightForHeightRepository.createTable(database);

        LocationRepository.createTable(database);
        LocationTagRepository.createTable(database);

        ClientRegisterTypeRepository.createTable(database);
        ChildAlertUpdatedRepository.createTable(database);

        runLegacyUpgrades(database);
        onUpgrade(database, 11, BuildConfig.DATABASE_VERSION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w("Upgrading database from version %d to %d, which will destroy all old data", oldVersion, newVersion);

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                case 5:
                    upgradeToVersion5(db);
                    break;
                case 6:
                    upgradeToVersion6(db);
                    break;
                case 7:
                    upgradeToVersion7OutOfArea(db);
                    break;
                case 8:
                    upgradeToVersion8AddServiceGroupColumn(db);
                    break;
                case 9:
                    ChildDbMigrations.addShowBcg2ReminderAndBcgScarColumnsToEcChildDetails(db);
                    break;
                case 12:
                    upgradeToVersion12SetClientValidationStatusInvalid(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }

        ChildDbMigrations.addShowBcg2ReminderAndBcgScarColumnsToEcChildDetails(db);

        IndicatorQueryRepository.performMigrations(db);

    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        byte[] pass = UnicefAngolaApplication.getInstance().getPassword();
        if (pass != null && pass.length > 0) {
            return getReadableDatabase(pass);
        }
        return null;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        byte[] pass = UnicefAngolaApplication.getInstance().getPassword();
        if (pass != null && pass.length > 0) {
            return getWritableDatabase(pass);
        } else {
            throw new IllegalStateException("Password is blank");
        }
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

    private void runLegacyUpgrades(@NonNull SQLiteDatabase database) {
        upgradeToVersion2(database);
        upgradeToVersion3(database);
        upgradeToVersion4(database);
        upgradeToVersion5(database);
        upgradeToVersion6(database);
        upgradeToVersion7OutOfArea(database);
        upgradeToVersion7EventWeightHeightVaccineRecurringChange(database);
        upgradeToVersion7VaccineRecurringServiceRecordChange(database);
        upgradeToVersion7WeightHeightVaccineRecurringServiceChange(database);
        upgradeToVersion7RemoveUnnecessaryTables(database);
    }

    /**
     * Version 16 added service_group column
     *
     * @param database SQLiteDatabase
     */
    private void upgradeToVersion8AddServiceGroupColumn(@NonNull SQLiteDatabase database) {
        try {
            database.execSQL(RecurringServiceTypeRepository.ADD_SERVICE_GROUP_COLUMN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion8AddServiceGroupColumn");
        }
    }

    /**
     * Version 2 added some columns to the ec_child table
     */
    private void upgradeToVersion2(@NonNull SQLiteDatabase database) {
        try {
            // Run insert query
            ArrayList<String> newlyAddedFields = new ArrayList<>();
            newlyAddedFields.add("BCG_2");
            newlyAddedFields.add("inactive");
            newlyAddedFields.add("lost_to_follow_up");

            DatabaseMigrationUtils.addFieldsToFTSTable(database, commonFtsObject, Utils.metadata().childRegister.tableName,
                    newlyAddedFields);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion2");
        }
    }

    private void upgradeToVersion3(@NonNull SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(VaccineRepository.EVENT_ID_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(WeightRepository.EVENT_ID_INDEX);
            db.execSQL(HeightRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(HeightRepository.EVENT_ID_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(VaccineRepository.FORMSUBMISSION_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(WeightRepository.FORMSUBMISSION_INDEX);
            db.execSQL(HeightRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(HeightRepository.FORMSUBMISSION_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_IS_VOIDED_COL_INDEX);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion3");
        }
    }

    private void upgradeToVersion4(@NonNull SQLiteDatabase db) {
        try {
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion4");
        }
    }

    private void upgradeToVersion5(@NonNull SQLiteDatabase db) {
        try {
            RecurringServiceTypeRepository.createTable(db);
            RecurringServiceRecordRepository.createTable(db);

            RecurringServiceTypeRepository recurringServiceTypeRepository = UnicefAngolaApplication.getInstance()
                    .recurringServiceTypeRepository();
            IMDatabaseUtils.populateRecurringServices(context, db, recurringServiceTypeRepository);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion5");
        }
    }

    private void upgradeToVersion6(@NonNull SQLiteDatabase db) {
        try {
            WeightZScoreRepository.createTable(db);
            db.execSQL(WeightRepository.ALTER_ADD_Z_SCORE_COLUMN);

            HeightZScoreRepository.createTable(db);
            db.execSQL(HeightRepository.ALTER_ADD_Z_SCORE_COLUMN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion6");
        }
    }

    private void upgradeToVersion7OutOfArea(@NonNull SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(HeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(HeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7");
        }
    }

    private void upgradeToVersion7EventWeightHeightVaccineRecurringChange(@NonNull SQLiteDatabase db) {
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);

            db.execSQL(WeightRepository.ALTER_ADD_CREATED_AT_COLUMN);
            WeightRepository.migrateCreatedAt(db);

            db.execSQL(HeightRepository.ALTER_ADD_CREATED_AT_COLUMN);
            HeightRepository.migrateCreatedAt(db);

            db.execSQL(VaccineRepository.ALTER_ADD_CREATED_AT_COLUMN);
            VaccineRepository.migrateCreatedAt(db);

            db.execSQL(RecurringServiceRecordRepository.ALTER_ADD_CREATED_AT_COLUMN);
            RecurringServiceRecordRepository.migrateCreatedAt(db);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7EventWeightHeightVaccineRecurringChange");
        }
    }

    private void upgradeToVersion7VaccineRecurringServiceRecordChange(@NonNull SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_COL);

            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7VaccineRecurringServiceRecordChange");
        }
    }

    private void upgradeToVersion7WeightHeightVaccineRecurringServiceChange(@NonNull SQLiteDatabase db) {
        try {

            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_TEAM_COL);

            db.execSQL(HeightRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(HeightRepository.UPDATE_TABLE_ADD_TEAM_COL);

            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);

            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(HeightRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);

            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7WeightHeightVaccineRecurringServiceChange");
        }
    }

    private void upgradeToVersion7RemoveUnnecessaryTables(@NonNull SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS address");
            db.execSQL("DROP TABLE IF EXISTS obs");
            if (DatabaseMigrationUtils.isColumnExists(db, "path_reports", Hia2ReportRepository.report_column.json.name()))
                db.execSQL("ALTER TABLE path_reports RENAME TO " + Hia2ReportRepository.Table.hia2_report.name() + ";");
            if (DatabaseMigrationUtils.isColumnExists(db, EventClientRepository.Table.client.name(), "firstName"))
                DatabaseMigrationUtils.recreateSyncTableWithExistingColumnsOnly(db, EventClientRepository.Table.client);
            if (DatabaseMigrationUtils.isColumnExists(db, EventClientRepository.Table.event.name(), "locationId"))
                DatabaseMigrationUtils.recreateSyncTableWithExistingColumnsOnly(db, EventClientRepository.Table.event);


        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion7RemoveUnnecessaryTables");
        }
    }

    /**
     * reset client validation status to force resync
     */

    private void upgradeToVersion12SetClientValidationStatusInvalid(@NonNull SQLiteDatabase database) {
        try {
            database.execSQL(String.format(SET_CLIENT_TABLE_VALIDATION_STATUS_TO_INVALID, BaseRepository.TYPE_InValid));
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion12setClientValidationStatusUnsynced");
        }
    }

    /*private boolean checkIfAppUpdated() {
        String savedAppVersion = ReportingLibrary.getInstance().getContext().allSharedPreferences().getPreference(appVersionCodePref);
        if (savedAppVersion.isEmpty()) {
            return true;
        } else {
            int savedVersion = Integer.parseInt(savedAppVersion);
            return (BuildConfig.VERSION_CODE > savedVersion);
        }
    }*/
}
