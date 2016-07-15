
package com.lesports.bike.settings.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.adapter.BluetoothAdapter;
import com.lesports.bike.settings.control.BluetoothControl;
import com.lesports.bike.settings.control.BluetoothControl.BluetoothControlCallback;
import com.lesports.bike.settings.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

/**
 * bluetooth home page
 * 
 * @author liutingting5
 */
public class BluetoothFragment extends BaseFragment
        implements View.OnClickListener, BluetoothControlCallback {

    private TextView mBlueState;
    private SwitchButton mSwitchButton;
    private BluetoothAdapter mBlueDeviceAdapter;
    private List<BluetoothDevice> mBlueDeviceList = new ArrayList<BluetoothDevice>();
    private BluetoothControl mBlueControl;
    private LinearLayout mBluePairDeviceBox, mBlueDeviceBox;
    private TextView mCurrentSelectDevice;
    private ImageView loading, loaded;
    private Animation mLoadingAnim;
    private ListView listView;
    private String TAG = "iii";
    private int requestNumber = -1;
    private boolean isFirstConn = true;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mBlueControl = BluetoothControl.getInstance(getActivity());
        if (!mBlueControl.isSupportBluetooth()) {
            exit();
            return null;
        }
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.bluetooth);
    }

    @Override
    protected void initViewAndData() {
        mBluePairDeviceBox = (LinearLayout) getActivity().findViewById(R.id.cur_bluetooth_list_box);
        mBlueDeviceBox = (LinearLayout) getActivity().findViewById(R.id.bluetooth_list_box);
        mBlueState = (TextView) getActivity().findViewById(R.id.bluetooth_status);
        mSwitchButton = (SwitchButton) getActivity().findViewById(R.id.bluetooth_switch);
        listView = (ListView) getActivity().findViewById(R.id.bluetooth_listview);
        mCurrentSelectDevice = (TextView) getActivity().findViewById(R.id.blue_cur_select);
        loading = (ImageView) getActivity().findViewById(R.id.loading);
        loaded = (ImageView) getActivity().findViewById(R.id.loaded);
        mLoadingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.loading_anim);
        mLoadingAnim.setInterpolator(new LinearInterpolator());

        mBlueDeviceAdapter = new BluetoothAdapter(getActivity());
        listView.setAdapter(mBlueDeviceAdapter);

        mSwitchButton.setOnClickListener(this);
        mCurrentSelectDevice.setOnClickListener(this);
        mBlueControl.setCallback(this);
        mBlueControl.register();

        onRefreshUI(mBlueControl.getBluetoothState());

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (mBlueControl.getPairDevice() != null) {
                    Log.d(TAG, "取消已配对设备：" +
                            mBlueControl.getPairDevice().getName());
                    requestNumber++;
                    mBlueControl.cancelPair(mBlueControl.getPairDevice());
                }

                BluetoothDevice device = mBlueControl.getClickDevice(arg2);
                Log.d(TAG, "连接当前设备：" + device.getName());
                mBluePairDeviceBox.setVisibility(View.VISIBLE);
                if (device.getName() != null)
                    mCurrentSelectDevice.setText(device.getName());
                else
                    mCurrentSelectDevice.setText(device.getAddress());
                loading.setVisibility(View.VISIBLE);
                loading.startAnimation(mLoadingAnim);
                loaded.setVisibility(View.GONE);
                mBlueControl.bluetoothPair(device);
            }
        });

        mCurrentSelectDevice.setOnClickListener(this);

    }

    @Override
    public void onRefreshUI(int state) {
        switch (state) {
            case android.bluetooth.BluetoothAdapter.STATE_OFF:
                Log.d(TAG, "蓝牙关闭");
                mSwitchButton.setSwitchStatus(false);
                mBlueState.setText(getResources().getString(R.string.wifi_off));
                mSwitchButton.setClickable(true);
                mBluePairDeviceBox.setVisibility(View.GONE);
                mBlueDeviceBox.setVisibility(View.GONE);
                Log.d(TAG, "关闭定时");
                mBlueControl.stopCountdown();
                break;
            case android.bluetooth.BluetoothAdapter.STATE_TURNING_ON:
                Log.d(TAG, "蓝牙正在打开");
                mSwitchButton.setSwitchStatus(false);
                mBlueState.setText(getResources().getString(R.string.wifi_oning));
                break;
            case android.bluetooth.BluetoothAdapter.STATE_ON:
                Log.d(TAG, "蓝牙打开");
                mSwitchButton.setSwitchStatus(true);
                mBlueState.setText(getResources().getString(R.string.wifi_on));
                mSwitchButton.setClickable(true);
                Log.d(TAG, "更新数据");
                mBlueControl.searchPairDevice();
                mBlueControl.searchBlueDevice();
                Log.d(TAG, "打开定时");
                mBlueControl.startCountdown();
                requestNumber = -1;
                break;
            case android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(TAG, "蓝牙正在关闭");
                mSwitchButton.setSwitchStatus(true);
                mBlueState.setText(getResources().getString(R.string.wifi_offing));
        }

    }

    @Override
    public void onBluetoothListChanges(List<BluetoothDevice> list) {
        if (list != null && list.size() > 0) {
            mBlueDeviceBox.setVisibility(View.VISIBLE);
            mBlueDeviceAdapter.refreshList(list);
        } else {
            mBlueDeviceBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDevicePairSucess(BluetoothDevice device) {
        Log.d(TAG, "蓝牙配对成功回调");
        if (device != null) {
            requestNumber = -1;
            isFirstConn = false;
            Log.d(TAG, " isFirstConn = false;");
            mBluePairDeviceBox.setVisibility(View.VISIBLE);
            if (device.getName() != null)
                mCurrentSelectDevice.setText(device.getName());
            else
                mCurrentSelectDevice.setText(device.getAddress());
            loading.clearAnimation();
            loading.setVisibility(View.GONE);
            loaded.setVisibility(View.VISIBLE);
        } else {
            loading.clearAnimation();
            mBluePairDeviceBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDevicePairFail(BluetoothDevice device) {
        Log.d(TAG, requestNumber + "次蓝牙配对失败回调 : " + device.getName());
        Log.d(TAG, "isFirstConn = " + isFirstConn);
        if (isFirstConn) {
            Log.d(TAG, " 首次连接失败");
            mBluePairDeviceBox.setVisibility(View.GONE);
            loading.clearAnimation();
            requestNumber = -1;
        } else {
            if (requestNumber == 0) {
                // 取消连接
                requestNumber++;
            } else if (requestNumber == 1) {
                // 连接失败
                mBluePairDeviceBox.setVisibility(View.GONE);
                loading.clearAnimation();
                requestNumber = -1;
                isFirstConn = true;
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBlueControl != null) {
            mBlueControl.unRegister();
            mBlueControl.stopCountdown();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.blue_cur_select:
                // mBlueControl.cancelPair(mBlueControl.getPairDevice());
                try {
                    Log.d(TAG, "conn device");
                    mBlueControl.mA2dpService.connect(mBlueControl.getPairDevice());
                } catch (RemoteException e) {
                    Log.d(TAG, "conn device exception : " + e.toString());
                    e.printStackTrace();
                }
                break;
            case R.id.bluetooth_switch:
                mSwitchButton.setClickable(false);
                boolean status = !mSwitchButton.isSwitchOn();
                if (status) {
                    Log.d(TAG, "打开蓝牙");
                    mBlueControl.open();
                } else {
                    Log.d(TAG, "关闭蓝牙");
                    mBlueControl.close();
                }
                break;
        }

    }

}
