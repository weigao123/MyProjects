package com.lesports.bike.settings.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.application.SettingApplication;
import com.lesports.bike.settings.ui.BaseFragment;
import com.lesports.bike.settings.ui.DetailActivity;
import com.lesports.bike.settings.ui.PttFragment;
import com.lesports.bike.settings.utils.L;
import com.lesports.bike.settings.utils.PopupUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

import bike.os.media.PTTListener;
import bike.os.media.PTTManager;

/**
 * Created by gaowei3 on 2016/5/29.
 *
 * 1、开机时，打开PTT
 * 2、AudioFocusLoss时关闭PTT
 */
public class PttService extends Service implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = PttService.class.getSimpleName();

    private PTTManager mPttManager;
    private AudioManager mAudioManager;
    private NotificationManager mNotifManager;
    private PowerManager mPowerManager;
    private PttServiceListener mPttServiceListener;
    private ArrayList<String> mChannelList;
    private View mPttListeningView;
    private View mPttSpeakingView;
    public class PttBinder extends Binder {
        public PttService getService() {
            return PttService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d(TAG, "onCreate");
        mPttManager = (PTTManager) getSystemService(Context.PTT_SERVICE);
        mPttManager.startListening(mPttListener);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        registerReceiver(pttReceiver, new IntentFilter(PTTManager.ACTION_TUNE));
        mPttListeningView = LayoutInflater.from(SettingApplication.getContext()).inflate(R.layout.ptt_listening, null);
        mPttSpeakingView = LayoutInflater.from(SettingApplication.getContext()).inflate(R.layout.ptt_speaking, null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PttBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra("switch")) {
                if (intent.getBooleanExtra("switch", false)) {
                    int channel = SystemProperties.getInt(PttFragment.PTT_CHANNEL_SELECT, 0);
                    openPtt(channel);
                } else {
                    closePtt();
                }
            } else if (intent.hasExtra("boot")) {
                int channel = SystemProperties.getInt(PttFragment.PTT_CHANNEL_SELECT, 0);
                openPtt(channel);
            }
        } else {
            if (SystemProperties.getInt(PttFragment.PTT_STATUS, 0) == 1) {
                int channel = SystemProperties.getInt(PttFragment.PTT_CHANNEL_SELECT, 0);
                openPtt(channel);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private BroadcastReceiver pttReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PTTManager.ACTION_TUNE.equals(intent.getAction())) {
                int channel = intent.getIntExtra(PTTManager.ACTION_TUNE_CHANNEL_ID, 1) - 1;
                SystemProperties.set(PttFragment.PTT_CHANNEL_SELECT, channel + "");
                startForeground(100, getNotification(getChannelList().get(channel)));
                if (mPttServiceListener != null) {
                    mPttServiceListener.channelChanged(channel);
                }
                L.d("ptt key channel change to index: " + (channel));
            }
        }
    };

    private PTTListener mPttListener = new PTTListener() {
        @Override
        public void onStatusChanged(int status) {
            switch (status) {
                case PTTManager.STATUS_SPEAKING:
                    PopupUtils.popupView(SettingApplication.getContext(), mPttSpeakingView);
                    break;
                case PTTManager.STATUS_SPEAKING_OVER:
                    PopupUtils.removeView(SettingApplication.getContext(), mPttSpeakingView);
                    break;
                case PTTManager.STATUS_LISTENING:
                    mPowerManager.wakeUp(SystemClock.uptimeMillis());
                    PopupUtils.popupTranslucentView(SettingApplication.getContext(), mPttListeningView);
                    break;
                case PTTManager.STATUS_LISTENING_OVER:
                    PopupUtils.removeView(SettingApplication.getContext(), mPttListeningView);
                    break;
                case PTTManager.STATUS_CLOSE:
                    PopupUtils.removeView(SettingApplication.getContext(), mPttListeningView);
                    PopupUtils.removeView(SettingApplication.getContext(), mPttSpeakingView);
                    break;
            }
        }
    };

    public void openPtt(final int channel) {
        L.d(TAG, "PTT start to open channel:" + channel);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPttManager.open()) {
                    if (mPttManager.tune(channel + 1)) {
                        mAudioManager.requestAudioFocus(PttService.this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                        startForeground(100, getNotification(getChannelList().get(channel)));
                        SystemProperties.set(PttFragment.PTT_STATUS, "1");
                        sendBroadcast(true);
                        if (mPttServiceListener != null) {
                            mPttServiceListener.statusChanged(PttFragment.PTT_LOADED);
                        }
                    }
                }
            }
        }).start();
    }

    public void closePtt() {
        L.d("closePtt");
        if (mPttManager.close()) {
            mAudioManager.abandonAudioFocus(PttService.this);
            stopForeground(true);
            SystemProperties.set(PttFragment.PTT_STATUS, "0");
            sendBroadcast(false);
            if (mPttServiceListener != null) {
                mPttServiceListener.statusChanged(PttFragment.PTT_CLOSED);
            }
        }
    }

    private void sendBroadcast(boolean state) {
        Intent intent = new Intent("com.lesports.bike.PTT_CHANGED");
        intent.putExtra("state", state);
        sendBroadcast(intent);
    }

    public void switchChannelFromUI(int channel) {
        if (mPttManager.tune(channel + 1)) {
            startForeground(100, getNotification(getChannelList().get(channel)));
            SystemProperties.set(PttFragment.PTT_CHANNEL_SELECT, channel + "");
            if (mPttServiceListener != null) {
                mPttServiceListener.statusChanged(PttFragment.PTT_LOADED);
            }
        }
    }

    private Notification getNotification(String text) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(BaseFragment.FRAGMENT_CLASS, PttFragment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.ptt)
                .setContentTitle("PTT当前频率")
                .setContentText(text)
                .setTicker("已开启PTT功能")   // 状态栏上显示
                .build();
        return notification;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        L.d(TAG, "FocusChange: " + focusChange);
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            closePtt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pttReceiver);
        L.d(TAG, "onDestroy");
        mPttManager.stopListening();
        mPttManager.close();
    }

    public void setServiceListener(PttServiceListener pttServiceListener) {
        this.mPttServiceListener = pttServiceListener;
    }

    public ArrayList<String> getChannelList() {
        if (mChannelList == null) {
            double[] allChannels = mPttManager.getAllChannels();
            DecimalFormat df = new DecimalFormat("#.0000");
            mChannelList = new ArrayList<String>();
            for (int i = 0; i < allChannels.length; i++) {
                mChannelList.add(df.format(allChannels[i]));
            }
        }
        return mChannelList;
    }

    public interface PttServiceListener {
        void channelChanged(int index);
        void statusChanged(int status);
    }
}
