package org.smartregister.unicefangola.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.child.job.ArchiveClientsJob;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.RecurringServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.PullUniqueIdsServiceJob;
import org.smartregister.job.SyncAllLocationsServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;
import org.smartregister.unicefangola.BaseRobolectricTest;
import org.smartregister.unicefangola.shadow.ShadowBaseJobUtils;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 05-03-2020.
 */
public class LoginInteractorTest extends BaseRobolectricTest {

    private LoginInteractor loginInteractor;

    @Before
    public void setUp() {
        loginInteractor = new LoginInteractor(Mockito.mock(BaseLoginContract.Presenter.class));
    }

    @Test
    public void scheduleJobsPeriodically() {
        loginInteractor.scheduleJobsPeriodically();

        Assert.assertTrue(ShadowBaseJobUtils.getShadowHelper().isCalled(ShadowBaseJobUtils.scheduleJobMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJobUtils.getShadowHelper().getMethodCalls(ShadowBaseJobUtils.scheduleJobMN);
        assertEquals(10, methodCalls.size());
        assertEquals(VaccineServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(RecurringServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(WeightIntentServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(HeightIntentServiceJob.TAG, methodCalls.get(3).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(4).get(0));
        assertEquals(SyncServiceJob.TAG, methodCalls.get(5).get(0));
        assertEquals(SyncAllLocationsServiceJob.TAG, methodCalls.get(6).get(0));
        assertEquals(PullUniqueIdsServiceJob.TAG, methodCalls.get(7).get(0));
        assertEquals(ImageUploadServiceJob.TAG, methodCalls.get(8).get(0));
        assertEquals(15L, methodCalls.get(8).get(1));
        assertEquals(RecurringIndicatorGeneratingJob.TAG, methodCalls.get(9).get(0));
    }

    @Test
    @Ignore("Fix robolectric configuration")
    public void scheduleJobsImmediatelyShouldCallEachJobToScheduleImmediateExecution() {
        loginInteractor.scheduleJobsImmediately();

        Assert.assertTrue(ShadowBaseJobUtils.getShadowHelper().isCalled(ShadowBaseJobUtils.scheduleJobImmediatelyMN));
        HashMap<Integer, ArrayList<Object>> methodCalls = ShadowBaseJobUtils.getShadowHelper().getMethodCalls(ShadowBaseJobUtils.scheduleJobImmediatelyMN);
        assertEquals(6, methodCalls.size());
        assertEquals(SyncServiceJob.TAG, methodCalls.get(0).get(0));
        assertEquals(SyncAllLocationsServiceJob.TAG, methodCalls.get(1).get(0));
        assertEquals(PullUniqueIdsServiceJob.TAG, methodCalls.get(2).get(0));
        assertEquals(ZScoreRefreshIntentServiceJob.TAG, methodCalls.get(3).get(0));
        assertEquals(ImageUploadServiceJob.TAG, methodCalls.get(4).get(0));
        assertEquals(ArchiveClientsJob.TAG, methodCalls.get(5).get(0));
    }
}