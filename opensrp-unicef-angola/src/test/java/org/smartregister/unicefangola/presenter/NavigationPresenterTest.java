package org.smartregister.unicefangola.presenter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.unicefangola.BaseUnitTest;
import org.smartregister.unicefangola.contract.NavigationContract;
import org.smartregister.unicefangola.util.AppConstants;

import java.util.HashMap;

public class NavigationPresenterTest extends BaseUnitTest {

    private NavigationPresenter navigationPresenter;

    @Mock
    private NavigationContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        navigationPresenter = new NavigationPresenter(view);
    }

    @Test
    public void initialize() {
        HashMap<String, String> tableMap = new HashMap<>();
        ReflectionHelpers.setField(navigationPresenter, "tableMap", tableMap);
        ReflectionHelpers.callInstanceMethod(navigationPresenter, "initialize");
        Assert.assertEquals(4, tableMap.size());
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenuConstants.ALL_CLIENTS));
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenuConstants.CHILD_CLIENTS));
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenuConstants.ANC));
        Assert.assertTrue(tableMap.containsKey(AppConstants.DrawerMenuConstants.ANC_CLIENTS));
    }

    @Test
    public void getNavigationView() {
        Assert.assertNotNull(navigationPresenter.getNavigationView());
    }

    @After
    public void tearDown(){
        navigationPresenter = null;
    }
}