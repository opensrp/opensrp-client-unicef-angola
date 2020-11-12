package org.smartregister.unicefangola.activity;


import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Utils;
import org.smartregister.unicefangola.BaseActivityUnitTest;


public class ChildRegisterActivityTest extends BaseActivityUnitTest {

    private ChildRegisterActivity childRegisterActivity;
    private ActivityController<ChildRegisterActivity> controller;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        controller = Robolectric.buildActivity(ChildRegisterActivity.class).create().start();
        childRegisterActivity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return childRegisterActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @Test
    public void startJsonFormActivityTest() throws JSONException {
        JSONObject jsonForm  = new JSONObject("{}");

        ChildRegisterActivity spyActivity = Mockito.spy(childRegisterActivity);
        spyActivity.startFormActivity(jsonForm);
        assertActivityStarted(spyActivity, new ChildFormActivity());
    }

}
