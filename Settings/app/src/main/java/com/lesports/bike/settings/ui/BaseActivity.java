package com.lesports.bike.settings.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.lesports.bike.settings.R;


/**
 * Created by gaowei3 on 2016/4/29.
 */
public abstract class BaseActivity extends Activity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContent);
        if (fragment == null) {
            fragment = createFragment();
            if (fragment != null) {
                fragmentManager.beginTransaction().add(R.id.fragmentContent, fragment).commit();
            }
        }
    }


}
