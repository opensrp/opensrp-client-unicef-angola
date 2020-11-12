package org.smartregister.unicefangola.activity;

import android.app.Activity;
import android.os.Looper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.unicefangola.BaseActivityUnitTest;

import java.util.HashMap;
import java.util.Map;

import static org.robolectric.Shadows.shadowOf;

public class ChildDetailTabbedActivityTest extends BaseActivityUnitTest {

    private ChildDetailTabbedActivity  childDetailTabbedActivity;
    private ActivityController<ChildDetailTabbedActivity> controller;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        shadowOf(Looper.getMainLooper()).idle();

        controller = Robolectric.buildActivity(ChildDetailTabbedActivity.class);
        childDetailTabbedActivity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return childDetailTabbedActivity;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @Test
    public void constructChildNameTest() {
        Map<String, String> columnMaps = new HashMap<>();
        columnMaps.put("first_name", "first");
        columnMaps.put("last_name", "last");
        columnMaps.put("middle_name", "middle");

        CommonPersonObjectClient commonFtsObject = Mockito.mock(CommonPersonObjectClient.class);
        Mockito.doReturn(columnMaps).when(commonFtsObject).getColumnmaps();
        ReflectionHelpers.setField(childDetailTabbedActivity, "childDetails", commonFtsObject);

        Assert.assertEquals("First Middle Last", childDetailTabbedActivity.constructChildName());

        columnMaps.remove("middle_name");
        Assert.assertEquals("First Last", childDetailTabbedActivity.constructChildName());

    }
}
