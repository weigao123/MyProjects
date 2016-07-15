
package com.lesports.bike.settings.control;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.IBluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BluetoothControl {
    private BluetoothAdapter mBluetoothAdapter;
    private String TAG = "iii";
    private BluetoothControlCallback callback;
    private List<BluetoothDevice> bluetoothList = new ArrayList<BluetoothDevice>();
    private BluetoothDevice pairDevice;
    public IBluetoothA2dp mA2dpService;
    private static BluetoothControl bluetoothManager;
    private Context context;
    private int REFRESH_INTERVAL = 10000;
    private BluetoothDevice lastPairDevice;
    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mA2dpService = IBluetoothA2dp.Stub.asInterface(service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

    };

    private BluetoothControl(Context context) {
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothControl getInstance(Context context) {
        if (bluetoothManager == null)
            bluetoothManager = new BluetoothControl(context);
        return bluetoothManager;
    }

    public void register() {
        Log.d(TAG, "注册监听");
        IntentFilter bluetoothFilter = new IntentFilter();
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        context.registerReceiver(BluetoothReciever, bluetoothFilter);
        IntentFilter btDiscoveryFilter = new IntentFilter();
        btDiscoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        btDiscoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        btDiscoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        btDiscoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(BTDiscoveryReceiver, btDiscoveryFilter);
        initA2dpService();
    }

    private void initA2dpService() {
        Log.d(TAG, "初始化蓝牙连接");
        // Intent i = getExplicitIntent(mContext,new
        // Intent(IBluetoothA2dp.class.getName()));//5.0以上系统需要显示intent
        // 详细参考http://blog.csdn.net/l2show/article/details/47421961
        Intent i = new Intent(IBluetoothA2dp.class.getName());
        boolean success = context.bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unRegister() {
        Log.d(TAG, "取消注册");
        context.unregisterReceiver(BluetoothReciever);
        context.unregisterReceiver(BTDiscoveryReceiver);
    }

    public boolean isSupportBluetooth() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "该设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean open() {
        return mBluetoothAdapter.enable();
    }

    public boolean close() {
        return mBluetoothAdapter.disable();
    }

    public int getBluetoothState() {
        return mBluetoothAdapter.getState();
    }

    public void searchBlueDevice() {
        Log.d(TAG, "搜索蓝牙设备");
        if (!mBluetoothAdapter.isDiscovering()) {
            Log.i(TAG, "开始搜索");
            mBluetoothAdapter.startDiscovery();
        } else {
            Log.i(TAG, "搜索中．．．");
        }
    }

    public BluetoothDevice getPairDevice() {
        return pairDevice;
    }

    public void searchPairDevice() {
        Log.d(TAG, "搜索配对设备");
        Set<BluetoothDevice> bts = mBluetoothAdapter.getBondedDevices();
        Iterator<BluetoothDevice> iterator = bts.iterator();
        BluetoothDevice bd = null;
        while (iterator.hasNext()) {
            bd = iterator.next();
            Log.d(TAG, "配对设备名称 : " + bd.getName() + "\n配对设备地址 ："
                    + bd.getAddress());
        }
        pairDevice = bd;
        callback.onDevicePairSucess(bd);
    }

    private BroadcastReceiver BTDiscoveryReceiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    if (btDevice != null && bluetoothList.indexOf(btDevice) == -1) {
                        bluetoothList.add(btDevice);
                    }
                    callback.onBluetoothListChanges(bluetoothList);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, btDevice.getName() + " : 配对中　 ......");
                    bluetoothList.remove(btDevice);
                    callback.onBluetoothListChanges(bluetoothList);
                } else if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, btDevice.getName() + " : 配对成功 !!!!");
                    callback.onDevicePairSucess(btDevice);
                    pairDevice = btDevice;
                    try {
                        Log.d(TAG, btDevice.getName() + " : 连接中　 ．．．．");
                        mA2dpService.connect(btDevice);
                    } catch (RemoteException e) {
                        Log.d(TAG, " 连接异常 :" + e.toString());
                        e.printStackTrace();
                    }
                } else if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, btDevice.getName() + " : 配对失败/取消配对");
                    callback.onDevicePairFail(btDevice);
                    bluetoothList.add(btDevice);
                    callback.onBluetoothListChanges(bluetoothList);
                } else if (btDevice.getBondState() == BluetoothDevice.UNBOND_REASON_AUTH_TIMEOUT) {
                    Log.d(TAG, btDevice.getName() + " : 连接超时");
                    callback.onDevicePairFail(btDevice);
                    bluetoothList.add(btDevice);
                    callback.onBluetoothListChanges(bluetoothList);
                } else {
                    Log.d(TAG, "其它");
                }
            }
        }

    };

    private BroadcastReceiver BluetoothReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int btState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.STATE_OFF);
                callback.onRefreshUI(btState);
            }
        }
    };

    public BluetoothDevice getClickDevice(int index) {
        return bluetoothList.get(index);
    }

    public BluetoothDevice getListPairDevice() {
        return lastPairDevice;
    }

    public void cancelPair(BluetoothDevice device) {
        Log.d(TAG, device.getName() + "取消配对");
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e(TAG, "取消配对异常 ：" + e.getMessage());
        }
    }

    public void bluetoothPair(BluetoothDevice device) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPair(String value, BluetoothDevice mDevice, int mType) {
        switch (mType) {
            case BluetoothDevice.PAIRING_VARIANT_PIN:
                byte[] pinBytes = BluetoothDevice.convertPinToBytes(value);
                if (pinBytes == null) {
                    return;
                }
                mDevice.setPin(pinBytes);
                break;
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
                int passkey = Integer.parseInt(value);
                mDevice.setPasskey(passkey);
                break;
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
            case BluetoothDevice.PAIRING_VARIANT_CONSENT:
                mDevice.setPairingConfirmation(true);
                break;
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
                // Do nothing.
                break;
            case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
                mDevice.setRemoteOutOfBandData();
                break;
            default:
                Log.e(TAG, "Incorrect pairing type received");
        }
    }

    public void setCallback(BluetoothControlCallback callback) {
        this.callback = callback;
    }

    public interface BluetoothControlCallback {
        void onRefreshUI(int state);

        void onBluetoothListChanges(List<BluetoothDevice> list);

        void onDevicePairSucess(BluetoothDevice device);

        void onDevicePairFail(BluetoothDevice device);
    }

    private CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, REFRESH_INTERVAL) {
        @Override
        public void onTick(long millisUntilFinished) {
            searchBlueDevice();
        }

        @Override
        public void onFinish() {
        }
    };

    public void startCountdown() {
        timer.start();
    }

    public void stopCountdown() {
        timer.cancel();
    }

}
