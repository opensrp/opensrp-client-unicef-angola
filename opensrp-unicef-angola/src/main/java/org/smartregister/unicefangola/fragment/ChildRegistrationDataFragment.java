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
            AppConstants.KeyConstants.FIRST_NAME,
            AppConstants.KeyConstants.SURNAME,
            "zeir_id",
            AppConstants.KeyConstants.SEX,
            AppConstants.KeyConstants.DATE_BIRTH,
            AppConstants.KeyConstants.CAREGIVER_FIRST_NAME,
            AppConstants.KeyConstants.CAREGIVER_LAST_NAME,
            AppConstants.KeyConstants.CAREGIVER_FIRST_PHONE_NUMBER,
            AppConstants.KeyConstants.CAREGIVER_FIRST_PHONE_NUMBER_OWNER,
            AppConstants.KeyConstants.CAREGIVER_SECOND_PHONE_NUMBER,
            AppConstants.KeyConstants.CAREGIVER_SECOND_PHONE_NUMBER_OWNER,
            AppConstants.KeyConstants.CAREGIVER_ADDRESS
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
                put(AppConstants.KeyConstants.SURNAME, R.string.last_name);
                put(AppConstants.KeyConstants.DATE_BIRTH, R.string.dob);
                put(AppConstants.KeyConstants.CAREGIVER_FIRST_NAME, R.string.mother_caregiver_first_name);
                put(AppConstants.KeyConstants.CAREGIVER_LAST_NAME, R.string.mother_caregiver_last_name);
                put(AppConstants.KeyConstants.CAREGIVER_FIRST_PHONE_NUMBER, R.string.primary_number);
                put(AppConstants.KeyConstants.CAREGIVER_FIRST_PHONE_NUMBER_OWNER, R.string.primary_number_belongs_to);
                put(AppConstants.KeyConstants.CAREGIVER_SECOND_PHONE_NUMBER, R.string.second_number);
                put(AppConstants.KeyConstants.CAREGIVER_SECOND_PHONE_NUMBER_OWNER, R.string.second_number_belongs_to);
                put(AppConstants.KeyConstants.CAREGIVER_ADDRESS, R.string.address);
            }
        };
        return super.getDataRowLabelResourceIds();
    }
}
