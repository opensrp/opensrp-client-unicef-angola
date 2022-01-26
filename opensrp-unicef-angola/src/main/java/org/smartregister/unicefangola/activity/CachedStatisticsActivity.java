package org.smartregister.unicefangola.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vijay.jsonwizard.activities.MultiLanguageActivity;

import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.unicefangola.R;
import org.smartregister.unicefangola.presenter.CachedStatisticPresenter;
import org.smartregister.view.contract.StatsFragmentContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;

import static org.smartregister.unicefangola.interactor.CachedStatisticsInteractor.CachedStatsInfo.CACHED_HEIGHTS;
import static org.smartregister.unicefangola.interactor.CachedStatisticsInteractor.CachedStatsInfo.CACHED_RECURRING_SERVICE_RECORDS;
import static org.smartregister.unicefangola.interactor.CachedStatisticsInteractor.CachedStatsInfo.CACHED_VACCINES;
import static org.smartregister.unicefangola.interactor.CachedStatisticsInteractor.CachedStatsInfo.CACHED_WEIGHTS;


public class CachedStatisticsActivity extends MultiLanguageActivity implements StatsFragmentContract.View {

    private CachedStatisticPresenter presenter;
    private TextView tvCachedVaccines;
    private TextView tvCachedRecurrentServiceRecords;
    private TextView tvCachedWeights;
    private TextView tvCachedheights;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cached_statistics);
        presenter = new CachedStatisticPresenter(this);
        initializeViews();
    }



    private void initializeViews() {
        tvCachedVaccines = findViewById(R.id.cached_vaccines);
        tvCachedRecurrentServiceRecords = findViewById(R.id.cached_recurring_service_records);
        tvCachedheights = findViewById(R.id.cached_weights);
        tvCachedWeights = findViewById(R.id.cached_heights);


        Button btnRefreshStats = findViewById(R.id.refresh_button);
        btnRefreshStats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.fetchSyncInfo();
            }
        });

        presenter.fetchSyncInfo();
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void refreshECSyncInfo(Map<String, Integer> syncInfoMap) {
      tvCachedVaccines.setText(syncInfoMap.get(CACHED_VACCINES)+"");
      tvCachedRecurrentServiceRecords.setText(syncInfoMap.get(CACHED_RECURRING_SERVICE_RECORDS)+"");
      tvCachedWeights.setText(syncInfoMap.get(CACHED_WEIGHTS)+"");
      tvCachedheights.setText(syncInfoMap.get(CACHED_HEIGHTS)+"");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}