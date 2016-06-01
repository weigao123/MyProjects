package com.lesports.bike.settings.widget;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesports.bike.settings.R;

/**
 * Created by gaowei3 on 2016/6/1.
 */
public class InputBox extends DialogFragment implements View.OnClickListener {

    private View mRoot;
    private TextView mWifiName;
    private EditText mWifiPassword;
    private ImageView mIsShowPassword;
    private TextView mLeftButton;
    private TextView mRightButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.widget_input_box, container, false);
        return mRoot;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewAndData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setCanceledOnTouchOutside(true);
    }

    public void initViewAndData() {
        mWifiName = (TextView) mRoot.findViewById(R.id.wifi_ssid);
        mWifiPassword = (EditText) mRoot.findViewById(R.id.wifi_password);
        mIsShowPassword = (ImageView) mRoot.findViewById(R.id.wifi_show_password);
        mLeftButton = (TextView) mRoot.findViewById(R.id.input_box_lbtn);
        mRightButton = (TextView) mRoot.findViewById(R.id.input_box_rbtn);

        mLeftButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_box_lbtn:
                dismiss();
        }
    }
}
