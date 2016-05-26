package com.lesports.bike.settings.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.lesports.bike.settings.R;

/**
 * Created by gaowei3 on 2016/5/16.
 */
public class DetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
    }

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        Class<?> target = (Class<?>)intent.getSerializableExtra(BaseFragment.FRAGMENT_CLASS);
        return Fragment.instantiate(this, target.getName(), null);
    }
}
