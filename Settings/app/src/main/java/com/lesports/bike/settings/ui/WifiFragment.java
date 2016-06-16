package com.lesports.bike.settings.ui;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.lesports.bike.settings.control.WifiControl;
import com.lesports.bike.settings.widget.InputBox;
import com.lesports.bike.settings.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;
import com.lesports.bike.settings.control.WifiControl.*;

/**
 * Created by gwball on 2016/5/25.
 */
public class WifiFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        View.OnClickListener, WifiControlCallback, InputBox.InputCallback {

    private static final int WIFI_REFRESH_DATA = 1;
    private static final int WIFI_REFRESH_VIEW = 2;

    private WifiControl mWifiControl;
    private List<WifiBean> mWifiList = new ArrayList<WifiBean>();

    private WifiBean mWifiToConnect;
    private NetworkInfo mNetworkInfo;
    private ListAdapter mAdapter;
    private Animation mLoadingAnim;

    private SwitchButton mSwitchButton;
    private TextView mWifiStatusView;
    private TextView mWifiPleaseOpen;
    private LinearLayout mWifiConnectionContainer;
    private TextView mWifiSelectedView;
    private ImageView mWifiLoading;
    private ImageView mWifiLoaded;
    private LinearLayout mWifiListContainer;

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
        mWifiConnectionContainer = (LinearLayout)getActivity().findViewById(R.id.wifi_current_container);
        mWifiSelectedView = (TextView)getActivity().findViewById(R.id.wifi_current_select);
        mWifiLoading = (ImageView)getActivity().findViewById(R.id.wifi_loading);
        mWifiLoaded = (ImageView)getActivity().findViewById(R.id.wifi_loaded);
        mWifiListContainer = (LinearLayout)getActivity().findViewById(R.id.wifi_candidate_container);
        ListView listView = (ListView) getActivity().findViewById(R.id.wifi_listview);
        mLoadingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.loading_anim);
        mLoadingAnim.setInterpolator(new LinearInterpolator());

        mSwitchButton.setOnClickListener(this);
        mAdapter = new ListAdapter(getActivity(), 0, mWifiList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        mWifiControl = WifiControl.getInstance(getActivity());
        mWifiControl.setCallback(this);
        mWifiControl.attachWifiControl();
        refreshView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mWifiControl.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            return;
        }
        WifiBean wifiBean = mWifiList.get(position);
        mWifiToConnect = wifiBean;
        if (wifiBean.passwordType == WifiControl.WIFICIPHER_NOPASS) {
            mWifiControl.connectWifi(mWifiToConnect.name, "", mWifiToConnect.passwordType);
        } else {
            InputBox inputBox = new InputBox();
            Bundle bundle = new Bundle();
            bundle.putString("wifi_name", wifiBean.name);
            inputBox.setArguments(bundle);
            inputBox.setInputCallback(this);
            inputBox.show(getFragmentManager(), null);
        }
    }

    @Override
    public void onClick(View v) {
        mSwitchButton.setClickable(false);
        boolean goStatus = !mSwitchButton.isSwitchOn();
        if (goStatus) {
            mWifiControl.openWifi();
        } else {
            mWifiControl.closeWifi();
            mNetworkInfo = null;
        }
    }

    @Override
    public void onRefreshDataSuccess(List<WifiBean> newWifiList) {
        mHandler.obtainMessage(WIFI_REFRESH_DATA, newWifiList).sendToTarget();
    }

    @Override
    public void onWifiStateChanged(int status) {
        refreshView();
    }

    @Override
    public void onConnectStateChanged(NetworkInfo info) {
        if (mNetworkInfo != null && info.getState() == mNetworkInfo.getState()) {
            return;
        }
        mNetworkInfo = info;
        refreshView();
    }

    @Override
    public void confirmCallback(String input) {
        mWifiControl.connectWifi(mWifiToConnect.name, input, mWifiToConnect.passwordType);
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
        int wifiStatus = mWifiControl.getWifiState();
        switch (wifiStatus) {
            case WifiManager.WIFI_STATE_DISABLING:
                mSwitchButton.setSwitchStatus(false);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_offing));
                mWifiPleaseOpen.setVisibility(View.GONE);
                mWifiConnectionContainer.setVisibility(View.GONE);
                mWifiListContainer.setVisibility(View.GONE);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                mSwitchButton.setSwitchStatus(false);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_off));
                mWifiPleaseOpen.setVisibility(View.VISIBLE);
                mWifiConnectionContainer.setVisibility(View.GONE);
                mWifiListContainer.setVisibility(View.GONE);
                mSwitchButton.setClickable(true);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                mSwitchButton.setSwitchStatus(true);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_oning));
                mWifiPleaseOpen.setVisibility(View.GONE);
                mWifiConnectionContainer.setVisibility(View.GONE);
                mWifiListContainer.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                mSwitchButton.setSwitchStatus(true);
                mWifiStatusView.setText(getResources().getString(R.string.wifi_on));
                mWifiPleaseOpen.setVisibility(View.GONE);
                mWifiListContainer.setVisibility(View.VISIBLE);
                mSwitchButton.setClickable(true);
                mAdapter.notifyDataSetChanged();
                if (mNetworkInfo == null) {
                    mWifiConnectionContainer.setVisibility(View.GONE);
                    return;
                }
                if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTING) {
                    mWifiSelectedView.setText(mNetworkInfo.getExtraInfo().replace("\"", ""));
                    mWifiLoading.setVisibility(View.VISIBLE);
                    mWifiLoaded.setVisibility(View.INVISIBLE);
                    mWifiLoading.startAnimation(mLoadingAnim);
                    mWifiConnectionContainer.setVisibility(View.VISIBLE);
                } else if (mNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    mWifiSelectedView.setText(mNetworkInfo.getExtraInfo().replace("\"", ""));
                    mWifiLoading.setVisibility(View.INVISIBLE);
                    mWifiLoaded.setVisibility(View.VISIBLE);
                    mWifiLoading.clearAnimation();
                    mWifiConnectionContainer.setVisibility(View.VISIBLE);
                } else {
                    mWifiConnectionContainer.setVisibility(View.GONE);
                }
        }
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
                viewHolder.wifiLevelView = (ImageView) convertView.findViewById(R.id.wifi_strength);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            WifiBean result = mWifiList.get(position);
            viewHolder.wifiNameView.setText(result.name);
            viewHolder.wifiLockView.setVisibility(result.passwordType == WifiControl.WIFICIPHER_NOPASS ? View.INVISIBLE : View.VISIBLE);
            viewHolder.wifiLevelView.setImageResource(R.drawable.wifi_level);
            viewHolder.wifiLevelView.setImageLevel(result.level);
            return convertView;
        }
    }

    private static class ViewHolder {
        public TextView wifiNameView;
        public ImageView wifiLockView;
        public ImageView wifiLevelView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWifiControl.detachWifiControl();
    }
}
