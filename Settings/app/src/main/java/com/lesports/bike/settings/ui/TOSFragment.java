package com.lesports.bike.settings.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.ActivityUtils;

public class TOSFragment extends BaseFragment implements OnClickListener {

	private View tosView;

	@Override
	protected void initViewAndData() {
		tosView.findViewById(R.id.item_tos_layout).setOnClickListener(this);
		tosView.findViewById(R.id.item_pa_layout).setOnClickListener(this);
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		tosView = inflater.inflate(R.layout.fragment_tos, null);
		return tosView;
	}

	@Override
	protected String getTitleName() {
		return getResources().getString(R.string.tos_home_title);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_tos_layout:
			Bundle bundle = new Bundle();
			bundle.putInt("itemId", 0);
			ActivityUtils.startFragmentActivity(getActivity(),
					TosDetailFragment.class, bundle);
			break;
		case R.id.item_pa_layout:

			Bundle bundle1 = new Bundle();
			bundle1.putInt("itemId", 1);
			ActivityUtils.startFragmentActivity(getActivity(),
					TosDetailFragment.class, bundle1);
			break;
		}
	}

}
