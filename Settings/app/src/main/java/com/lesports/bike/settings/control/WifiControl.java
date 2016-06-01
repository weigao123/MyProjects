package com.lesports.bike.settings.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gaowei3 on 2016/6/1.
 */
public class WifiControl {

    private static WifiControl instance;
    private WifiManager mWifiManager;
    private WifiControlCallback callback;
    private Context context;

    private WifiControl(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiControl getInstance(Context context) {
        if (instance == null) {
            instance = new WifiControl(context.getApplicationContext());
        }
        return instance;
    }

    public void openWifi() {
        mWifiManager.setWifiEnabled(true);
        getWifiListLoop();
    }

    public void closeWifi() {
        mWifiManager.setWifiEnabled(false);
        timer.cancel();
    }

    public void initWifi() {
        IntentFilter filters= new IntentFilter();
        filters.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filters);
        if (mWifiManager.isWifiEnabled()) {
            getWifiListLoop();
        }
    }

    public void detachWifi() {
        context.unregisterReceiver(mReceiver);
        timer.cancel();
    }

    public int getWifiStatus() {
        return mWifiManager.getWifiState();
    }

    public void getWifiListLoop() {
        timer.start();
    }
    private CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, 5000) {
        @Override
        public void onTick(long millisUntilFinished) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mWifiManager.startScan();
                    refreshWifiList();
                }
            }).start();
        }
        @Override
        public void onFinish() {
        }
    };

    public void refreshWifiList() {
        final List<WifiBean> wifiBeanList = new ArrayList<WifiBean>();
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        wifiBeanList.clear();
        HashMap<String, WifiBean> wifiMap = new HashMap<String, WifiBean>();
        for (ScanResult result : scanResults) {
            WifiBean wifiBean = new WifiBean();
            wifiBean.name = result.SSID;
            wifiBean.isLock = result.capabilities.contains("WPA");
            wifiBean.level = Math.abs(result.level);
            if (!wifiMap.containsKey(wifiBean.name)) {
                wifiBeanList.add(wifiBean);
                wifiMap.put(wifiBean.name, wifiBean);
            }
        }
        Collections.sort(wifiBeanList, new Comparator<WifiBean>() {
            @Override
            public int compare(final WifiBean con1, final WifiBean con2) {
                if (con1.level == con2.level) {
                    return 0;
                } else if (con2.level > con1.level) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        if (callback != null) {
            callback.onRefreshDataSuccess(wifiBeanList);
        }
    }

    public void setCallback(WifiControlCallback callback) {
        this.callback = callback;
    }

    public interface WifiControlCallback {
        void onRefreshDataSuccess(List<WifiBean> newWifiList);
        void onStatusChanged(int status);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    getWifiListLoop();
                } else if (wifiState == WifiManager.WIFI_STATE_DISABLED){
                    timer.cancel();
                }
                callback.onStatusChanged(wifiState);
            }
        }
    };

    public class WifiBean {
        public String name;
        public boolean isLock;
        public int level;
    }
}
