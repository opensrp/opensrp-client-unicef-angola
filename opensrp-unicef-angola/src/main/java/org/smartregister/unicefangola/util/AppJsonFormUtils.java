package org.smartregister.unicefangola.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class AppJsonFormUtils extends ChildJsonFormUtils {

    public static String updateJsonFormWithClientDetails(Context context, Map<String, String> childDetails, List<String> nonEditableFields) {

        try {
            JSONObject birthRegistrationForm = FormUtils.getInstance(context)
                    .getFormJson(Utils.metadata().childRegister.formName);
            updateRegistrationEventType(birthRegistrationForm);
            ChildJsonFormUtils.addRegistrationFormLocationHierarchyQuestions(birthRegistrationForm);

            birthRegistrationForm.put(ChildJsonFormUtils.ENTITY_ID, childDetails.get(Constants.KEY.BASE_ENTITY_ID));
            birthRegistrationForm.put(ChildJsonFormUtils.RELATIONAL_ID, childDetails.get(RELATIONAL_ID));
            birthRegistrationForm.put(AppConstants.KeyConstants.FATHER_RELATIONAL_ID, childDetails.get(AppConstants.KeyConstants.FATHER_RELATIONAL_ID));

            birthRegistrationForm.put(ChildJsonFormUtils.CURRENT_ZEIR_ID,
                    Utils.getValue(childDetails, AppConstants.KeyConstants.ZEIR_ID, true).replace("-", ""));
            birthRegistrationForm.put(ChildJsonFormUtils.CURRENT_OPENSRP_ID,
                    Utils.getValue(childDetails, Constants.JSON_FORM_KEY.UNIQUE_ID, false));

            JSONObject metadata = birthRegistrationForm.getJSONObject(ChildJsonFormUtils.METADATA);
            metadata.put(ChildJsonFormUtils.ENCOUNTER_LOCATION, ChildLibrary.getInstance()
                    .getLocationPickerView(context).getSelectedItem());

            //inject zeir id into the birthRegistrationForm
            JSONObject stepOne = birthRegistrationForm.getJSONObject(ChildJsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(ChildJsonFormUtils.FIELDS);
            updateFormDetailsForEdit(childDetails, jsonArray, nonEditableFields);
            return birthRegistrationForm.toString();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e); Timber.e(e, "AppChildJsonFormUtils --> getMetadataForEditForm");
        }

        return "";
    }

    private static void updateFormDetailsForEdit(Map<String, String> childDetails, JSONArray jsonArray, List<String> nonEditableFields)
            throws JSONException {
        String prefix;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            prefix = getPrefix(jsonObject);

            if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.CONSENT)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, "yes");
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(Constants.KEY.PHOTO)) {
                processPhoto(childDetails.get(Constants.KEY.BASE_ENTITY_ID), jsonObject);
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.CAREGIVER_BIRTHDATE_UNKNOWN)) {
                getDobUnknown(childDetails, jsonObject);
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.AGE)) {
                processAge(Utils.getValue(childDetails, AppConstants.KeyConstants.DOB, false), jsonObject);
            } else if (jsonObject.getString(JsonFormConstants.TYPE).equalsIgnoreCase(JsonFormConstants.DATE_PICKER)) {
                processDate(childDetails, prefix, jsonObject);
            } else if (jsonObject.getString(ChildJsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(ChildJsonFormUtils.PERSON_INDENTIFIER)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, Utils.getValue(childDetails,
                        jsonObject.getString(ChildJsonFormUtils.OPENMRS_ENTITY_ID).toLowerCase(), true).replace("-", ""));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.MIDDLE_NAME)) {
                String middleName = Utils.getValue(childDetails, AppConstants.KeyConstants.MIDDLE_NAME, true);
                jsonObject.put(ChildJsonFormUtils.VALUE, middleName);
            } else if (jsonObject.has(JsonFormConstants.TREE)) {
                processLocationTree(childDetails, jsonObject);
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.CAREGIVER_FIRST_NAME)) {
                String motherFirstName = Utils.getValue(childDetails, AppConstants.KeyConstants.MOTHER_FIRST_NAME, true);
                jsonObject.put(ChildJsonFormUtils.VALUE, motherFirstName);
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.CAREGIVER_LAST_NAME)) {
                String motherLastName = Utils.getValue(childDetails, AppConstants.KeyConstants.MOTHER_LAST_NAME, true);
                jsonObject.put(ChildJsonFormUtils.VALUE, motherLastName);
//            } else if (jsonObject.getString(ChildJsonFormUtils.KeyConstants).equalsIgnoreCase(AppConstants.KeyConstants.CAREGIVER_MIDDLE_NAME)) {
//                String motherMiddleName = Utils.getValue(childDetails, AppConstants.KeyConstants.CAREGIVER_MIDDLE_NAME, true);
//                jsonObject.put(ChildJsonFormUtils.VALUE, motherMiddleName);
//            } else if (jsonObject.getString(ChildJsonFormUtils.KeyConstants).equalsIgnoreCase(AppConstants.KeyConstants.FATHER_FIRST_NAME)) {
//                String fatherFirstName = Utils.getValue(childDetails, AppConstants.KeyConstants.FATHER_FIRST_NAME, true);
//                jsonObject.put(ChildJsonFormUtils.VALUE, fatherFirstName);
//            } else if (jsonObject.getString(ChildJsonFormUtils.KeyConstants).equalsIgnoreCase(AppConstants.KeyConstants.FATHER_LAST_NAME)) {
//                String fatherLastName = Utils.getValue(childDetails, AppConstants.KeyConstants.FATHER_LAST_NAME, true);
//                jsonObject.put(ChildJsonFormUtils.VALUE, fatherLastName);
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.SURNAME)) {
                String childLastName = Utils.getValue(childDetails, AppConstants.KeyConstants.LAST_NAME, true);
                jsonObject.put(ChildJsonFormUtils.VALUE, childLastName);
//            } else if (jsonObject.getString(ChildJsonFormUtils.KeyConstants).equalsIgnoreCase("mother_guardian_number")) {
//                String motherPhoneNumber = Utils.getValue(childDetails, "mother_phone_number", true);
//                jsonObject.put(ChildJsonFormUtils.VALUE, motherPhoneNumber);
//            } else if (jsonObject.getString(ChildJsonFormUtils.KeyConstants).equalsIgnoreCase(AppConstants.KeyConstants.FATHER_PHONE)) {
//                String fatherPhoneNumber = Utils.getValue(childDetails, "father_phone_number", true);
//                jsonObject.put(ChildJsonFormUtils.VALUE, fatherPhoneNumber);
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.SECOND_PHONE_NUMBER)) {
                String secondPhone = Utils.getValue(childDetails, "mother_second_phone_number", true);
                jsonObject.put(ChildJsonFormUtils.VALUE, secondPhone);
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase("Sex")) {
                jsonObject.put(ChildJsonFormUtils.VALUE, childDetails.get(ChildJsonFormUtils.GENDER));
            } else if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.BIRTH_WEIGHT)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, childDetails.get(AppConstants.KeyConstants.BIRTH_WEIGHT.toLowerCase()));
            } else {
                jsonObject.put(ChildJsonFormUtils.VALUE, childDetails.get(jsonObject.getString(ChildJsonFormUtils.KEY)));
            }
            jsonObject.put(ChildJsonFormUtils.READ_ONLY, nonEditableFields.contains(jsonObject.getString(ChildJsonFormUtils.KEY)));
        }
    }

    private static void getDobUnknown(Map<String, String> childDetails, JSONObject jsonObject) throws JSONException {
        JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
        optionsObject.put(ChildJsonFormUtils.VALUE,
                Utils.getValue(childDetails, AppConstants.KeyConstants.DOB_UNKNOWN, false));
    }

    @NotNull
    private static String getPrefix(JSONObject jsonObject) throws JSONException {
        String prefix = "";
        if (jsonObject.has(ChildJsonFormUtils.ENTITY_ID)) {
            String entityId = jsonObject.getString(ChildJsonFormUtils.ENTITY_ID);
            if (!TextUtils.isEmpty(entityId) && entityId.equalsIgnoreCase(Constants.KEY.MOTHER))
                prefix = "mother_";
        }
        return prefix;
    }

    private static void processLocationTree(Map<String, String> childDetails, JSONObject jsonObject) throws JSONException {
        updateHomeFacilityHierarchy(childDetails, jsonObject);
    }

    private static void updateHomeFacilityHierarchy(Map<String, String> childDetails, JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString(ChildJsonFormUtils.KEY).equalsIgnoreCase(AppConstants.KeyConstants.HOME_FACILITY)) {
            List<String> homeFacilityHierarchy = LocationHelper.getInstance().getOpenMrsLocationHierarchy(
                    Utils.getValue(childDetails, AppConstants.KeyConstants.HOME_FACILITY, false), false);
            String homeFacilityHierarchyString = AssetHandler
                    .javaToJsonString(homeFacilityHierarchy, new TypeToken<List<String>>() {
                    }.getType());
            ArrayList<String> allLevels = AppUtils.getHealthFacilityLevels();
            List<FormLocation> entireTree = LocationHelper.getInstance().generateLocationHierarchyTree(true, allLevels);
            String entireTreeString = AssetHandler.javaToJsonString(entireTree, new TypeToken<List<FormLocation>>() {
            }.getType());
            if (StringUtils.isNotBlank(homeFacilityHierarchyString)) {
                jsonObject.put(ChildJsonFormUtils.VALUE, homeFacilityHierarchyString);
                jsonObject.put(JsonFormConstants.TREE, new JSONArray(entireTreeString));
            }
        }
    }

    private static void updateRegistrationEventType(JSONObject form) throws JSONException {
        if (form.has(ChildJsonFormUtils.ENCOUNTER_TYPE) && form.getString(ChildJsonFormUtils.ENCOUNTER_TYPE)
                .equals(Constants.EventType.BITRH_REGISTRATION)) {
            form.put(ChildJsonFormUtils.ENCOUNTER_TYPE, Constants.EventType.UPDATE_BITRH_REGISTRATION);
        }

        if (form.has(ChildJsonFormUtils.STEP1) && form.getJSONObject(ChildJsonFormUtils.STEP1).has(AppConstants.KeyConstants.TITLE) && form.getJSONObject(ChildJsonFormUtils.STEP1).getString(AppConstants.KeyConstants.TITLE)
                .equals(Constants.EventType.BITRH_REGISTRATION)) {
            form.getJSONObject(ChildJsonFormUtils.STEP1).put(AppConstants.KeyConstants.TITLE, AppConstants.FormTitleUtil.UPDATE_CHILD_FORM);
        }

        //Update father details if it exists or create a new one
        if (form.has(Constants.KEY.MOTHER)) {
            form.getJSONObject(Constants.KEY.MOTHER).put(ENCOUNTER_TYPE, Constants.EventType.UPDATE_MOTHER_DETAILS);
        }
    }
}
