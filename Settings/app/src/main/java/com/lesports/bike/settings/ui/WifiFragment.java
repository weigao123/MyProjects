package com.lesports.bike.settings.ui;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
import com.lesports.bike.settings.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gwball on 2016/5/25.
 */
public class WifiFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int WIFI_CLOSED = 1;
    private static final int WIFI_OPENED = 2;
    private static final int WIFI_CONNECTTING = 3;
    private static final int WIFI_CONNECTTED = 4;

    private WifiManager mWifiManager;
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

        mWifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            refreshWifiList();
            mWifiStatus = WIFI_OPENED;
        } else {
            mWifiStatus = WIFI_CLOSED;
        }
        refreshView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onClick(View v) {
        boolean status = mSwitchButton.isSwitchOn();
        boolean result = mWifiManager.setWifiEnabled(!status);
        if (result) {
            mWifiStatus = status? WIFI_CLOSED : WIFI_OPENED;
            refreshView();
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

    private void refreshView() {
        if (mWifiStatus == WIFI_CLOSED) {
            mSwitchButton.setSwitchStatus(false);
            mWifiStatusView.setText(getResources().getString(R.string.wifi_off));
            mWifiPleaseOpen.setVisibility(View.VISIBLE);
            mWifiCurrentContainer.setVisibility(View.GONE);
            mWifiCandidateContainer.setVisibility(View.GONE);
        } else {
            if (mWifiStatus == WIFI_OPENED) {
                mWifiCurrentContainer.setVisibility(View.GONE);
            } else {
                if (mWifiStatus == WIFI_CONNECTTING) {
                    mWifiLoading.setVisibility(View.VISIBLE);
                    mWifiLoaded.setVisibility(View.GONE);
                }
                if (mWifiStatus == WIFI_CONNECTTED) {
                    mWifiLoading.setVisibility(View.GONE);
                    mWifiLoaded.setVisibility(View.VISIBLE);
                }
                mWifiSelectedView.setText(mWifiSelectedName);
                mWifiCurrentContainer.setVisibility(View.VISIBLE);
            }

            mSwitchButton.setSwitchStatus(true);
            mWifiStatusView.setText(getResources().getString(R.string.wifi_on));
            mWifiPleaseOpen.setVisibility(View.GONE);
            mWifiCandidateContainer.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void refreshWifiList() {
        mWifiManager.startScan();
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        mWifiList.clear();

        HashMap<String, WifiBean> wifiMap = new HashMap<String, WifiBean>();
        for (ScanResult result : scanResults) {
            WifiBean wifiBean = new WifiBean();
            wifiBean.name = result.SSID;
            wifiBean.isLock = result.capabilities.contains("WPA");
            wifiBean.level = Math.abs(result.level);
            if (!wifiMap.containsKey(wifiBean.name)) {
                mWifiList.add(wifiBean);
                wifiMap.put(wifiBean.name, wifiBean);
            }
        }
        Collections.sort(mWifiList, new Comparator<WifiBean>() {
            @Override
            public int compare(final WifiBean con1, final WifiBean con2) {
                if (con1.level == con2.level) {
                    return 0;
                } else if (con2.level > con1.level) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    private class WifiBean {
        String name;
        boolean isLock;
        int level;
    }
}
