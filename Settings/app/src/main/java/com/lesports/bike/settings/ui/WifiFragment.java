package com.lesports.bike.settings.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.control.WifiControl;
import com.lesports.bike.settings.widget.InputBox;
import com.lesports.bike.settings.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;
import com.lesports.bike.settings.control.WifiControl.*;

/**
 * Created by gwball on 2016/5/25.
 */
public class WifiFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, WifiControlCallback {

    private static final int WIFI_CLOSED = 1;
    private static final int WIFI_OPENED = 2;
    private static final int WIFI_CONNECTTING = 3;
    private static final int WIFI_CONNECTTED = 4;

    private static final int WIFI_REFRESH_DATA = 1;
    private static final int WIFI_REFRESH_VIEW = 2;

    private WifiControl mWifiControl;
    private List<WifiBean> mWifiList = new ArrayList<WifiBean>();

    private int mWifiStatus;
    private String mWifiSelectedName;
    private ListAdapter mAdapter;

    private SwitchButton mSwitchButton;
    private TextView mWifiStatusView;
    private TextView mWifiPleaseOpen;
    private LinearLayout mWifiCurrentContainer;
    private TextView mWifiSelectedView;
    private ImageView mWifiLoading;
    private ImageView mWifiLoaded;
    private LinearLayout mWifiCandidateContainer;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.wifi);
    }


    @Override
    public void initViewAndData() {
        mSwitchButton = (SwitchButton)getActivity().findViewById(R.id.wifi_switch);
        mWifiStatusView = (TextView)getActivity().findViewById(R.id.wifi_status);
        mWifiPleaseOpen = (TextView)getActivity().findViewById(R.id.wifi_please_open);
        mWifiCurrentContainer = (LinearLayout)getActivity().findViewById(R.id.wifi_current_container);
        mWifiSelectedView = (TextView)getActivity().findViewById(R.id.wifi_current_select);
        mWifiLoading = (ImageView)getActivity().findViewById(R.id.wifi_loading);
        mWifiLoaded = (ImageView)getActivity().findViewById(R.id.wifi_loaded);
        mWifiCandidateContainer = (LinearLayout)getActivity().findViewById(R.id.wifi_candidate_container);
        ListView listView = (ListView) getActivity().findViewById(R.id.wifi_listview);

        mSwitchButton.setOnClickListener(this);
        mAdapter = new ListAdapter(getActivity(), 0, mWifiList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        mWifiControl = WifiControl.getInstance(getActivity());
        mWifiControl.setCallback(this);
        mWifiControl.initWifi();
        refreshView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InputBox inputBox = new InputBox();
        //inputBox.setStyle(DialogFragment.STYLE_NO_TITLE | DialogFragment.STYLE_NO_FRAME, 0);
        inputBox.show(getFragmentManager(), null);

    }

    @Override
    public void onClick(View v) {
        mSwitchButton.setClickable(false);
        boolean goStatus = !mSwitchButton.isSwitchOn();
        if (goStatus) {
            mWifiControl.openWifi();
        } else {
            mWifiControl.closeWifi();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_REFRESH_DATA:
                    List<WifiBean> newWifiList = (List<WifiBean>)msg.obj;
                    mWifiList.clear();
                    mWifiList.addAll(newWifiList);
                    break;
                case WIFI_REFRESH_VIEW:
                    break;
            }
            refreshView();
        }
    };

    private void refreshView() {
        if (!isAdded()) {
            return;
        }
        int wifiStatus = mWifiControl.getWifiStatus();
        switch (wifiStatus) {
            case WifiManager.WIFI_STATE_DISABLING:
                mSwitchButton.setSwitchStatus(false);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_on));
                mWifiPleaseOpen.setVisibility(View.GONE);
                mWifiCurrentContainer.setVisibility(View.GONE);
                mWifiCandidateContainer.setVisibility(View.VISIBLE);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                mSwitchButton.setSwitchStatus(false);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_off));
                mWifiPleaseOpen.setVisibility(View.VISIBLE);
                mWifiCurrentContainer.setVisibility(View.GONE);
                mWifiCandidateContainer.setVisibility(View.GONE);
                mSwitchButton.setClickable(true);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                mSwitchButton.setSwitchStatus(true);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_on));
                mWifiPleaseOpen.setVisibility(View.GONE);
                mWifiCurrentContainer.setVisibility(View.GONE);
                mWifiCandidateContainer.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                mSwitchButton.setSwitchStatus(true);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_on));
                mWifiPleaseOpen.setVisibility(View.GONE);
                mWifiCurrentContainer.setVisibility(View.GONE);
                mWifiCandidateContainer.setVisibility(View.VISIBLE);
                mSwitchButton.setClickable(true);
                mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWifiControl.detachWifi();
    }

    @Override
    public void onRefreshDataSuccess(List<WifiBean> newWifiList) {
        mHandler.obtainMessage(WIFI_REFRESH_DATA, newWifiList).sendToTarget();
    }

    @Override
    public void onStatusChanged(int status) {
        mHandler.sendEmptyMessage(WIFI_REFRESH_VIEW);
    }

    private class ListAdapter extends ArrayAdapter<WifiBean> {
        public ListAdapter(Context context, int resource, List<WifiBean> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.wifi_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.wifiNameView = (TextView) convertView.findViewById(R.id.wifi_item_name);
                viewHolder.wifiLockView = (ImageView) convertView.findViewById(R.id.wifi_has_lock);
                viewHolder.wifiStrength = (ImageView) convertView.findViewById(R.id.wifi_strength);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            WifiBean result = mWifiList.get(position);
            viewHolder.wifiNameView.setText(result.name);
            viewHolder.wifiLockView.setVisibility(result.isLock ? View.VISIBLE : View.INVISIBLE);
            viewHolder.wifiStrength.setImageResource(R.drawable.wifi_level);
            viewHolder.wifiStrength.setImageLevel(result.level);
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView wifiNameView;
        public ImageView wifiLockView;
        public ImageView wifiStrength;
    }
}
