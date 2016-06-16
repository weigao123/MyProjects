package com.lesports.bike.settings.ui;

import android.app.Fragment;
import android.os.Bundle;

import com.lesports.bike.settings.R;

public class MainActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
    }
}
