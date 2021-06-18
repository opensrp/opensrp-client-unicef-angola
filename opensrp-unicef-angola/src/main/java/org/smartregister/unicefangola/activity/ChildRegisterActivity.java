package org.smartregister.unicefangola.activity;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.event.ClientDirtyFlagEvent;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.unicefangola.R;
import org.smartregister.unicefangola.fragment.AdvancedSearchFragment;
import org.smartregister.unicefangola.fragment.ChildRegisterFragment;
import org.smartregister.unicefangola.presenter.AppChildRegisterPresenter;
import org.smartregister.unicefangola.util.AppConstants;
import org.smartregister.unicefangola.util.AppUtils;
import org.smartregister.unicefangola.view.NavDrawerActivity;
import org.smartregister.unicefangola.view.NavigationMenu;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.lang.ref.WeakReference;

public class ChildRegisterActivity extends BaseChildRegisterActivity implements NavDrawerActivity {

    private NavigationMenu navigationMenu;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = AppUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(AppUtils.setAppLocale(base, lang));
    }

    @Override
    protected Fragment[] getOtherFragments() {
        ADVANCED_SEARCH_POSITION = 1;
        Fragment[] fragments = new Fragment[1];
        fragments[ADVANCED_SEARCH_POSITION - 1] = new WeakReference<>(new AdvancedSearchFragment()).get();
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public String getRegistrationForm() {
        return AppConstants.JsonForm.CHILD_ENROLLMENT;
    }

    @Override
    public void startNFCCardScanner() {
        // TODO
    }
    @Subscribe
    public void processClient(ClientDirtyFlagEvent clientDirtyFlagEvent){
        // TODO
    }

    @Override
    protected void initializePresenter() {
        presenter = new AppChildRegisterPresenter(this, new BaseChildRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        WeakReference<ChildRegisterFragment> weakReference = new WeakReference<>(new ChildRegisterFragment());
        return weakReference.get();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        createDrawer();
        EventBus.getDefault().register(this);
    }

    private void createDrawer() {
        WeakReference<ChildRegisterActivity> weakReference = new WeakReference<>(this);
        navigationMenu = NavigationMenu.getInstance(weakReference.get());
    }

    @Override
    public void openDrawer() {
        if (navigationMenu != null) {
            navigationMenu.openDrawer();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        /*if (jsonForm.has(AppConstants.KeyConstants.ENCOUNTER_TYPE) && jsonForm.optString(AppConstants.KeyConstants.ENCOUNTER_TYPE).equals(
                AppConstants.KeyConstants.BIRTH_REGISTRATION)) {
            ChildJsonFormUtils.addRegistrationFormLocationHierarchyQuestions(jsonForm);
        }*/

        Form form = new Form();
        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        Intent intent = new Intent(this, Utils.metadata().childFormActivity);
        intent.putExtra(Constants.INTENT_KEY.JSON, jsonForm.toString());
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(JsonFormConstants.PERFORM_FORM_TRANSLATION,  true);
        startActivityForResult(intent, ChildJsonFormUtils.REQUEST_CODE_GET_JSON);
    }


    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        MenuItem clients = bottomNavigationView.getMenu().findItem(R.id.action_clients);
        if (clients != null) {
            clients.setTitle(getString(R.string.header_children));
        }
        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
    }
}
