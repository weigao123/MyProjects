package com.lesports.bike.settings.ui;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.ui.BaseActivity;

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
