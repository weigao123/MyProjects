package com.lesports.bike.settings.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.widget.SwitchButton;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

import bike.os.media.PTTManager;

/**
 * Created by gwball on 2016/5/25.
 */
public class PttFragment extends BaseFragment {
    private PTTManager mPttManager;

    private SwitchButton mSwitchButton;
    private TextView mStatusView;

    @Override
    protected void initViewAndData() {
        mStatusView = (TextView) getActivity().findViewById(R.id.ptt_status);
        mSwitchButton = (SwitchButton) getActivity().findViewById(R.id.ptt_switch);

        mPttManager = (PTTManager) getActivity().getSystemService("ptt_service");
        double[] allChannels = mPttManager.getAllChannels();

        String[] ptts = new String[allChannels.length];
        DecimalFormat df = new DecimalFormat("#.0000");
        for (int i = 0; i < allChannels.length; i++) {
            ptts[i] = df.format(allChannels[i]);
        }

        int pttStatus = mPttManager.getCurChannelId();
        Log.d("wei.gao", "ptt: "+pttStatus);

        getActivity().registerReceiver(pttReceiver, new IntentFilter(PTTManager.ACTION_TUNE));
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ptt, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.ptt);
    }

    private BroadcastReceiver pttReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra(PTTManager.ACTION_TUNE_CHANNEL_ID, 0);

        }

    };
}
