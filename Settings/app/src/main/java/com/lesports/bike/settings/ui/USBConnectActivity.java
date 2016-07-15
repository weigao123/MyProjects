
package com.lesports.bike.settings.ui;

import com.lesports.bike.settings.R;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Shader;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.graphics.LinearGradient;

public class USBConnectActivity extends Activity implements OnClickListener {

    private TextView chargeOnly;
    private TextView mediaAndCharge;
    private UsbManager mUsbManager = null;
    private BroadcastReceiver mPowerDisconnectReceiver = null;

    public static final int CHARGE_ONLY = 1;
    public static final int MEDIA_AND_CHARGE = 2;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);        
        setContentView(R.layout.usb_connect_view);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPowerDisconnectReceiver = new PowerDisconnectReceiver();
        registerReceiver(mPowerDisconnectReceiver, new IntentFilter(
                Intent.ACTION_POWER_DISCONNECTED));
        chargeOnly = (TextView) findViewById(R.id.charge_only_button);
        mediaAndCharge = (TextView) findViewById(R.id.media_and_charge_button);
        Shader shader_gradient1 = new LinearGradient(0, 10, 0, 80, getResources().getColor(R.color.gradient_start_color),
                getResources().getColor(R.color.gradient_end_color), Shader.TileMode.CLAMP);
        Shader shader_gradient2 = new LinearGradient(0, 10, 0, 80, getResources().getColor(R.color.gradient_start_color),
                getResources().getColor(R.color.gradient_end_color), Shader.TileMode.CLAMP);
        chargeOnly.getPaint().setShader(shader_gradient1);
        mediaAndCharge.getPaint().setShader(shader_gradient2);
        chargeOnly.setOnClickListener(this);
        mediaAndCharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.charge_only_button:
                Message msg = Message.obtain(mHandler, CHARGE_ONLY);
                mHandler.sendMessageDelayed(msg, 0);
                break;
            case R.id.media_and_charge_button:
                Message msg2 = Message.obtain(mHandler, MEDIA_AND_CHARGE);
                mHandler.sendMessageDelayed(msg2, 0);
            default:
                break;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CHARGE_ONLY:
                    mUsbManager.setCurrentFunction(
                            UsbManager.USB_FUNCTION_NONE, false);
                    USBConnectActivity.this.finish();
                    break;
                case MEDIA_AND_CHARGE:
                    mUsbManager.setCurrentFunction(
                            UsbManager.USB_FUNCTION_MTP, false);
                    USBConnectActivity.this.finish();
                    break;
                default:
                    break;
            }

        };
    };

    private class PowerDisconnectReceiver extends BroadcastReceiver {
        public void onReceive(Context content, Intent intent) {
            USBConnectActivity.this.finish();
        }
    }

}
