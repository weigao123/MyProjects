package com.lesports.bike.settings.ui;

import android.app.Fragment;
import android.content.Context;
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


//        View mView = LayoutInflater.from(this).inflate(R.layout.activity_translucent, null);
//
//        Context mContext = getApplicationContext();
//        WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
//        int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
//        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
//        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        params.alpha = 80;
//        // 不设置这个弹出框的透明遮罩显示为黑色
//        params.format = 1;
//        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
//        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
//        // 不设置这个flag的话，home页的划屏会有问题
//
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//
//        params.gravity = Gravity.CENTER;
//        mWindowManager.addView(mView, params);

    }
}
