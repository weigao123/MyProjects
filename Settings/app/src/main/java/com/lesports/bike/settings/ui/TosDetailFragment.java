package com.lesports.bike.settings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lesports.bike.settings.R;

public class TosDetailFragment extends BaseFragment {

	private View tosDetailView;
	private int id = -1;

	@Override
	protected void initViewAndData() {
		TextView contentView = (TextView) tosDetailView
				.findViewById(R.id.tos_content);
		if (id == 0) {
			contentView.setText(getResources()
					.getString(R.string.tos_content_1));
		} else if (id == 1) {
			contentView.setText(getResources()
					.getString(R.string.tos_content_2));
		}
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		tosDetailView = inflater.inflate(R.layout.fragment_tos_detail, null);
		id = getActivity().getIntent().getIntExtra("itemId", -1);
		return tosDetailView;
	}

	@Override
	protected String getTitleName() {
		String title="";
		if (id == 0) {
			title = getResources().getString(R.string.tos_item_1);
		} else if (id == 1) {
			title = getResources().getString(R.string.tos_item_2);
		}
		return title;
	}

}
