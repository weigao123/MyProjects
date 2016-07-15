
package com.lesports.bike.settings.receiver;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.lesports.bike.settings.control.BluetoothControl;

import java.util.Locale;

public class BluetoothConnectActivityReceiver extends BroadcastReceiver {

    String pinCode = "0";
    private String TAG = "iii";
    int pairingKey;
    String mPairingKey;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
            Log.d(TAG, "收到配对请求");
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT,
                    BluetoothDevice.ERROR);
            Log.d(TAG, "device = " + device.getName());
            Log.d(TAG, "type = " + type);
            if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION ||
                    type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY ||
                    type == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN) {
                pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,
                        BluetoothDevice.ERROR);
                Log.d(TAG, "pairingKey = " + pairingKey);
            }
            mPairingKey = String.format(Locale.US, "%06d", pairingKey);
            BluetoothControl.getInstance(context).onPair(String.valueOf(pairingKey),
                    device, type);
        } else if (action.equals(BluetoothDevice.ACTION_PAIRING_CANCEL)) {
            // Remove the notification
            // NotificationManager manager = (NotificationManager) context
            // .getSystemService(Context.NOTIFICATION_SERVICE);
            // manager.cancel(NOTIFICATION_ID);
        }
    }
}
