package org.smartregister.unicefangola.activity;

import android.widget.ListView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.smartregister.unicefangola.BaseRobolectricTest;
import org.smartregister.unicefangola.R;

public class ReportRegisterActivityTest extends BaseRobolectricTest {

    private ReportRegisterActivity reportRegisterActivity;

    @Before
    public void setUp() {
        reportRegisterActivity = Robolectric.setupActivity(ReportRegisterActivity.class);
    }

    @Test
    public void testThatActivityStartedCorrectly() {
        Assert.assertEquals(R.layout.activity_report_register, reportRegisterActivity.getContentView());
        ListView listView = reportRegisterActivity.findViewById(R.id.lv_reportRegister_groupings);
        listView.performClick();
        Assert.assertNotNull(listView);

    }
}