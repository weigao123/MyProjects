package com.lesports.bike.settings.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.lesports.bike.settings.R;

/**
 * Created by gaowei3 on 2016/5/16.
 */
public class DetailActivity extends BaseActivity {

    private Class<?> mCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
    }

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        mCurrent = (Class<?>)intent.getSerializableExtra(BaseFragment.FRAGMENT_CLASS);
        return Fragment.instantiate(this, mCurrent.getName(), null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Class<?> target = (Class<?>)intent.getSerializableExtra(BaseFragment.FRAGMENT_CLASS);
        if (!mCurrent.equals(target)) {
            changeFragment(Fragment.instantiate(this, target.getName(), null));
        }
    }
}
