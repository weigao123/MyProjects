
package com.lesports.bike.settings.ui;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.VolumePreference.SeekBarVolumizer;
import android.preference.VolumePreference.VolumeStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Broadcaster;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.lesports.bike.settings.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gwball on 2016/5/25.
 */
public class AudioFragment extends BaseFragment implements View.OnKeyListener {

    private ExSeekBarVolumizer[] mSeekBarVolumizer;
    private static ExSeekBarVolumizer ringExSeekBarVolumizer;
    private View audioView;
    private static final String TAG = "AudioFragment";

    private static final int[] SEEKBAR_ID = new int[] {
            R.id.ringtone_seek_bar,
            R.id.music_seek_bar
    };

    private static final int[] SEEKBAR_TYPE = new int[] {
            AudioManager.STREAM_RING,
            AudioManager.STREAM_MUSIC
    };

    private SeekBar[] mSeekBars = new SeekBar[SEEKBAR_ID.length];

    public Uri getMediaVolumeUri(Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.getPackageName()
                + "/" + R.raw.media_volume);
    }

    private void bindSeekBar(View view) {
        for (int i = 0; i < SEEKBAR_ID.length; i++) {
            SeekBar seekBar = (SeekBar) view.findViewById(SEEKBAR_ID[i]);
            mSeekBars[i] = seekBar;
            if (SEEKBAR_TYPE[i] == AudioManager.STREAM_MUSIC) {
                mSeekBarVolumizer[i] = new ExSeekBarVolumizer(this.getActivity(), seekBar,
                        SEEKBAR_TYPE[i], getMediaVolumeUri(this.getActivity()));
            } else {
                mSeekBarVolumizer[i] = new ExSeekBarVolumizer(this.getActivity(), seekBar,
                        SEEKBAR_TYPE[i]);
            }
        }
        ringExSeekBarVolumizer = mSeekBarVolumizer[0];
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        audioView = inflater.inflate(R.layout.fragment_audio, container, false);
        return audioView;
    }

    @Override
    protected void initViewAndData() {
        mSeekBarVolumizer = new ExSeekBarVolumizer[SEEKBAR_ID.length];
        bindSeekBar(audioView);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.audio);
    }

    protected static void onSampleStarting(ExSeekBarVolumizer volumizer) {
        if (ringExSeekBarVolumizer != null && volumizer != ringExSeekBarVolumizer) {
            ringExSeekBarVolumizer.stopSample();
        }
    }

    /**
     * Turns a {@link SeekBar} into a volume control.
     */
    public class ExSeekBarVolumizer implements OnSeekBarChangeListener, Handler.Callback {

        private Context mContext;
        private Handler mHandler;

        private AudioManager mAudioManager;
        private boolean mHasAudioFocus = false;
        private int mStreamType;
        private int mOriginalStreamVolume;
        private Ringtone mRingtone;

        private int mLastProgress = -1;
        private SeekBar mSeekBar;
        private VolumeReciever mVolumeReciever;

        private static final String  VOLUME_CHANGED_ACTION  =
                "android.media.VOLUME_CHANGED_ACTION";
        private static final int MSG_SET_STREAM_VOLUME = 0;
        private static final int MSG_START_SAMPLE = 1;
        private static final int MSG_STOP_SAMPLE = 2;
        private static final int CHECK_RINGTONE_PLAYBACK_DELAY_MS = 1000;

        private ContentObserver mVolumeObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (mSeekBar != null && mAudioManager != null) {
                    int volume = mAudioManager.getStreamVolume(mStreamType);
                    mSeekBar.setProgress(volume);
                }
            }
        };

        public ExSeekBarVolumizer(Context context, SeekBar seekBar, int streamType) {
            this(context, seekBar, streamType, null);
        }

        public ExSeekBarVolumizer(Context context, SeekBar seekBar, int streamType, Uri defaultUri) {
            mContext = context;
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mStreamType = streamType;
            mSeekBar = seekBar;

            HandlerThread thread = new HandlerThread(TAG + ".CallbackHandler");
            thread.start();
            mHandler = new Handler(thread.getLooper(), this);

            initSeekBar(seekBar, defaultUri);
            initVolumeReceiver();
        }

        private void initSeekBar(SeekBar seekBar, Uri defaultUri) {
            seekBar.setMax(mAudioManager.getStreamMaxVolume(mStreamType));
            mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
            seekBar.setProgress(mOriginalStreamVolume);
            seekBar.setOnSeekBarChangeListener(this);

            mContext.getContentResolver().registerContentObserver(
                    System.getUriFor(System.VOLUME_SETTINGS[mStreamType]),
                    false, mVolumeObserver);

            if (defaultUri == null) {
                if (mStreamType == AudioManager.STREAM_RING) {
                    defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
                } else if (mStreamType == AudioManager.STREAM_NOTIFICATION) {
                    defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                } else {
                    defaultUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                }
            }
            mRingtone = RingtoneManager.getRingtone(mContext, defaultUri);
            if (mRingtone != null) {
                mRingtone.setStreamType(mStreamType);
            }
        }

        private void initVolumeReceiver() {
            mVolumeReciever = new VolumeReciever();
            IntentFilter filter = new IntentFilter();
            filter.addAction(VOLUME_CHANGED_ACTION);
            getActivity().registerReceiver(mVolumeReciever, filter);
        }

        private class VolumeReciever extends BroadcastReceiver {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub
                if (arg1.getAction().equals(VOLUME_CHANGED_ACTION)) {
                    mSeekBar.setProgress(mAudioManager.getStreamVolume(mStreamType));
                }
            }
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_STREAM_VOLUME:
                    mAudioManager.setStreamVolume(mStreamType, mLastProgress, 0);
                    break;
                case MSG_START_SAMPLE:
                    onStartSample();
                    break;
                case MSG_STOP_SAMPLE:
                    onStopSample();
                    break;
            }
            return true;
        }

        private void postStartSample() {
            mHandler.removeMessages(MSG_START_SAMPLE);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_SAMPLE),
                    isSamplePlaying() ? CHECK_RINGTONE_PLAYBACK_DELAY_MS : 0);
        }

        private void postStopSample() {
            // remove pending delayed start messages
            mHandler.removeMessages(MSG_START_SAMPLE);
            mHandler.removeMessages(MSG_STOP_SAMPLE);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_SAMPLE));
        }

        private void onStartSample() {
            if (!isSamplePlaying()) {
                onSampleStarting(this);
                /* SPRD: add for interaction between music and clock @{ */
                if (!mHasAudioFocus && mAudioManager != null) {
                    mAudioManager.requestAudioFocus(null, mStreamType,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                    mHasAudioFocus = true;
                }
                /* @} */
                if (mRingtone != null) {
                    mRingtone.play();
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            onStopSample();
                        }
                    }, 1500);
                }
            }
        }

        private void onStopSample() {
            if (mRingtone != null) {
                mRingtone.stop();
            }
            /* SPRD: add for interaction between music and clock, @{ */
            if (mHasAudioFocus && mAudioManager != null) {
                mAudioManager.abandonAudioFocus(null);
                mHasAudioFocus = false;
            }
            /* @} */
        }

        public void startSample() {
            postStartSample();
        }

        public void stopSample() {
            postStopSample();
        }

        public void stop() {
            postStopSample();
            mContext.getContentResolver().unregisterContentObserver(mVolumeObserver);
            mSeekBar.setOnSeekBarChangeListener(null);
        }

        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromTouch) {

            mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);

            if (!fromTouch) {
                return;
            }
            postSetVolume(progress);
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            postStartSample();
        }

        void postSetVolume(int progress) {
            // Do the volume changing separately to give responsive UI
            mLastProgress = progress;
            mHandler.removeMessages(MSG_SET_STREAM_VOLUME);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_STREAM_VOLUME));
        }

        public boolean isSamplePlaying() {
            return mRingtone != null && mRingtone.isPlaying();
        }

        public void changeVolumeBy(int amount) {
            mSeekBar.incrementProgressBy(amount);
            postSetVolume(mSeekBar.getProgress());
            postStartSample();
        }

        public void onSaveInstanceState(VolumeStore volumeStore) {
            if (mLastProgress >= 0) {
                volumeStore.volume = mLastProgress;
                volumeStore.originalVolume = mOriginalStreamVolume;
            }
        }

        public void onRestoreInstanceState(VolumeStore volumeStore) {
            if (volumeStore.volume != -1) {
                mOriginalStreamVolume = volumeStore.originalVolume;
                mLastProgress = volumeStore.volume;
                postSetVolume(mLastProgress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
            // TODO Auto-generated method stub
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If key arrives immediately after the activity has been cleaned up.
        if (mSeekBarVolumizer == null)
            return true;
        boolean isdown = (event.getAction() == KeyEvent.ACTION_DOWN);
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (isdown) {
                    ringExSeekBarVolumizer.changeVolumeBy(-1);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (isdown) {
                    ringExSeekBarVolumizer.changeVolumeBy(1);
                }
                return true;
            default:
                return false;
        }
    }

}
