package com.lesports.bike.settings.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.http.VolleyHelper;
import com.lesports.bike.settings.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwball on 2016/5/25.
 */
public class DataUsageFragment extends BaseFragment implements View.OnClickListener {
    private SwitchButton mSwitchButton;
    private TextView mDataStatusView;
    private TextView mDataRest;
    private ImageView mDataLoadingView;
    private ConnectivityManager mConnectivityManager;
    private boolean mLoading;
    private Animation mLoadingAnim;
    private VolleyHelper mVolleyHelper;

    @Override
    protected void initViewAndData() {
        mSwitchButton = (SwitchButton) getActivity().findViewById(R.id.data_switch);
        mDataStatusView = (TextView) getActivity().findViewById(R.id.data_status);
        mDataRest = (TextView) getActivity().findViewById(R.id.data_of_the_rest);
        mDataLoadingView = (ImageView) getActivity().findViewById(R.id.data_loading);
        mSwitchButton.setOnClickListener(this);
        mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        mLoadingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.loading_anim);
        mLoadingAnim.setInterpolator(new LinearInterpolator());

        IntentFilter filters = new IntentFilter();
        filters.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mReceiver, filters);

        loadDataUsage();
        refreshView();
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_datausage, container, false);
    }

    @Override
    protected String getTitleName() {
        return getString(R.string.data_usage);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (mConnectivityManager.getActiveNetworkInfo() != null && mConnectivityManager.getActiveNetworkInfo().isAvailable()) {
                    loadDataUsage();
                }
                refreshView();
            }
        }
    };

    private void refreshView() {
        mSwitchButton.setSwitchStatus(mConnectivityManager.getMobileDataEnabled());
        mDataStatusView.setText(mConnectivityManager.getMobileDataEnabled() ? getString(R.string.data_on) : getString(R.string.data_off));
        if (mLoading) {
            mDataLoadingView.setVisibility(View.VISIBLE);
            mDataLoadingView.startAnimation(mLoadingAnim);
        } else {
            mDataLoadingView.clearAnimation();
            mDataLoadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataLoadingView.clearAnimation();
        getActivity().unregisterReceiver(mReceiver);
        mVolleyHelper.cancel();
    }

    @Override
    public void onClick(View v) {
        if (mSwitchButton.isSwitchOn()) {
            mConnectivityManager.setMobileDataEnabled(false);
        } else {
            mConnectivityManager.setMobileDataEnabled(true);
        }
//        mDataStatusView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshView();
//            }
//        }, 1000);
    }

    private void loadDataUsage() {
        Map<String, String> params = new HashMap<String, String>();
        String iccid = getIccid(getActivity());
        if (mLoading || iccid == null) {
            return;
        }
        mLoading = true;
        params.put("iccid", iccid);
        mVolleyHelper = new VolleyHelper().setPath("/sim/monthlyDataUsage").setParameters(params);
        mVolleyHelper.startStringRequest(new VolleyHelper.RequestResult() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                mLoading = false;
                setDataValue(jsonObject);
            }
            @Override
            public void onError() {
                mLoading = false;
                setDataValue(null);
            }
        });
    }

    private void setDataValue(final JSONObject jsonObject) {
        mDataStatusView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (jsonObject != null) {
                        mDataRest.setText(jsonObject.getString("data") + " M");
                    } else {
                        mDataRest.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refreshView();
            }
        });
    }

    private String getIccid(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        if (TextUtils.isEmpty(simSerialNumber)) {
            return null;
        } else {
            simSerialNumber = simSerialNumber.substring(0, simSerialNumber.length() - 1);
            return simSerialNumber;
        }
    }
}
