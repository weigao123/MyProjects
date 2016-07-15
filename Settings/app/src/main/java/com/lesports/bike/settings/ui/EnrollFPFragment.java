package com.lesports.bike.settings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.control.FingerprintControl;
import com.lesports.bike.settings.widget.FingerProgressBar;

/**
 * Created by gaowei3 on 2016/6/6.
 */
public class EnrollFPFragment extends BaseFragment implements FingerprintControl.FPControlCallback{

    private FingerprintControl mFingerprintControl;
    private TextView mProgress;
    private TextView mEnrollTip;
    private RelativeLayout mEnrollConfirm;
    private FingerProgressBar mFingerProgressBar;

    @Override
    protected void initViewAndData() {
        mFingerprintControl = FingerprintControl.getInstance(getActivity());
        mFingerprintControl.register();
        mFingerprintControl.setCallback(this);
        mEnrollConfirm = (RelativeLayout) getActivity().findViewById(R.id.fp_enroll_confirm);
        mEnrollTip = (TextView) getActivity().findViewById(R.id.fp_enroll_tip);
        mFingerProgressBar = (FingerProgressBar) getActivity().findViewById(R.id.fp_enroll_progress);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enroll_fp, null);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.fp_add_fp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFingerprintControl.cancelEnroll();
    }

    @Override
    public void onEnrollProgress(int progress) {
        mFingerProgressBar.setProgress(progress);
        if (progress >= 100) {
            mEnrollTip.setText(getResources().getText(R.string.fp_enroll_tip2));
            mEnrollConfirm.setVisibility(View.VISIBLE);
            mEnrollConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
    }
}
