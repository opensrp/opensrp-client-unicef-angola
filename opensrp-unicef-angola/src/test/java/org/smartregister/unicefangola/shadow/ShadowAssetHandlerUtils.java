package org.smartregister.unicefangola.shadow;

import android.content.Context;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.smartregister.util.AssetHandler;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-06
 */

@Implements(AssetHandler.class)
public class ShadowAssetHandlerUtils {

    @Implementation
    public static String readFileFromAssetsFolder(String fileName, Context context) {
        if ("vaccines.json".equals(fileName)) {
            return VaccineDataUtils.VACCINES_JSON;
        }

        return "";
    }
}
