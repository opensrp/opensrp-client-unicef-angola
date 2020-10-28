package org.smartregister.unicefangola.fragment;

import android.os.Bundle;

import org.smartregister.child.domain.Field;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.unicefangola.R;
import org.smartregister.unicefangola.util.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-30.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {

    private final static List<String> requiredFieldsList = Arrays.asList(
            AppConstants.KEY.FIRST_NAME,
            AppConstants.KEY.SURNAME,
            "zeir_id",
            AppConstants.KEY.SEX,
            AppConstants.KEY.DATE_BIRTH,
            AppConstants.KEY.CAREGIVER_FIRST_NAME,
            AppConstants.KEY.CAREGIVER_LAST_NAME,
            AppConstants.KEY.CAREGIVER_FIRST_PHONE_NUMBER,
            AppConstants.KEY.CAREGIVER_FIRST_PHONE_NUMBER_OWNER,
            AppConstants.KEY.CAREGIVER_SECOND_PHONE_NUMBER,
            AppConstants.KEY.CAREGIVER_SECOND_PHONE_NUMBER_OWNER,
            AppConstants.KEY.CAREGIVER_ADDRESS
    );


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fieldNameAliasMap = new HashMap<String, String>() {
            {
                put("mother_guardian_number", "mother_phone_number");
                put("second_phone_number", "mother_second_phone_number");
                put("father_phone", "father_phone_number");
            }
        };
    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JsonForm.CHILD_ENROLLMENT;
    }

    @Override
    public List<Field> getFields() {
        ArrayList<Field> requiredFields = new ArrayList<>();
        List<Field> allFields = super.getFields();
        for (Field field : allFields) {
            if (requiredFieldsList.contains(field.getKey()))
                requiredFields.add(field);
        }
        return requiredFields;
    }

    @Override
    protected List<String> addUnFormattedNumberFields(String... key) {
        return Arrays.asList("mother_guardian_number", "second_phone_number");
    }

    @Override
    protected Map<String, String> getDataRowLabelResourceIds() {
        fieldNameResourceMap = new HashMap<String, Integer>() {
            {
                put(AppConstants.KEY.SURNAME, R.string.last_name);
                put(AppConstants.KEY.DATE_BIRTH, R.string.dob);
                put(AppConstants.KEY.CAREGIVER_FIRST_NAME, R.string.mother_caregiver_first_name);
                put(AppConstants.KEY.CAREGIVER_LAST_NAME, R.string.mother_caregiver_last_name);
                put(AppConstants.KEY.CAREGIVER_FIRST_PHONE_NUMBER, R.string.primary_number);
                put(AppConstants.KEY.CAREGIVER_FIRST_PHONE_NUMBER_OWNER, R.string.primary_number_belongs_to);
                put(AppConstants.KEY.CAREGIVER_SECOND_PHONE_NUMBER, R.string.second_number);
                put(AppConstants.KEY.CAREGIVER_SECOND_PHONE_NUMBER_OWNER, R.string.second_number_belongs_to);
                put(AppConstants.KEY.CAREGIVER_ADDRESS, R.string.address);
            }
        };
        return super.getDataRowLabelResourceIds();
    }
}
