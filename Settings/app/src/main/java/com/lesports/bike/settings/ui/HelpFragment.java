package com.lesports.bike.settings.ui;

import com.lesports.bike.settings.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends BaseFragment {

	private View helpView;

	@Override
	protected void initViewAndData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		helpView = inflater.inflate(R.layout.fragment_help, null);
		return helpView;
	}

	@Override
	protected String getTitleName() {
		return getResources().getString(R.string.about_help);
	}

}
