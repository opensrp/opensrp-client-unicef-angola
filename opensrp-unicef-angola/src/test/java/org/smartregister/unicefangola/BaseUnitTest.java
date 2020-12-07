package org.smartregister.unicefangola;

import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.unicefangola.shadow.ShadowAssetHandlerUtils;

@RunWith (RobolectricTestRunner.class)
@Config (application = TestUnicefAngolaApplication.class, sdk = Build.VERSION_CODES.P, shadows = {ShadowAssetHandlerUtils.class})
public abstract class BaseUnitTest {
    protected static final String DUMMY_USERNAME = "myusername";
    protected static final String DUMMY_PASSWORD = "mypassword";
}
