package org.smartregister.unicefangola.util;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.UniqueId;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.unicefangola.application.UnicefAngolaApplication;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 07/10/2021.
 */
public class DataCleanUpUtils {
    private static final String fetchDuplicatesSQL = "WITH duplicateCaregivers AS (WITH allCaregivers AS ( select baseEntityId, json_extract(json, '$.identifiers.M_ZEIR_ID')  m_zeir_id, syncStatus from client  ) " +
            "select b.* from (select baseEntityId, m_zeir_id from (allCaregivers) group by m_zeir_id having count(m_zeir_id) > 1) a " +
            "INNER JOIN (allCaregivers) b on b.m_zeir_id = a.m_zeir_id  ORDER by m_zeir_id) " +
            "SELECT baseEntityId, m_zeir_id, lag(m_zeir_id) over(order by m_zeir_id) as prev_m_zeir_id from duplicateCaregivers;";

    public static void cleanUpDuplicateMotherIds() throws Exception {

        String username = UnicefAngolaApplication.getInstance().context().userService().getAllSharedPreferences().fetchRegisteredANM();

        new PullUniqueIDs();//Ensure we pull in Unique IDS first

        SQLiteDatabase database = DrishtiApplication.getInstance().getRepository().getReadableDatabase();
        UniqueIdRepository uniqueIdRepository = ChildLibrary.getInstance().getUniqueIdRepository();

        List<String> motherBaseEntityIdsWithDuplicates = new ArrayList<>();

        Cursor cursor = null;

        try {

            cursor = database.rawQuery(fetchDuplicatesSQL, new String[]{});

            while (cursor.moveToNext()) {

                motherBaseEntityIdsWithDuplicates.add(cursor.getString(cursor.getColumnIndex("baseEntityId")));
                String mZeirId = cursor.getString(cursor.getColumnIndex("m_zeir_id"));
                String prevZeirId = null;
                try {
                    prevZeirId = cursor.getString(cursor.getColumnIndex("prev_m_zeir_id"));
                } catch (NullPointerException e){
                    Timber.i("null prev_m_zeir_id %s",e.getMessage());
                }
                if(!StringUtils.isBlank(prevZeirId) & (prevZeirId.equals(mZeirId))){
                    motherBaseEntityIdsWithDuplicates.add(cursor.getString(cursor.getColumnIndex("baseEntityId")));
                }

            }

        } catch (Exception e) {

            FirebaseCrashlytics.getInstance().recordException(e); Timber.e(e);


        } finally {

            if (cursor == null && !cursor.isClosed())
                cursor.close();
        }

        Timber.d("###### %s , username %s", motherBaseEntityIdsWithDuplicates.toString(), username);

        for (String baseEntityId : motherBaseEntityIdsWithDuplicates) {


            JSONObject client = UnicefAngolaApplication.getInstance().eventClientRepository().getClientByBaseEntityId(baseEntityId);


            JSONObject identifiers = UnicefAngolaApplication.getInstance().eventClientRepository().getClientByBaseEntityId(baseEntityId).getJSONObject("identifiers");

            UniqueId uniqueId = uniqueIdRepository.getNextUniqueId();
            String newZeirId = uniqueId != null ? uniqueId.getOpenmrsId() : null;

            if (StringUtils.isBlank(newZeirId))
                throw new Exception(String.format("NO Unique Id Found to assign to %s, ProviderId ", baseEntityId, username));

            identifiers.put(ChildJsonFormUtils.M_ZEIR_ID, newZeirId);

            client.put("identifiers", identifiers);

            UnicefAngolaApplication.getInstance().eventClientRepository().addorUpdateClient(baseEntityId, client);

            UnicefAngolaApplication.getInstance().eventClientRepository().markClientValidationStatus(baseEntityId, false);

            uniqueIdRepository.close(newZeirId);

        }

        //Log the successes
        if (motherBaseEntityIdsWithDuplicates.size() > 0) {
            FirebaseCrashlytics.getInstance().recordException(new Exception(String.format("Successful duplicates processed %s , ProviderId %s", motherBaseEntityIdsWithDuplicates.toString(), username)));
        }else{
            FirebaseCrashlytics.getInstance().recordException(new Exception(String.format("NO Duplicates to clean up for ProviderId %s", motherBaseEntityIdsWithDuplicates.toString(), username)));

        }

    }


}
