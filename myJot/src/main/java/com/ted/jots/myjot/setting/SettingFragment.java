package com.ted.jots.myjot.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.ted.jots.myjot.R;
import com.ted.jots.myjot.config.ConfigManager;
import com.ted.jots.myjot.main.MainPresenter;

/**
 * Created by ted on 2016/12/26.
 * in com.ted.jots.myjot.setting
 */

public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Handler mUiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            return false;
        }
    });

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_pref);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.is_support_clipboard_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.listen_clipboard_time_key));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                if (getString(R.string.listen_clipboard_time_key).equals(key)) {
                    preference.setSummary(getString(R.string.listen_clipboard_des,
                            listPreference.getEntries()[prefIndex]));
                }
            }
        } else if (preference instanceof CheckBoxPreference) {
            boolean result = sharedPreferences.getBoolean(key, false);
            findPreference(getString(R.string.listen_clipboard_time_key)).setEnabled(result);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equalsIgnoreCase(getString(R.string.share_app_to_friend_key))) {
            MainPresenter.onShareApp(getActivity());
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment = null;
        if (preference instanceof ColorPickerPreference) {
            dialogFragment = ColorPickerPreDlgFragCompat.newInstance(preference.getKey());
        } else if (preference instanceof CommonDialogPreference) {
            if (getString(R.string.app_use_courses_key).equals(preference.getKey()))
                dialogFragment = SingleDialogFragment.newIns(preference);
            else dialogFragment = CommonDialogFragment.newIns(preference);
        }
        if (null != dialogFragment) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "PreferenceDialog");
        } else
            super.onDisplayPreferenceDialog(preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onDialogCall(String dialogPreKey, boolean isOkBtn) {
        if (dialogPreKey.equals(getString(R.string.reset_all_setting_key)) && isOkBtn) {
            refreshSetting();
            mUiHandler.sendEmptyMessageDelayed(0, 500);
        } else if (dialogPreKey.equals(getString(R.string.about_developer_key)) && isOkBtn) {
            MainPresenter.onTanksDeveloper(getActivity());
        }
    }

    private void refreshSetting() {
        ((ColorPickerPreference) findPreference(getString(R.string.edit_page_color_key))).setColor(ConfigManager.DEFAULT_ARGB_COLOR);
        ((ColorPickerPreference) findPreference(getString(R.string.font_color_key))).setColor(ConfigManager.DEFAULT_TEXT_COLOR);
        ((ListPreference) findPreference(getString(R.string.listen_clipboard_time_key))).setValueIndex(1);
        ((CheckBoxPreference) findPreference(getString(R.string.is_support_clipboard_key))).setChecked(true);
    }
}
