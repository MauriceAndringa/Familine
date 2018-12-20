package com.familine.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.View;

import com.core.utils.SharedPrefsHelper;
import com.familine.fragments.SettingsFragment;
import com.core.utils.Toaster;
import com.familine.R;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

/**
 * QuickBlox team
 */
public class SettingsActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MAX_VIDEO_START_BITRATE = 2000;
    private String bitrateStringKey;
    private String rolesKey;
    private SettingsFragment settingsFragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();

        // Display the fragment as the main content.
        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

        bitrateStringKey = getString(R.string.pref_startbitratevalue_key);
        rolesKey = getString(R.string.pref_roles_key);
    }

    private void initActionBar() {
        actionBar.setTitle(R.string.actionbar_title_settings);
    }

    @Override
    protected View getSnackbarAnchorView() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences =
                settingsFragment.getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences =
                settingsFragment.getPreferenceScreen().getSharedPreferences();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(bitrateStringKey)) {
            int bitrateValue = sharedPreferences.getInt(bitrateStringKey, Integer.parseInt(
                    "0"));
            if (bitrateValue == 0){
                setDefaultstartingBitrate(sharedPreferences);
                return;
            }
            int startBitrate = bitrateValue;
            if (startBitrate > MAX_VIDEO_START_BITRATE){
                Toaster.longToast("Max value is:" + MAX_VIDEO_START_BITRATE);
                setDefaultstartingBitrate(sharedPreferences);
            }
        }

        if (key.equals(rolesKey)) {
            Preference rolePreference = settingsFragment.findPreference(key);

            if (rolePreference instanceof ListPreference) {
                ListPreference listPref = (ListPreference) rolePreference;
                String currValue = listPref.getValue();
                String newExternalId = currValue.equals("Helped") ? "0" : "1";

                QBUser currentUser = SharedPrefsHelper.getInstance().getQbUser();
                currentUser.setExternalId(newExternalId);
                SharedPrefsHelper.getInstance().saveQbUser(currentUser);

                QBUser user = new QBUser();
                user.setId(currentUser.getId());
                user.setTags(currentUser.getTags());
                user.setExternalId(currentUser.getExternalId());

                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        rolePreference.setSummary(listPref.getEntry());
                        Toaster.shortToast("User role successfully updated");
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toaster.shortToast("User role could not be updated");
                    }
                });
            }
        }
    }

    private void setDefaultstartingBitrate(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(bitrateStringKey,
                Integer.parseInt("0"));
        editor.apply();
        updateSummary(sharedPreferences, bitrateStringKey);
    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {
        Preference updatedPref = settingsFragment.findPreference(key);
        // Set summary to be the user-description for the selected value
        if (updatedPref instanceof EditTextPreference) {
            ((EditTextPreference) updatedPref).setText(sharedPreferences.getString(key, ""));
        } else {
            updatedPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
