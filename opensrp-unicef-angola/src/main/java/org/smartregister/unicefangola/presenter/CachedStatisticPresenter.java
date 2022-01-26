package org.smartregister.unicefangola.presenter;

import org.smartregister.unicefangola.interactor.CachedStatisticsInteractor;
import org.smartregister.view.contract.StatsFragmentContract;
import org.smartregister.view.interactor.StatsFragmentInteractor;

import java.util.Map;

public class CachedStatisticPresenter implements StatsFragmentContract.Presenter {

    private StatsFragmentContract.Interactor interactor;
    private StatsFragmentContract.View view;

    public CachedStatisticPresenter(StatsFragmentContract.View view) {
        this.view = view;
        this.interactor = new CachedStatisticsInteractor(this);
    }

    @Override
    public void onECSyncInfoFetched(Map<String, Integer> syncInfoMap) {
        view.refreshECSyncInfo(syncInfoMap);
    }

    @Override
    public void fetchSyncInfo() {
        interactor.fetchECSyncInfo();
    }
}
