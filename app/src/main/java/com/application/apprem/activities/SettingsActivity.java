package com.application.apprem.activities;

import android.os.Bundle;

import com.application.apprem.R;
import com.application.apprem.settings.SettingsFragment;
import com.application.apprem.utils.PreferenceUtil;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{
    public static final String KEY_SEVEN_DAYS_SETTING = "sevendays";
    public static final String KEY_SCHOOL_WEBSITE_SETTING = "schoolwebsite";

    public int loadedFragments = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {

        final Bundle args = pref.getExtras();

        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();

        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle(pref.getTitle());
        } catch (Exception ignore) {
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (loadedFragments == 0) {
            finish();
        } else {
            loadedFragments--;
            try {
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.settings);
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
