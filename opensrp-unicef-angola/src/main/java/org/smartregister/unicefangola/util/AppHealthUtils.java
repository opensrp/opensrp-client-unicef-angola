package org.smartregister.unicefangola.util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import org.smartregister.AllConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.unicefangola.BuildConfig;
import org.smartregister.unicefangola.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by ndegwamartin on 26/06/2021.
 */
public class AppHealthUtils {

    /**
     * @param view The view we want to bind the event that launches the app heath action selection dialog
     *             The the selection alert dialog will show if you long click/long press the view
     */
    public AppHealthUtils(View view) {
        view.setOnLongClickListener(v -> {
            AppHealthUtils.showAppHealthSelectDialog(view.getContext());
            return false;
        });
    }

    public static void showAppHealthSelectDialog(Context context) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item);
        adapter.add(context.getString(R.string.download_database));
        adapter.add(context.getString(R.string.view_sync_stats));

        new AlertDialog.Builder(context)
                .setAdapter(adapter, (dialog, which) -> {

                    switch (which) {
                        case 0:
                            Utils.showToast(context, context.getString(R.string.export_db_notification));
                            Executor executor = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());
                            executor.execute(() -> {

                                Utils.copyDatabase(AllConstants.DATABASE_NAME, getCurrentTimeStamp(), context);
                                refreshFileSystem(context);

                                handler.post(() -> Utils.showToast(context, context.getString(R.string.transfer_successful)));
                            });

                            break;
                        case 1:
                            Utils.showToast(context, "TO DO implement " + adapter.getItem(which));
                            break;
                        default:
                            break;

                    }

                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public static String getCurrentTimeStamp() {
        String currentTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        return new StringBuilder(BuildConfig.APPLICATION_ID).append('_').append(currentTimeStamp).append(".db").toString();
    }


    public static void refreshFileSystem(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                    + Environment.getExternalStorageDirectory())));

        } else {
            MediaScannerConnection.scanFile(context, new String[]{Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()}, null, (path, uri) -> {
                //Overriden: Do nothing
            });
        }
    }
}
