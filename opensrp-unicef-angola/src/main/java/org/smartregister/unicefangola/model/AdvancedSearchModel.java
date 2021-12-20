package org.smartregister.unicefangola.model;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.model.ChildMotherDetailModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.domain.Response;
import org.smartregister.unicefangola.util.AppConstants;

import java.util.List;
import java.util.Map;

import static org.smartregister.child.util.Constants.Client.SYSTEM_OF_REGISTRATION;
import static org.smartregister.child.util.Constants.KEY.FATHER_RELATIONAL_ID;
import static org.smartregister.child.util.Constants.KEY.RELATIONALID;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.DOB;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.FIRST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.GENDER;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.ID;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.INACTIVE;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.LAST_NAME;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.LOST_TO_FOLLOW_UP;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.RELATIONAL_ID;
import static org.smartregister.unicefangola.util.AppConstants.KeyConstants.ZEIR_ID;
import static org.smartregister.unicefangola.util.TableUtil.getAllClientColumn;
import static org.smartregister.unicefangola.util.TableUtil.getChildDetailsColumn;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class AdvancedSearchModel extends BaseChildAdvancedSearchModel {

    @Override
    public Map<String, String> createEditMap(Map<String, String> editMap) {
        return editMap;
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {

        String[] columns = new String[]{
                AppConstants.KeyConstants.ID_LOWER_CASE,
                RELATIONALID,
                RELATIONAL_ID,
                AppConstants.KeyConstants.FATHER_BASE_ENTITY_ID,
                FIRST_NAME,
                LAST_NAME,
                GENDER,
                DOB,
                ZEIR_ID,
                AppConstants.KeyConstants.MOTHER_FIRST_NAME,
                AppConstants.KeyConstants.MOTHER_LAST_NAME,
                INACTIVE,
                LOST_TO_FOLLOW_UP,
                SYSTEM_OF_REGISTRATION
        };

        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {
            List<ChildMotherDetailModel> ChildMotherDetailModels = getChildMotherDetailModels(response);
            for (ChildMotherDetailModel ChildMotherDetailModel : ChildMotherDetailModels) {
                matrixCursor.addRow(ChildMotherDetailModel.getColumnValuesFromJson());
            }
        }

        return matrixCursor;
    }

    @Override
    public String mainSelect(String mainCondition) {
        return "select " + StringUtils.join(getColumns(), ",") + " from " + DBConstants.RegisterTable.CHILD_DETAILS + " " +
                "join " + DBConstants.RegisterTable.MOTHER_DETAILS + " on " + DBConstants.RegisterTable.CHILD_DETAILS + "." + RELATIONAL_ID + " = " + DBConstants.RegisterTable.MOTHER_DETAILS + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + DBConstants.RegisterTable.CLIENT + " on " + DBConstants.RegisterTable.CLIENT + "." + Constants.KEY.BASE_ENTITY_ID + " = " + DBConstants.RegisterTable.CHILD_DETAILS + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + DBConstants.RegisterTable.CLIENT + " mother on mother." + Constants.KEY.BASE_ENTITY_ID + " = " + DBConstants.RegisterTable.MOTHER_DETAILS + "." + Constants.KEY.BASE_ENTITY_ID + " where " + mainCondition;
    }

    @NotNull
    @Override
    public String[] getColumns() {
        return new String[]{
                getAllClientColumn(ID) + "as _id",
                getChildDetailsColumn(RELATIONALID),
                getChildDetailsColumn(RELATIONAL_ID),
                getChildDetailsColumn(FATHER_RELATIONAL_ID),
                getAllClientColumn(FIRST_NAME),
                getAllClientColumn(LAST_NAME),
                getAllClientColumn(GENDER),
                getAllClientColumn(DOB),
                getAllClientColumn(ZEIR_ID),
                "mother.first_name                     as " + AppConstants.KeyConstants.MOTHER_FIRST_NAME,
                "mother.last_name                      as " + AppConstants.KeyConstants.MOTHER_LAST_NAME,
                getChildDetailsColumn(INACTIVE),
                getChildDetailsColumn(LOST_TO_FOLLOW_UP)
        };
    }
}
