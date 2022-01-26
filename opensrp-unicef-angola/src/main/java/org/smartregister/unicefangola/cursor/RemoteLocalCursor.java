package org.smartregister.unicefangola.cursor;

import android.database.Cursor;

import net.sqlcipher.InvalidRowColumnException;

import org.smartregister.child.util.DBConstants;
import org.smartregister.unicefangola.util.AppConstants;

import timber.log.Timber;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class RemoteLocalCursor {
    private String id;
    private String relationalId;
    private String motherBaseEntityId;
    private String firstName;
    private String lastName;
    private String gender;
    private String dob;
    private String openSrpId;
    private String motherFirstName;
    private String motherLastName;
    private String inactive;
    private String lostToFollowUp;
    private String fatherBaseEntityId;

    public RemoteLocalCursor(Cursor cursor) {
        try {
            id = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ID_LOWER_CASE));
            relationalId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.RELATIONALID));
            motherBaseEntityId = cursor.getString(cursor.getColumnIndex(AppConstants.KeyConstants.RELATIONAL_ID));

            fatherBaseEntityId = cursor.getString(
                    cursor.getColumnIndex(AppConstants.KeyConstants.FATHER_BASE_ENTITY_ID) > -1 ?
                            cursor.getColumnIndex(AppConstants.KeyConstants.FATHER_BASE_ENTITY_ID):
                            cursor.getColumnIndex(AppConstants.KeyConstants.FATHER_RELATIONAL_ID)
            );

            firstName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FIRST_NAME));
            lastName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.LAST_NAME));
            dob = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DOB));
            openSrpId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ZEIR_ID));
            gender = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.GENDER));
            motherFirstName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.MOTHER_FIRST_NAME));
            motherLastName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.MOTHER_LAST_NAME));
            inactive = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.INACTIVE));
            lostToFollowUp = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.LOST_TO_FOLLOW_UP));
        } catch (InvalidRowColumnException ex) {
            FirebaseCrashlytics.getInstance().recordException(ex); Timber.e(ex);
        }

    }

    public String getId() {
        return id;
    }

    public String getRelationalId() {
        return relationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getOpenSrpId() {
        return openSrpId;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public String getInactive() {
        return inactive;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }

    public String getLostToFollowUp() {
        return lostToFollowUp;
    }

    public String getMotherBaseEntityId() {
        return motherBaseEntityId;
    }

    public String getFatherBaseEntityId() {
        return fatherBaseEntityId;
    }

}
