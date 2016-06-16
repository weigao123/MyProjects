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
import android.os.SystemProperties;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.ui.BaseFragment;
import com.lesports.bike.settings.ui.DetailActivity;
import com.lesports.bike.settings.ui.MainActivity;
import com.lesports.bike.settings.ui.PttFragment;
import com.lesports.bike.settings.utils.L;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
    private PttServiceListener mPttServiceListener;
    private ArrayList<String> mChannelList;

    public class PttBinder extends Binder {
        public PttService getService() {
            return PttService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.d(TAG, "onCreate");

        mPttManager = (PTTManager) getSystemService("ptt_service");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mNotifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        registerReceiver(pttReceiver, new IntentFilter(PTTManager.ACTION_TUNE));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PttBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("isBoot")) {
            if (SystemProperties.getInt(PttFragment.PTT_STATUS, 0) == 1) {
                final int channel = SystemProperties.getInt(PttFragment.PTT_CHANNEL_SELECT, 0);
                L.d(TAG, "Boot: PTT start to open channel:" + channel);
                openPtt(channel);
            } else {
                L.d(TAG, "Boot: PTT status is 0");
            }
        } else if (intent.hasExtra("state")) {
            if (intent.getBooleanExtra("state", false)) {
                final int channel = SystemProperties.getInt(PttFragment.PTT_CHANNEL_SELECT, 0);
                openPtt(channel);
            } else {
                closePtt();
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

    public void openPtt(final int channel) {
        L.d("openPtt");
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
