package org.smartregister.unicefangola.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.smartregister.Context;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.growthmonitoring.job.HeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.WeightIntentServiceJob;
import org.smartregister.growthmonitoring.job.ZScoreRefreshIntentServiceJob;
import org.smartregister.immunization.job.VaccineServiceJob;
import org.smartregister.job.ImageUploadServiceJob;
import org.smartregister.job.SyncServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.unicefangola.R;
import org.smartregister.unicefangola.contract.NavigationContract;
import org.smartregister.unicefangola.interactor.NavigationInteractor;
import org.smartregister.unicefangola.model.NavigationModel;
import org.smartregister.unicefangola.util.AppConstants;
import org.smartregister.unicefangola.util.DataCleanUpUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class NavigationPresenter implements NavigationContract.Presenter {

    private NavigationContract.Model mModel;
    private NavigationContract.Interactor mInteractor;
    private WeakReference<NavigationContract.View> mView;

    private HashMap<String, String> tableMap = new HashMap<>();

    public NavigationPresenter(NavigationContract.View view) {
        mView = new WeakReference<>(view);
        mInteractor = NavigationInteractor.getInstance();
        mModel = NavigationModel.getInstance();
        initialize();
    }

    private void initialize() {
        tableMap.put(AppConstants.DrawerMenuConstants.CHILD_CLIENTS, AppConstants.RegisterType.CHILD);
        tableMap.put(AppConstants.DrawerMenuConstants.ANC_CLIENTS, AppConstants.RegisterType.ANC);
        tableMap.put(AppConstants.DrawerMenuConstants.ANC, AppConstants.RegisterType.ANC);
        tableMap.put(AppConstants.DrawerMenuConstants.ALL_CLIENTS, AppConstants.RegisterType.OPD);
    }

    @Override
    public NavigationContract.View getNavigationView() {
        return mView.get();
    }

    @Override
    public void refreshLastSync() {
        // get last sync date
        getNavigationView().refreshLastSync(mInteractor.sync());
    }

    @Override
    public void displayCurrentUser() {
        getNavigationView().refreshCurrentUser(mModel.getCurrentUser());
    }

    @Override
    public void sync(Activity activity) {


        if (activity instanceof BaseChildRegisterActivity) {
            ((BaseChildRegisterActivity) activity).showProgressDialog(R.string.syncing);
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {

                DataCleanUpUtils.cleanUpDuplicateMotherIds();

            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e); Timber.e(e);
            }

            handler.post(() -> {

                SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);

                if (activity instanceof BaseChildRegisterActivity) {
                    ((BaseChildRegisterActivity) activity).hideProgressDialog();
                }
            });
        });


        ImageUploadServiceJob.scheduleJobImmediately(ImageUploadServiceJob.TAG);
        SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
        SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG);
        ZScoreRefreshIntentServiceJob.scheduleJobImmediately(ZScoreRefreshIntentServiceJob.TAG);
        WeightIntentServiceJob.scheduleJobImmediately(WeightIntentServiceJob.TAG);
        HeightIntentServiceJob.scheduleJobImmediately(HeightIntentServiceJob.TAG);
        VaccineServiceJob.scheduleJobImmediately(VaccineServiceJob.TAG);
    }

    @Override
    public String getLoggedInUserInitials() {

        try {
            AllSharedPreferences allSharedPreferences = Context.getInstance().allSharedPreferences();
            String preferredName = allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
            if (!TextUtils.isEmpty(preferredName)) {
                String[] initialsArray = preferredName.split(" ");
                String initials = "";
                if (initialsArray.length > 0) {
                    initials = initialsArray[0].substring(0, 1);
                    if (initialsArray.length > 1) {
                        initials = initials + initialsArray[1].substring(0, 1);
                    }
                }
                return initials.toUpperCase();
            }

        } catch (StringIndexOutOfBoundsException exception) {
            FirebaseCrashlytics.getInstance().recordException(exception); Timber.e(exception, "Error fetching initials");
        }

        return null;
    }
}
