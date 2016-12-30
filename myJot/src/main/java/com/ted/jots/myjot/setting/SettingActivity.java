package com.ted.jots.myjot.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ted.jots.myjot.R;

/**
 * Created by ted on 2016/12/24.
 * in com.ted.jots.myjot.setting
 */

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(null != getSupportActionBar())
            getSupportActionBar().setTitle(R.string.setting);
        if (null == savedInstanceState)
            getSupportFragmentManager().beginTransaction().add(
                    R.id.setting_fragment_container, new SettingFragment()).commit();
    }

}
