package org.smartregister.unicefangola.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.unicefangola.util.AppConstants;

import static org.smartregister.unicefangola.util.AppConstants.KEY.AGE;
import static org.smartregister.unicefangola.util.AppConstants.KEY.BASE_ENTITY_ID;
import static org.smartregister.unicefangola.util.AppConstants.KEY.BIRTH_REGISTRATION_NUMBER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.CAREGIVER_AGE;
import static org.smartregister.unicefangola.util.AppConstants.KEY.CHILD_REG;
import static org.smartregister.unicefangola.util.AppConstants.KEY.DOB;
import static org.smartregister.unicefangola.util.AppConstants.KEY.DOB_UNKNOWN;
import static org.smartregister.unicefangola.util.AppConstants.KEY.FATHER_DOB;
import static org.smartregister.unicefangola.util.AppConstants.KEY.FATHER_NATIONALITY;
import static org.smartregister.unicefangola.util.AppConstants.KEY.FATHER_NATIONALITY_OTHER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.FATHER_RELATIONAL_ID;
import static org.smartregister.unicefangola.util.AppConstants.KEY.FIRST_BIRTH;
import static org.smartregister.unicefangola.util.AppConstants.KEY.FIRST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KEY.GA_AT_BIRTH;
import static org.smartregister.unicefangola.util.AppConstants.KEY.GENDER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.HOME_ADDRESS;
import static org.smartregister.unicefangola.util.AppConstants.KEY.ID;
import static org.smartregister.unicefangola.util.AppConstants.KEY.INACTIVE;
import static org.smartregister.unicefangola.util.AppConstants.KEY.LAST_INTERACTED_WITH;
import static org.smartregister.unicefangola.util.AppConstants.KEY.LAST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KEY.LOST_TO_FOLLOW_UP;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MIDDLE_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_DOB;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_GUARDIAN_NUMBER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_NATIONALITY;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_NATIONALITY_OTHER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_PHONE_NUMBER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_RUBELLA;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_SECOND_PHONE_NUMBER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.MOTHER_TDV_DOSES;
import static org.smartregister.unicefangola.util.AppConstants.KEY.PLACE_OF_BIRTH;
import static org.smartregister.unicefangola.util.AppConstants.KEY.PROTECTED_AT_BIRTH;
import static org.smartregister.unicefangola.util.AppConstants.KEY.REGISTRATION_DATE;
import static org.smartregister.unicefangola.util.AppConstants.KEY.RELATIONALID;
import static org.smartregister.unicefangola.util.AppConstants.KEY.RELATIONAL_ID;
import static org.smartregister.unicefangola.util.AppConstants.KEY.RUBELLA_SEROLOGY;
import static org.smartregister.unicefangola.util.AppConstants.KEY.SECOND_PHONE_NUMBER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.SEROLOGY_RESULTS;
import static org.smartregister.unicefangola.util.AppConstants.KEY.SHOW_BCG2_REMINDER;
import static org.smartregister.unicefangola.util.AppConstants.KEY.SHOW_BCG_SCAR;
import static org.smartregister.unicefangola.util.AppConstants.KEY.VILLAGE;
import static org.smartregister.unicefangola.util.AppConstants.KEY.ZEIR_ID;
import static org.smartregister.unicefangola.util.TableUtil.getAllClientColumn;
import static org.smartregister.unicefangola.util.TableUtil.getChildDetailsColumn;
import static org.smartregister.unicefangola.util.TableUtil.getFatherDetailsColumn;
import static org.smartregister.unicefangola.util.TableUtil.getMotherDetailsColumn;

public class AppChildRegisterQueryProvider extends RegisterQueryProvider {

    @Override
    public String mainRegisterQuery() {
        return "select " + StringUtils.join(mainColumns(), ",") + " from " + getChildDetailsTable() + " " +
                "join " + getMotherDetailsTable() + " on " + getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "left join " + getFatherDetailsTable() + " on " + getChildDetailsTable() + "." + Constants.KEY.FATHER_RELATIONAL_ID + " = " + getFatherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID + " = " + getChildDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " mother on mother." + Constants.KEY.BASE_ENTITY_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "left join " + getDemographicTable() + " father on father." + Constants.KEY.BASE_ENTITY_ID + " = " + getFatherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID;
    }

    private String getFatherDetailsTable() {
        return AppConstants.TABLE_NAME.FATHER_DETAILS;
    }

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
                getAllClientColumn(VILLAGE),
                getAllClientColumn(HOME_ADDRESS),
                getAllClientColumn(DOB),
                getAllClientColumn(DOB_UNKNOWN),
                getAllClientColumn(REGISTRATION_DATE),
                getAllClientColumn(LAST_INTERACTED_WITH),
                getAllClientColumn(MIDDLE_NAME),
                getMotherDetailsColumn(MOTHER_NATIONALITY),
                getMotherDetailsColumn(MOTHER_NATIONALITY_OTHER),
                getMotherDetailsColumn(PROTECTED_AT_BIRTH),
                getMotherDetailsColumn(MOTHER_TDV_DOSES),
                getMotherDetailsColumn(FIRST_BIRTH),
                getMotherDetailsColumn(RUBELLA_SEROLOGY),
                getMotherDetailsColumn(SEROLOGY_RESULTS),
                getMotherDetailsColumn(MOTHER_RUBELLA),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_FIRST_PHONE_NUMBER),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_FIRST_PHONE_NUMBER_OWNER),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_FIRST_PHONE_NUMBER_VALUE),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_SECOND_PHONE_NUMBER),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_SECOND_PHONE_NUMBER_OWNER),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_SECOND_PHONE_NUMBER_VALUE),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_HIGHEST_EDUCATION),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_RELIGION),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_MARITAL_STATUS),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_EMPLOYMENT),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_NO_OF_CHILDREN),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_LIVES),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_NAME),
                getMotherDetailsColumn(AppConstants.KEY.CAREGIVER_ADDRESS),
                getMotherDetailsColumn(CAREGIVER_AGE),
                getFatherDetailsColumn(FATHER_NATIONALITY),
                getFatherDetailsColumn(FATHER_NATIONALITY_OTHER),
                getChildDetailsColumn(AppConstants.KEY.BIRTH_LOCATION),
                getChildDetailsColumn(AppConstants.KEY.ANC),
                getChildDetailsColumn(AppConstants.KEY.NO_ANC),
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
                getChildDetailsColumn(FATHER_RELATIONAL_ID),
                "mother.first_name                     as " + AppConstants.KEY.MOTHER_FIRST_NAME,
                "mother.last_name                      as " + AppConstants.KEY.MOTHER_LAST_NAME,
                "mother.middle_name                    as " + AppConstants.KEY.CAREGIVER_MIDDLE_NAME,
                "mother.dob                            as " + MOTHER_DOB,
                "mother.gender                         as " + AppConstants.KEY.CAREGIVER_SEX,
                "father.first_name                     as " + AppConstants.KEY.FATHER_FIRST_NAME,
                "father.last_name                      as " + AppConstants.KEY.FATHER_LAST_NAME,
                "father.dob                            as " + FATHER_DOB
        };
    }
}
