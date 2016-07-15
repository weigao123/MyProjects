package com.lesports.bike.settings.ui;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.ActivityUtils;
import com.lesports.bike.settings.utils.SystemUtils;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by gwball on 2016/5/25.
 */
public class AboutFragment extends BaseFragment implements OnClickListener {
	private View aboutView;
	private TextView versionTextView;
	private TextView modelTextView;
	private TextView macTextView;
	private TextView ipTextView;
	private TextView imeiTextView;
	private TextView subVersionTextView;

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		aboutView = inflater.inflate(R.layout.fragment_about, container, false);
		return aboutView;
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {
		SystemUtils systemUtils = new SystemUtils(getActivity());
		imeiTextView.setText(systemUtils.getIMEI());
		macTextView.setText(systemUtils.getMac());
		ipTextView.setText(systemUtils.getIp());
		modelTextView.setText(android.os.Build.MODEL);
		versionTextView.setText(android.os.Build.VERSION.RELEASE);
		subVersionTextView.setText(android.os.Build.DISPLAY);
	}

	@Override
	protected void initViewAndData() {
		versionTextView = (TextView) aboutView.findViewById(R.id.about_version);
		modelTextView = (TextView) aboutView.findViewById(R.id.about_model_tv);
		macTextView = (TextView) aboutView.findViewById(R.id.about_mac_tv);
		ipTextView = (TextView) aboutView.findViewById(R.id.about_ip_tv);
		imeiTextView = (TextView) aboutView.findViewById(R.id.about_imei_tv);
		subVersionTextView = (TextView) aboutView
				.findViewById(R.id.about_version_2);
		aboutView.findViewById(R.id.about_sys_version).setOnClickListener(this);
		aboutView.findViewById(R.id.about_help).setOnClickListener(this);
		aboutView.findViewById(R.id.about_tos).setOnClickListener(this);
		aboutView.findViewById(R.id.ip_address).setOnClickListener(this);
	}

	@Override
	protected String getTitleName() {
		return getResources().getString(R.string.about_bike);
	}

	private long mClickTime;
	private int mClickTimes;
	@Override
	public void onClick(View v) {

		Intent intent = new Intent(Intent.ACTION_MAIN);
		switch (v.getId()) {
		case R.id.about_help:
			ActivityUtils.startFragmentActivity(getActivity(),
					HelpFragment.class);
			break;
		case R.id.about_tos:
			ActivityUtils.startFragmentActivity(getActivity(),
					TOSFragment.class);
			break;
		case R.id.about_sys_version:
	        intent.setClassName("com.adups.fota","com.adups.fota.GoogleOtaClient");
	        try {
	            startActivity(intent);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
			break;
		case R.id.ip_address:
			long now = System.currentTimeMillis();
			if (now - mClickTime < 500) {
				mClickTimes ++;
			} else {
				mClickTimes = 0;
			}
			mClickTime = now;
			if (mClickTimes >= 4) {
				ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.Settings");
				intent.setComponent(cm);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				try {
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		default:
			break;
		}
	}
}
