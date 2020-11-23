package com.application.apprem.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.application.apprem.R;
import com.application.apprem.settings.TimeSettingsFragment;
import com.application.apprem.utils.PreferenceUtil;

public class TimeSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_setting);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, new TimeSettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(this, SummaryActivity.class));
        PreferenceUtil.setStartActivityShown(this, true);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
