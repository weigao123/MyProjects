
package com.lesports.bike.settings.ui;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.VolumePreference.VolumeStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import bike.os.security.ElectronicLockManager;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.ui.AudioFragment.ExSeekBarVolumizer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gwball on 2016/5/25.
 */
public class DisplayFragment extends BaseFragment implements View.OnClickListener {

    private View displayView;
    private LinearLayout brightnessSection;
    private com.lesports.bike.settings.widget.SwitchButton brightnessAutioAdjust;
    private com.lesports.bike.settings.widget.SwitchButton saveMode;
    boolean isAutoBrightness = false;
    private ElectronicLockManager electronicLockManager;
    private Boolean isBikeRunning = false;
    private static final int BIKE_IS_RUNNING = -2;
    private static final String TAG = "DisplayFragment";

    @Override
    protected void initViewAndData() {
        SeekBar seekBar = (SeekBar) displayView.findViewById(R.id.brightness_seek_bar);
        new ExSeekBarBrightness(this.getActivity(), seekBar);
        if (isAutoBrightness(this.getActivity())) {
            brightnessSection.setVisibility(View.GONE);
            brightnessAutioAdjust.setSwitchStatus(true);
        } else {
            brightnessSection.setVisibility(View.VISIBLE);
            brightnessAutioAdjust.setSwitchStatus(false);
        }
        brightnessAutioAdjust.setOnClickListener(this);
        saveMode.setOnClickListener(this);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        displayView = inflater.inflate(R.layout.fragment_display, container, false);
        electronicLockManager = (ElectronicLockManager) this.getActivity().getSystemService(
                "electronic_lock_service");
        brightnessSection = (LinearLayout) displayView.findViewById(R.id.brightness_section);
        brightnessAutioAdjust = (com.lesports.bike.settings.widget.SwitchButton) displayView
                .findViewById(R.id.brightness_autio_adjust_switch);
        saveMode = (com.lesports.bike.settings.widget.SwitchButton) displayView
                .findViewById(R.id.save_mode_switch);
        return displayView;
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.display_title);
    }

    /**
     * Turns a {@link SeekBar} into a brightness control.
     */
    public class ExSeekBarBrightness implements OnSeekBarChangeListener, Handler.Callback {

        private Context mContext;
        private Handler mHandler;
        private SeekBar mSeekBar;
        private int mLastProgress = -1;

        private static final int MSG_SET_BRIGHTNESS = 0;

        // private ContentObserver mBrightnessObserver = new
        // ContentObserver(mHandler) {
        // @Override
        // public void onChange(boolean selfChange) {
        // super.onChange(selfChange);
        // if (mSeekBar != null) {
        // int volume = Settings.System.getInt(getActivity()
        // .getContentResolver(),
        // Settings.System.SCREEN_BRIGHTNESS, 255);
        // mSeekBar.setProgress(volume);
        // }
        // }
        // };

        public ExSeekBarBrightness(Context context, SeekBar seekBar) {
            mContext = context;
            mSeekBar = seekBar;

            HandlerThread thread = new HandlerThread("CallbackHandler");
            thread.start();
            mHandler = new Handler(thread.getLooper(), this);

            initSeekBar(seekBar);
        }

        private void initSeekBar(SeekBar seekBar) {
            int mOriginal = Settings.System.getInt(getActivity()
                    .getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 255);
            seekBar.setMax(255);
            seekBar.setProgress(mOriginal);
            seekBar.setOnSeekBarChangeListener(this);

            // mContext.getContentResolver().registerContentObserver(
            // Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            // false, mBrightnessObserver);
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_BRIGHTNESS:
                    Settings.System.putInt(getActivity().getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, mLastProgress);
                    break;
            }
            return true;
        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromTouch) {
            if (!fromTouch) {
                return;
            }
            postSetBrightness(progress);
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        void postSetBrightness(int progress) {
            mLastProgress = progress;
            mHandler.removeMessages(MSG_SET_BRIGHTNESS);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_BRIGHTNESS));
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            // TODO Auto-generated method stub
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.brightness_autio_adjust_switch:
                changeBrightnessAutioAdjust();
                break;
            case R.id.save_mode_switch:
                changeSaveMode();
                break;
            default:
                break;
        }
    }

    public boolean isAutoBrightness(Context context) {
        try {
            isAutoBrightness = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) ==
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        return isAutoBrightness;
    }

    public Boolean isBikeRunning() {
        isBikeRunning = electronicLockManager.lock() == BIKE_IS_RUNNING;
        return isBikeRunning;
    }

    public void changeBrightnessAutioAdjust() {
        if (isAutoBrightness(getActivity())) {
            brightnessSection.setVisibility(View.VISIBLE);
            brightnessAutioAdjust.setSwitchStatus(false);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        } else {
            brightnessSection.setVisibility(View.GONE);
            brightnessAutioAdjust.setSwitchStatus(true);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
    }

    public void changeSaveMode() {
        if (!saveMode.isSwitchOn() && !isBikeRunning()) {
            saveMode.setSwitchStatus(true);
            Settings.System.putInt(getActivity().getContentResolver(),
                    android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 60000);
        } else {
            saveMode.setSwitchStatus(false);
            Settings.System.putInt(getActivity().getContentResolver(),
                    android.provider.Settings.System.SCREEN_OFF_TIMEOUT, -1);
        }
    }

}
