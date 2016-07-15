package com.lesports.bike.settings.ui;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.AppDataUtils;

import java.io.FileDescriptor;

public class RingtoneFragment extends BaseFragment implements OnClickListener{
    
    private View ringtoneView;
    private MediaPlayer mediaPlayer;
    private CheckBox ringDefault;
    private CheckBox ringWave;
    private CheckBox ringBeacon;
    private CheckBox ringBlink;
    private CheckBox ringKnock;
    
    private static final int RING_DEFAULT = 0;
    private static final int RING_WAVE = 1;
    private static final int RING_BEACON = 2;
    private static final int RING_BLINK = 3;
    private static final int RING_KNOCK = 4;

    @Override
    protected void initViewAndData() {
        // TODO Auto-generated method stub
        ringtoneView.findViewById(R.id.ring_default).setOnClickListener(this);
        ringtoneView.findViewById(R.id.ring_wave).setOnClickListener(this);
        ringtoneView.findViewById(R.id.ring_beacon).setOnClickListener(this);
        ringtoneView.findViewById(R.id.ring_blink).setOnClickListener(this);
        ringtoneView.findViewById(R.id.ring_knock).setOnClickListener(this);
        
        ringDefault = (CheckBox) ringtoneView.findViewById(R.id.ring_default_cb);
        ringWave = (CheckBox) ringtoneView.findViewById(R.id.ring_wave_cb);
        ringBeacon = (CheckBox) ringtoneView.findViewById(R.id.ring_beacon_cb);
        ringBlink = (CheckBox) ringtoneView.findViewById(R.id.ring_blink_cb);
        ringKnock = (CheckBox) ringtoneView.findViewById(R.id.ring_knock_cb);
        
        ringDefault.setClickable(false);
        ringWave.setClickable(false);
        ringBeacon.setClickable(false);
        ringBlink.setClickable(false);
        ringKnock.setClickable(false);
        
        initRingtoneState();
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        ringtoneView = inflater.inflate(R.layout.fragment_ringtone, null);
        return ringtoneView;
    }

    @Override
    protected String getTitleName() {
        // TODO Auto-generated method stub
        return getResources().getString(R.string.ringtone);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.ring_default:
                resetSelect();
                ringDefault.setChecked(true);
                setRingtone(RING_DEFAULT);
                playRingtone(RING_DEFAULT);
                break;             
            case R.id.ring_wave:
                resetSelect();
                ringWave.setChecked(true);
                setRingtone(RING_WAVE);
                playRingtone(RING_WAVE);
                break;  
            case R.id.ring_beacon:
                resetSelect();
                ringBeacon.setChecked(true);
                setRingtone(RING_BEACON);
                playRingtone(RING_BEACON);
                break;
            case R.id.ring_blink:
                resetSelect();
                ringBlink.setChecked(true);
                setRingtone(RING_BLINK);
                playRingtone(RING_BLINK);
                break;
            case R.id.ring_knock:
                resetSelect();
                ringKnock.setChecked(true);
                setRingtone(RING_KNOCK);
                playRingtone(RING_KNOCK);
                break;
            default:
                resetSelect();
                ringDefault.setChecked(true);
                setRingtone(RING_DEFAULT);
                playRingtone(RING_DEFAULT);
                break;
        }
        
    }
    
    private void resetSelect() {
        ringDefault.setChecked(false);
        ringWave.setChecked(false);
        ringBeacon.setChecked(false);
        ringBlink.setChecked(false);
        ringKnock.setChecked(false);
    }
    
    private void initRingtoneState() {
        int ringtoneValue = AppDataUtils.getRingtone(getActivity());
        initRingtone(ringtoneValue);
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
//        mediaPlayer = MediaPlayer.create(getActivity(), resId);
//        mediaPlayer.start();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        AssetFileDescriptor openRawResourceFd = getActivity().getResources().openRawResourceFd(resId);
        try {
            FileDescriptor fileDescriptor = openRawResourceFd.getFileDescriptor();
            System.out.println(fileDescriptor);
            mediaPlayer.setDataSource(fileDescriptor,openRawResourceFd.getStartOffset(),openRawResourceFd.getDeclaredLength());
            openRawResourceFd.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void initRingtone(int ringtoneValue) {
        switch (ringtoneValue) {
        case 0:
            ringDefault.setChecked(true);
            break;
        case 1:
            ringWave.setChecked(true);
            break;
        case 2:
            ringBeacon.setChecked(true);
            break;
        case 3:
            ringBlink.setChecked(true);
            break;
        case 4:
            ringKnock.setChecked(true);
            break;
        default:
            ringDefault.setChecked(true);
            break;
        }
    }
    
    
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    } 
    
    private void setRingtone(int id) {
        AppDataUtils.setRingtone(getActivity(), id);
    }
    
}