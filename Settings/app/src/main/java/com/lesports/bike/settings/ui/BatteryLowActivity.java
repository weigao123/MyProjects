
package com.lesports.bike.settings.ui;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.R.id;

public class BatteryLowActivity extends Activity {
    private int batteryLevel;
    private boolean isLock;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        batteryLevel = getIntent().getIntExtra("batteryLevel", -1);
        isLock = getIntent().getBooleanExtra("isLock", false);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.battery_layout);
        initViews();

    }

    private void initViews() {

        TextView noLockView = (TextView) findViewById(R.id.battery_no_lcok_view);
        View lockView = findViewById(R.id.battery_lcok_layout);
        if (isLock) {
            noLockView.setVisibility(View.GONE);
            lockView.setVisibility(View.VISIBLE);
            TextView messageView = (TextView) findViewById(R.id.battery_lcok_text);
            TextView unLockView = (TextView) findViewById(R.id.battery_lcok_btn);
            if (batteryLevel == 15) {
                messageView.setText(getResources().getString(
                        R.string.battery_less_than_15));
            } else if (batteryLevel == 5) {
                messageView.setText(getResources().getString(
                        R.string.battery_less_than_5));
            } else if (batteryLevel == 1) {
                messageView.setText(getResources().getString(
                        R.string.battery_to_power_off));
            }
            Shader shader_gradient1 = new LinearGradient(0, 10, 0, 80,
                    getResources().getColor(R.color.gradient_start_color),
                    getResources().getColor(R.color.gradient_end_color),
                    Shader.TileMode.CLAMP);
            unLockView.getPaint().setShader(shader_gradient1);
            unLockView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });

        } else {
            noLockView.setVisibility(View.VISIBLE);
            lockView.setVisibility(View.GONE);
            Shader shader_gradient1 = new LinearGradient(0, 10, 0, 80,
                    getResources().getColor(R.color.gradient_start_color),
                    getResources().getColor(R.color.gradient_end_color),
                    Shader.TileMode.CLAMP);
            noLockView.getPaint().setShader(shader_gradient1);
            if (batteryLevel == 15) {
                noLockView.setText(getResources().getString(
                        R.string.battery_less_than_15));
            } else if (batteryLevel == 5) {
                noLockView.setText(getResources().getString(
                        R.string.battery_less_than_5));
            } else if (batteryLevel == 1) {
                noLockView.setText(getResources().getString(
                        R.string.battery_to_power_off));
            }
            new Handler() {
                public void handleMessage(android.os.Message msg) {
                    finish();
                    overridePendingTransition(0, 0);
                };
            }.sendEmptyMessageDelayed(0, 3000);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(0, 0);
        }
        return super.onKeyDown(keyCode, event);
    }
}
