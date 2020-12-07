package org.smartregister.unicefangola.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.unicefangola.util.AppConstants;

import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.AGE;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.BASE_ENTITY_ID;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.BIRTH_REGISTRATION_NUMBER;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.CAREGIVER_AGE;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.CHILD_REG;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.DOB;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.DOB_UNKNOWN;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.FIRST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.GA_AT_BIRTH;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.GENDER;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.ID;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.INACTIVE;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.LAST_INTERACTED_WITH;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.LAST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.LOST_TO_FOLLOW_UP;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.MIDDLE_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.MOTHER_DOB;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.PLACE_OF_BIRTH;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.REGISTRATION_DATE;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.RELATIONALID;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.RELATIONAL_ID;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.SHOW_BCG2_REMINDER;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.SHOW_BCG_SCAR;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.ZEIR_ID;
import static org.smartregister.unicefangola.util.TableUtil.getAllClientColumn;
import static org.smartregister.unicefangola.util.TableUtil.getChildDetailsColumn;
import static org.smartregister.unicefangola.util.TableUtil.getMotherDetailsColumn;

public class AppChildRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String mainRegisterQuery() {
        return "select " + StringUtils.join(mainColumns(), ",") + " from " + getChildDetailsTable() + " " +
                "join " + getMotherDetailsTable() + " on " + getChildDetailsTable() + "." + RELATIONAL_ID + " = " + getMotherDetailsTable() + "." + BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + "." + BASE_ENTITY_ID + " = " + getChildDetailsTable() + "." + BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " mother on mother." + BASE_ENTITY_ID + " = " + getMotherDetailsTable() + "." + BASE_ENTITY_ID;
    }

//    private String getFatherDetailsTable() {
//        return AppConstants.TableNameConstants.FATHER_DETAILS;
//    }

    @Override
    public String[] mainColumns() {
        return new String[]{
                getAllClientColumn(ID) + "as _id",
                getAllClientColumn(RELATIONALID),
                getAllClientColumn(ZEIR_ID),
                getAllClientColumn(GENDER),
                getAllClientColumn(BASE_ENTITY_ID),
                getAllClientColumn(FIRST_NAME),
                getAllClientColumn(LAST_NAME),
//                getAllClientColumn(VILLAGE),
//                getAllClientColumn(HOME_ADDRESS),
                getAllClientColumn(DOB),
                getAllClientColumn(DOB_UNKNOWN),
                getAllClientColumn(REGISTRATION_DATE),
                getAllClientColumn(LAST_INTERACTED_WITH),
                getAllClientColumn(MIDDLE_NAME),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_FIRST_PHONE_NUMBER),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_FIRST_PHONE_NUMBER_OWNER),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_FIRST_PHONE_NUMBER_VALUE),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_SECOND_PHONE_NUMBER),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_SECOND_PHONE_NUMBER_OWNER),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_SECOND_PHONE_NUMBER_VALUE),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_HIGHEST_EDUCATION),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_RELIGION),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_MARITAL_STATUS),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_EMPLOYMENT),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_NO_OF_CHILDREN),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_LIVES),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_NAME),
                getMotherDetailsColumn(AppConstants.KeyConstants.CAREGIVER_ADDRESS),
                getMotherDetailsColumn(CAREGIVER_AGE),
                getChildDetailsColumn(AppConstants.KeyConstants.BIRTH_LOCATION),
                getChildDetailsColumn(AppConstants.KeyConstants.ANC),
                getChildDetailsColumn(AppConstants.KeyConstants.NO_ANC),
                getChildDetailsColumn(INACTIVE),
                getChildDetailsColumn(LOST_TO_FOLLOW_UP),
                getChildDetailsColumn(RELATIONAL_ID),
                getChildDetailsColumn(SHOW_BCG_SCAR),
                getChildDetailsColumn(SHOW_BCG2_REMINDER),
                getChildDetailsColumn(BIRTH_REGISTRATION_NUMBER),
                getChildDetailsColumn(CHILD_REG),
                getChildDetailsColumn(AGE),
                getChildDetailsColumn(PLACE_OF_BIRTH),
                getChildDetailsColumn(GA_AT_BIRTH),
                "mother.first_name                     as " + AppConstants.KeyConstants.MOTHER_FIRST_NAME,
                "mother.last_name                      as " + AppConstants.KeyConstants.MOTHER_LAST_NAME,
                "mother.middle_name                    as " + AppConstants.KeyConstants.CAREGIVER_MIDDLE_NAME,
                "mother.dob                            as " + MOTHER_DOB,
                "mother.gender                         as " + AppConstants.KeyConstants.CAREGIVER_SEX
        };
    }
}
