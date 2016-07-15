
package com.lesports.bike.settings.control;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.application.SettingApplication;
import com.lesports.bike.settings.ui.BatteryLowActivity;
import com.lesports.bike.settings.utils.PopupUtils;

public class BatteryControl {

    private static final String TAG = "BatteryControl";
    private static BatteryControl manager;
    private Context context;
    private int batteryLevel;

    private BatteryControl(Context context) {
        this.context = context;
        context.registerReceiver(new BatteryReceiver(), new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
    }

    public static BatteryControl getInstance() {
        if (manager == null) {
            manager = new BatteryControl(SettingApplication.getContext());
        }
        return manager;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    private class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra("level", 0);
            int scale = intent.getIntExtra("scale", 100);
            int currentLevel = (level * 100) / scale;
            if (currentLevel == batteryLevel) {
                return;
            }
            batteryLevel = (level * 100) / scale;
            Log.d(TAG, "onReceive     :" + batteryLevel);

            int status = intent.getIntExtra("status",
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            if (batteryLevel == 15 || batteryLevel == 5 || batteryLevel == 1) {
                if (status != BatteryManager.BATTERY_STATUS_CHARGING) {//
                    // 充电时不提示低电量

                    /*
                     * Intent alarmIntent = new Intent(context,
                     * BatteryLowActivity.class);
                     * alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     * alarmIntent.putExtra("batteryLevel", batteryLevel);
                     * alarmIntent.putExtra("isLock", isScreenLocked());
                     * context.startActivity(alarmIntent);
                     */
                    if (batteryLevel == 15) {
                        PopupUtils.popupToastView(SettingApplication.getContext(),
                                R.string.battery_less_than_15);

                    } else if (batteryLevel == 5) {
                        PopupUtils.popupToastView(SettingApplication.getContext(),
                                R.string.battery_less_than_5);
                    } else if (batteryLevel == 1) {
                        PopupUtils.popupToastView(SettingApplication.getContext(),
                                R.string.battery_to_power_off);
                    }
                    playLowBatterySound();

                }
            }

        }
    }

    void playLowBatterySound() {
        final ContentResolver cr = context.getContentResolver();

        /*
         * final int silenceAfter = Settings.Global.getInt(cr,
         * Settings.Global.LOW_BATTERY_SOUND_TIMEOUT, 0); final long offTime =
         * SystemClock.elapsedRealtime() - mScreenOffTime; if (silenceAfter > 0
         * && mScreenOffTime > 0 && offTime > silenceAfter) { Slog.i(TAG ,
         * "screen off too long (" + offTime + "ms, limit " + silenceAfter +
         * "ms): not waking up the user with low battery sound"); return; }
         */

        if (Settings.Global.getInt(cr, Settings.Global.POWER_SOUNDS_ENABLED, 1) == 1) {
            final String soundPath = Settings.Global.getString(cr,
                    Settings.Global.LOW_BATTERY_SOUND);
            if (soundPath != null) {
                final Uri soundUri = Uri.parse("file://" + soundPath);
                if (soundUri != null) {
                    final Ringtone sfx = RingtoneManager.getRingtone(context, soundUri);
                    if (sfx != null) {
                        sfx.setStreamType(AudioManager.STREAM_SYSTEM);
                        sfx.play();
                    }
                }
            }
        }
    }

    /**
     * 是否屏幕是亮的
     * 
     * @return
     */
    private boolean isScreenOn() {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        return isScreenOn;
    }

    /**
     * 是否锁屏
     * 
     * @return
     */
    private boolean isScreenLocked() {
        KeyguardManager mKeyguardManager = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
        return flag;
    }
}
