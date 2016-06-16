package com.lesports.bike.settings.ui;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.ActivityUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * Created by gwball on 2016/5/25.
 */
public class AboutFragment extends BaseFragment implements OnClickListener {
	private View aboutView;

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		aboutView = inflater.inflate(R.layout.fragment_about, container, false);
		return aboutView;
	}

	@Override
	protected void initViewAndData() {
		aboutView.findViewById(R.id.about_help).setOnClickListener(this);
		aboutView.findViewById(R.id.about_tos).setOnClickListener(this);
	}

	@Override
	protected String getTitleName() {
		return getResources().getString(R.string.about_bike);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_help:
			ActivityUtils.startFragmentActivity(getActivity(),
					HelpFragment.class);
			break;
		case R.id.about_tos:
			ActivityUtils.startFragmentActivity(getActivity(),
					TOSFragment.class);
			break;

		default:
			break;
		}
	}
}
