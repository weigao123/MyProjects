package com.lesports.bike.settings.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.ActivityUtils;
import com.lesports.bike.settings.utils.L;

/**
 * Created by gaowei3 on 2016/5/16.
 */
public class DetailActivity extends BaseActivity {

    private Class<?> mCurrent;
    private static final Class mClass[] = {PttFragment.class, AudioFragment.class, DataUsageFragment.class,
                              WifiFragment.class, DisplayFragment.class, BluetoothFragment.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
    }

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        if (intent.hasExtra("function_index")) {
            mCurrent = mClass[intent.getIntExtra("function_index", 0)];
        } else {
            mCurrent = (Class<?>) intent.getSerializableExtra(BaseFragment.FRAGMENT_CLASS);
        }
        return Fragment.instantiate(this, mCurrent.getName(), null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        L.d("intent");
        if (intent.hasExtra("function_index")) {
            Class<?> target = mClass[intent.getIntExtra("function_index", 0)];
            if (!mCurrent.equals(target)) {
                ActivityUtils.startFragmentActivity(this, mClass[intent.getIntExtra("function_index", 0)]);
            }
        }

//        Class<?> target = (Class<?>)intent.getSerializableExtra(BaseFragment.FRAGMENT_CLASS);
//        if (!mCurrent.equals(target)) {
//            changeFragment(Fragment.instantiate(this, target.getName(), null));
//        }
    }
}
