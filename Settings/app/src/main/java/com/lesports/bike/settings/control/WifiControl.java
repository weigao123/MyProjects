package com.lesports.bike.settings.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.widget.Toast;

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
    private int TIME_REFRESH = 5000;

    public static final int WIFICIPHER_NOPASS = 1;
    public static final int WIFICIPHER_WEP = 2;
    public static final int WIFICIPHER_WPA = 3;

    private String mConnectedTarget;

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

    public void attachWifiControl() {
        IntentFilter filters= new IntentFilter();
        filters.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filters.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filters);
        if (mWifiManager.isWifiEnabled()) {
            getWifiListLoop();
        }
    }

    public void detachWifiControl() {
        context.unregisterReceiver(mReceiver);
        timer.cancel();
    }

    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    public void getWifiListLoop() {
        timer.start();
    }

    private CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, TIME_REFRESH) {
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
            if (result.capabilities.contains("WEP") || result.capabilities.contains("wep")) {
                wifiBean.passwordType = WIFICIPHER_WEP;
            } else if (result.capabilities.contains("WPA") || result.capabilities.contains("wpa")) {
                wifiBean.passwordType = WIFICIPHER_WPA;
            } else {
                wifiBean.passwordType = WIFICIPHER_NOPASS;
            }
            wifiBean.level = Math.abs(result.level);
            if (!wifiMap.containsKey(wifiBean.name) && (!wifiBean.name.equals(mConnectedTarget))) {
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
                callback.onWifiStateChanged(wifiState);
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    callback.onConnectStateChanged(networkInfo);
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        mConnectedTarget = networkInfo.getExtraInfo().replace("\"", "");
                        refreshWifiList();
                    }
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo tmpInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            }
        }
    };

    /*----------------------------------------------------------------------------------------------*/

    public void connectWifi(String ssid, String password, int type) {
        if (getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            new ConnectWifiThread().execute(ssid, password, type + "");
        } else {
            Toast.makeText(context, "wifi is not ready.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 配置wifi
     */
    private WifiConfiguration createWifiInfo(String SSID, String Password, int Type, WifiManager wifiManager) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = isExist(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type == WIFICIPHER_NOPASS)
        {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == WIFICIPHER_WEP)
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WIFICIPHER_WPA)
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 查看wifi自带的配置列表中是否配置过这个网络,有的话返回配置信息
     */
    private WifiConfiguration isExist(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 连接wifi
     */
    class ConnectWifiThread extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String SSID = params[0];
            int Type = Integer.parseInt(params[2]);
            // 连接配置好指定ID的网络
            WifiConfiguration config = createWifiInfo(SSID, params[1], Type, mWifiManager);
            WifiInfo mInfo = mWifiManager.getConnectionInfo();
            if (mInfo != null) {
                mWifiManager.disableNetwork(mInfo.getNetworkId());
            }
            boolean b = false;
            if (config.networkId > 0) {
                b = mWifiManager.enableNetwork(config.networkId, true);
                mWifiManager.updateNetwork(config);
            } else {
                int netId = mWifiManager.addNetwork(config);
                if (netId >= 0) {
                    mWifiManager.saveConfiguration();
                    b = mWifiManager.enableNetwork(netId, true);
                } else {
                }

            }
            return b ? SSID + "" : null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public class WifiBean {
        public String name;
        public int passwordType;
        public int level;
    }

    public void setCallback(WifiControlCallback callback) {
        this.callback = callback;
    }

    public interface WifiControlCallback {
        void onRefreshDataSuccess(List<WifiBean> newWifiList);
        void onWifiStateChanged(int status);
        void onConnectStateChanged(NetworkInfo info);
    }
}
