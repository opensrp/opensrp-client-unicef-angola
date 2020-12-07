package org.smartregister.unicefangola.activity;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.unicefangola.R;
import org.smartregister.unicefangola.fragment.AppChildFormFragment;
import org.smartregister.unicefangola.util.AppConstants;

import java.util.Map;

import static org.smartregister.unicefangola.fragment.AppChildFormFragment.getFormFragment;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.FIRST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.LAST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.MOTHER_GUARDIAN_NUMBER;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.MOTHER_NATIONALITY;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.MOTHER_NATIONALITY_OTHER;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.MOTHER_RUBELLA;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.MOTHER_TDV_DOSES;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.PROTECTED_AT_BIRTH;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.SECOND_PHONE_NUMBER;
import static org.smartregister.unicefangola.util.TableUtil.getMotherDetailsColumn;

public class ChildFormActivity extends BaseChildFormActivity {
    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        AppChildFormFragment childFormFragment = getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(R.id.container, childFormFragment).commit();
    }

    protected static String getMainConditionString(Map<String, String> entityMap) {

        String mainConditionString = "";
        for (Map.Entry<String, String> entry : entityMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.firstName)) {
                key = FIRST_NAME;
            }

            if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.lastName)) {
                key = LAST_NAME;
            }

            if (StringUtils.equalsIgnoreCase(key, MotherLookUpUtils.MOTHER_GUARDIAN_PHONE_NUMBER)) {
                key = getMotherDetailsColumn(MOTHER_GUARDIAN_NUMBER);
            }

            if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.birthDate)) {
                if (!isDate(value)) {
                    continue;
                }
                key = AppConstants.KeyConstants.DOB;
            }

            if (!key.equals(AppConstants.KeyConstants.DOB)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + key + " Like '%" + value + "%'";
                } else {
                    mainConditionString += " AND " + key + " Like '%" + value + "%'";

                }
            } else {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " cast(" + key + " as date) " + " =  cast('" + value + "'as date) ";
                } else {
                    mainConditionString += " AND cast(" + key + " as date) " + " =  cast('" + value + "'as date) ";
                }
            }
        }
        return mainConditionString;
    }

    @Override
    public String lookUpQuery(Map<String, String> entityMap, String tableName) {
        RegisterQueryProvider queryProvider = Utils.metadata().getRegisterQueryProvider();

        String[] lookupColumns = new String[]{queryProvider.getDemographicTable() + "." + MotherLookUpUtils.RELATIONALID,
                queryProvider.getDemographicTable() + "." + MotherLookUpUtils.DETAILS, Constants.KEY.ZEIR_ID, FIRST_NAME, LAST_NAME,
                queryProvider.getDemographicTable() + "." + AllConstants.ChildRegistrationFields.GENDER,
                queryProvider.getDemographicTable() + "." + Constants.KEY.DOB,
                queryProvider.getDemographicTable() + "." + AppConstants.KeyConstants.HOME_ADDRESS,
                queryProvider.getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID,
                getMotherDetailsColumn(MOTHER_NATIONALITY),
                getMotherDetailsColumn(MOTHER_NATIONALITY_OTHER),
                getMotherDetailsColumn(PROTECTED_AT_BIRTH),
                getMotherDetailsColumn(MOTHER_TDV_DOSES),
                getMotherDetailsColumn(MOTHER_RUBELLA),
                getMotherDetailsColumn(MOTHER_GUARDIAN_NUMBER),
                getMotherDetailsColumn(SECOND_PHONE_NUMBER)};

        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(tableName, lookupColumns);
        queryBuilder.customJoin(
                " join " + queryProvider.getMotherDetailsTable() + " on " + queryProvider.getMotherDetailsTable() + "." + AppConstants.KeyConstants.BASE_ENTITY_ID + "=" + queryProvider.getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID +
                        " join " + queryProvider.getChildDetailsTable() + " on " + queryProvider.getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = " + queryProvider.getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID);
        String query = queryBuilder.mainCondition(getMainConditionString(entityMap));

        // Make the id distinct
        query = query.replace("ec_client.id as _id", "distinct(ec_client.id) as _id");

        return queryBuilder.Endquery(query);
    }
}
