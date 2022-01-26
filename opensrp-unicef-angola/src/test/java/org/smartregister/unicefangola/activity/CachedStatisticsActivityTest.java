package org.smartregister.unicefangola.activity;

import android.app.Activity;
import android.os.Looper;

import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.unicefangola.BaseActivityUnitTest;

import static org.robolectric.Shadows.shadowOf;

public class CachedStatisticsActivityTest  extends BaseActivityUnitTest {

    private CachedStatisticsActivity  cachedStatisticsActivity;
    private ActivityController<CachedStatisticsActivity> controller;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        shadowOf(Looper.getMainLooper()).idle();

        controller = Robolectric.buildActivity(CachedStatisticsActivity.class);
        cachedStatisticsActivity = controller.get();
    }

    @Override
    protected Activity getActivity() {
        return null;
    }

    @Override
    protected ActivityController<CachedStatisticsActivity> getActivityController() {
        return cachedStatisticsActivity;
    }
}
