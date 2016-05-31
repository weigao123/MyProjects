package com.lesports.bike.settings.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.service.PttService;
import com.lesports.bike.settings.widget.SwitchButton;

import java.util.ArrayList;

/**
 * Created by gwball on 2016/5/25.
 */
public class PttFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        View.OnClickListener, PttService.PttServiceListener {

    public static final String PTT_CHANNEL_SELECT = "persist.sys.ptt_channel_select";
    public static final String PTT_STATUS = "persist.sys.ptt_status";
    public static final int PTT_CLOSED = 0;
    public static final int PTT_LOADING = 1;
    public static final int PTT_LOADED = 2;

    private PttService mPttService;

    private LinearLayout mPttOpenContainer;
    private TextView mPttPleaseOpen;
    private SwitchButton mSwitchButton;
    private TextView mPttStatusText;
    private TextView mPttChannelSelect;
    private ImageView mPttLoadingView;
    private ImageView mPttLoadedView;

    private ArrayList<String> mChannelsList = new ArrayList<String>();
    // start from 0
    private int mChannelSelect = 0;
    private int mPttStatus;
    private Animation mLoadingAnim;
    private ListAdapter mAdapter;
    @Override
    protected void initViewAndData() {
        mPttOpenContainer = (LinearLayout) getActivity().findViewById(R.id.ptt_open_container);
        mPttPleaseOpen = (TextView) getActivity().findViewById(R.id.ptt_please_open);
        mPttStatusText = (TextView) getActivity().findViewById(R.id.ptt_status);
        mSwitchButton = (SwitchButton) getActivity().findViewById(R.id.ptt_switch);
        mPttChannelSelect = (TextView) getActivity().findViewById(R.id.ptt_channel_select);
        mPttLoadingView = (ImageView) getActivity().findViewById(R.id.ptt_loading);
        mPttLoadedView = (ImageView) getActivity().findViewById(R.id.ptt_loaded);
        mLoadingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.loading_anim);
        mLoadingAnim.setInterpolator(new LinearInterpolator());

        mSwitchButton.setOnClickListener(this);
        ListView listView = (ListView) getActivity().findViewById(R.id.ptt_listview);
        mAdapter = new ListAdapter(getActivity(), 0, mChannelsList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        int pttStatus = SystemProperties.getInt(PttFragment.PTT_STATUS, 0);
        mChannelSelect = SystemProperties.getInt(PttFragment.PTT_CHANNEL_SELECT, 0);
        if (pttStatus == 1) {
            mPttStatus = PTT_LOADED;
        } else {
            mPttStatus = PTT_CLOSED;
        }

        getActivity().startService(new Intent(getActivity(), PttService.class));
        getActivity().bindService(new Intent(getActivity(), PttService.class), conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ptt, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.ptt);
    }

    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            PttService.PttBinder pttBinder = (PttService.PttBinder) service;
            mPttService = pttBinder.getService();
            mPttService.setServiceListener(PttFragment.this);
            mChannelsList.addAll(mPttService.getChannelList());
            mAdapter.notifyDataSetChanged();
            refreshStatus();
        }
        public void onServiceDisconnected(ComponentName name) {
            mPttService = null;
        }
    };

    private void openPtt() {
        mPttStatus = PTT_LOADING;
        refreshStatus();
        mPttService.openPtt(mChannelSelect);
    }

    private void closePtt() {
        mPttService.closePtt();
    }

    private void refreshStatus() {
        if (mPttStatus == PTT_CLOSED) {
            mSwitchButton.setSwitchStatus(false);
            mPttOpenContainer.setVisibility(View.INVISIBLE);
            mPttPleaseOpen.setVisibility(View.VISIBLE);
            mPttStatusText.setText(getResources().getString(R.string.ptt_off));
            mSwitchButton.setClickable(true);
            mPttLoadingView.clearAnimation();
        } else {
            if (mPttStatus == PTT_LOADING) {
                // 正在打开
                mSwitchButton.setClickable(false);
                mPttLoadingView.setVisibility(View.VISIBLE);
                mPttLoadingView.startAnimation(mLoadingAnim);
                mPttLoadedView.setVisibility(View.INVISIBLE);
            } else if (mPttStatus == PTT_LOADED) {
                // 成功打开
                mSwitchButton.setClickable(true);
                mPttLoadingView.setVisibility(View.INVISIBLE);
                mPttLoadingView.clearAnimation();
                mPttLoadedView.setVisibility(View.VISIBLE);
            }
            mSwitchButton.setSwitchStatus(true);
            mPttOpenContainer.setVisibility(View.VISIBLE);
            mPttPleaseOpen.setVisibility(View.INVISIBLE);
            mPttStatusText.setText(getResources().getString(R.string.ptt_on));
            mPttChannelSelect.setText(mChannelsList.get(mChannelSelect));
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPttStatus == PTT_LOADING) {
            return;
        }
        mPttStatus = PTT_LOADING;
        mChannelSelect = position;
        refreshStatus();
        mPttService.switchChannelFromUI(position);
    }

    @Override
    public void onClick(View v) {
        mSwitchButton.setClickable(false);
        if (!mSwitchButton.isSwitchOn()) {
            openPtt();
        } else {
            closePtt();
        }
    }

    @Override
    public void channelChanged(int index) {
        mChannelSelect = index;
        mHandler.sendEmptyMessage(1);
    }

    @Override
    public void statusChanged(int status) {
        mPttStatus = status;
        mHandler.sendEmptyMessage(1);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isAdded()) {
                refreshStatus();
            }
        }
    };

    private class ListAdapter extends ArrayAdapter<String> {
        public ListAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ptt_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.ptt_item_channel);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(mChannelsList.get(position));
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView textView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unbindService(conn);
        mPttService.setServiceListener(null);
    }
}
