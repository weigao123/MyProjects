package com.lesports.bike.settings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.control.FingerprintControl;
import com.lesports.bike.settings.utils.ActivityUtils;
import com.lesports.bike.settings.utils.SizeUtils;
import com.lesports.bike.settings.utils.PopupUtils;
import com.lesports.bike.settings.utils.UIUtils;

/**
 * Created by gaowei3 on 2016/6/3.
 */
public class FingerprintFragment extends BaseFragment {
    private static int[] mFpName = new int[]{R.string.fp_fp1, R.string.fp_fp2, R.string.fp_fp3,
            R.string.fp_fp4, R.string.fp_fp5};
    private FingerprintControl mFingerprintControl;
    @Override
    protected void initViewAndData() {
        mFingerprintControl = FingerprintControl.getInstance(getActivity());
    }

    private void refreshUI() {
        LinearLayout root = (LinearLayout) getActivity().findViewById(R.id.fp_root);
        root.removeAllViews();
        boolean[] register = mFingerprintControl.loadFingerprints();
        for (int i = 0; i < register.length; i++) {
            if (register[i]) {
                final int position = i;
                RelativeLayout item = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fp_list_item, null);
                ((TextView)item.findViewById(R.id.fp_name)).setText(getResources().getText(mFpName[i]));
                item.findViewById(R.id.fp_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDeleteItem(position + 1);
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(getActivity(), 55));
                item.setLayoutParams(params);
                root.addView(item);
            }
        }
        if (root.getChildCount() < 5) {
            RelativeLayout item = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.fp_list_item, null);
            ((TextView)item.findViewById(R.id.fp_name)).setText(getResources().getText(R.string.fp_add_fp));
            item.findViewById(R.id.fp_delete).setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(getActivity(), 55));
            item.setLayoutParams(params);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startFragmentActivity(getActivity(), EnrollFPFragment.class);
                }
            });
            root.addView(item);
        }
    }
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.fp_manager);
    }

    private void onDeleteItem(final int position) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fp_delete_fp, null);
        TextView textView1 = (TextView) view.findViewById(R.id.fp_del_cancel);
        TextView textView2 = (TextView) view.findViewById(R.id.fp_del_confirm);
        UIUtils.makeColor(textView1, 13, 33, getResources().getColor(R.color.green), getResources().getColor(R.color.blue));
        UIUtils.makeColor(textView2, 13, 33, getResources().getColor(R.color.green), getResources().getColor(R.color.blue));
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupUtils.removeView(getActivity(), view);
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFingerprintControl.deleteFingerprint(position);
                PopupUtils.removeView(getActivity(), view);
                refreshUI();
            }
        });
        PopupUtils.popupView(getActivity(), view);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }
}
