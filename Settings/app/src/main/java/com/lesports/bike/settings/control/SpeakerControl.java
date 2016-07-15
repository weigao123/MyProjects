package com.lesports.bike.settings.control;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.AppDataUtils;

import java.io.FileDescriptor;

public class SpeakerControl {
    
    private static SpeakerControl speakerControl;
    private Context mContext;
    private MediaPlayer mediaPlayer; 
    private boolean isPlaying;  
    
    public SpeakerControl(Context context) {
        mContext = context;
    }

    public static synchronized SpeakerControl fromApplication(Context context){
        if (speakerControl == null) {
            speakerControl = new SpeakerControl(context);
        }
        return speakerControl;
    }
    
    public void start(){
        if(!isPlaying){
            playRingtone(AppDataUtils.getRingtone(mContext));
            isPlaying = true;
        }
    }
    
    public void stop(){
        if(isPlaying){
            releaseMediaPlayer();
            isPlaying = false;
        }
    }
    
    private void playRingtone(int ringtone) {
        int resId;
        switch (ringtone) {
        case 0:
            resId = R.raw.bell_d;
            break;
        case 1:
            resId = R.raw.bell_e;
            break;
        case 2:
            resId = R.raw.bell_f;
            break;
        case 3:
            resId = R.raw.bell_g;
            break;
        case 4:
            resId = R.raw.bell_h;
            break;
        default:
            resId = R.raw.bell_d;
            break;
        } 
        releaseMediaPlayer();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        AssetFileDescriptor openRawResourceFd = mContext.getResources().openRawResourceFd(resId);
        try {
            FileDescriptor fileDescriptor = openRawResourceFd.getFileDescriptor();
            System.out.println(fileDescriptor);
            mediaPlayer.setDataSource(fileDescriptor,openRawResourceFd.getStartOffset(),openRawResourceFd.getDeclaredLength());
            openRawResourceFd.close();
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
  
}