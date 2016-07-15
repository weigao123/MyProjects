package com.lesports.bike.settings.ui;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.widget.LockPatternUtils;
import com.lesports.bike.settings.R;
import com.lesports.bike.settings.utils.ActivityUtils;
import com.lesports.bike.settings.utils.PopupUtils;
import com.lesports.bike.settings.widget.PasswordInput;

/**
 * Created by gaowei3 on 2016/5/25.
 */
public class PasswordFragment extends BaseFragment implements View.OnClickListener {

    public static final String PASSWORD_TODO = "todo";
    public static final int PASSWORD_VERIFY = 1;
    public static final int PASSWORD_ON_LOCK = 2;
    public static final int PASSWORD_OFF_LOCK = 3;
    public static final int PASSWORD_MODIFY = 4;

    private LockPatternUtils mLockPatternUtils;
    PasswordInput mPasswordInput;
    String[] mInput = new String[]{"", "", "", ""};

    private TextView mPasswordTitle;
    private TextView mPasswordWrongTip;

    private int mTodo = 0;
    private String mLastInput = "";
    private Handler mHandle;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password, container, false);
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.password_manager);
    }

    @Override
    protected void initViewAndData() {
        getActivity().findViewById(R.id.password_button_0).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_1).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_2).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_3).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_4).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_5).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_6).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_7).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_8).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_9).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_cancel).setOnClickListener(this);
        getActivity().findViewById(R.id.password_button_delete).setOnClickListener(this);
        mPasswordInput = (PasswordInput)getActivity().findViewById(R.id.password_input);
        mPasswordWrongTip = (TextView)getActivity().findViewById(R.id.password_wrong_tip);
        mPasswordTitle = (TextView)getActivity().findViewById(R.id.password_title);

        mLockPatternUtils = new LockPatternUtils(getActivity());
        mTodo = getActivity().getIntent().getIntExtra(PASSWORD_TODO, 0);
        mHandle = new Handler();
        switch (mTodo) {
            case PASSWORD_ON_LOCK:
                mPasswordTitle.setText(R.string.password_input);
                mPasswordWrongTip.setText(R.string.password_not_match);
                break;
            case PASSWORD_OFF_LOCK:
                mPasswordTitle.setText(R.string.password_close);
                mPasswordWrongTip.setText(R.string.password_wrong);
                break;
            case PASSWORD_MODIFY:
            case PASSWORD_VERIFY:
                mPasswordTitle.setText(R.string.password_verify);
                mPasswordWrongTip.setText(R.string.password_wrong);
                break;
        }
    }

    private void verifyPassword() {
        String password = "";
        for (int i = 0; i < 4; i++) {
            password += mInput[i];
        }
        switch (mTodo) {
            case PASSWORD_ON_LOCK:
                if ("".equals(mLastInput)) {
                    mLastInput = password;
                    clearPasswordView();
                    mPasswordTitle.setText(R.string.password_input_check_new);
                    return;
                }
                if (!password.equals(mLastInput)) {
                    mPasswordWrongTip.setVisibility(View.VISIBLE);
                    mLastInput = "";
                    clearPasswordView();
                    mPasswordTitle.setText(R.string.password_input);
                    return;
                }
                mLockPatternUtils.saveLockPassword(password, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC, false);
                PopupUtils.popupView(getActivity(), getTipView(R.string.password_set_success), true, true);
                break;
            case PASSWORD_OFF_LOCK:
                if (mLockPatternUtils.checkPassword(password)) {
                    mLockPatternUtils.clearLock(true);
                    PopupUtils.popupView(getActivity(), getTipView(R.string.password_close), true, true);
                } else {
                    clearPasswordView();
                    mPasswordWrongTip.setVisibility(View.VISIBLE);
                }
                break;
            case PASSWORD_MODIFY:
                if (mLockPatternUtils.checkPassword(password)) {
                    clearPasswordView();
                    mTodo = PASSWORD_ON_LOCK;
                    mPasswordTitle.setText(R.string.password_input);
                    mPasswordWrongTip.setText(R.string.password_not_match);
                } else {
                    clearPasswordView();
                    mPasswordWrongTip.setVisibility(View.VISIBLE);
                }
                break;
            case PASSWORD_VERIFY:
                if (mLockPatternUtils.checkPassword(password)) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    clearPasswordView();
                    mPasswordWrongTip.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void clearPasswordView() {
        for (int i = 0; i < 4; i++) {
            mInput[i] = "";
            mPasswordInput.setPasswordDot(-1);
        }
    }

    @Override
    public void onClick(View v) {
        String input = null;
        switch (v.getId()) {
            case R.id.password_button_0:
                input = "0";
                break;
            case R.id.password_button_1:
                input = "1";
                break;
            case R.id.password_button_2:
                input = "2";
                break;
            case R.id.password_button_3:
                input = "3";
                break;
            case R.id.password_button_4:
                input = "4";
                break;
            case R.id.password_button_5:
                input = "5";
                break;
            case R.id.password_button_6:
                input = "6";
                break;
            case R.id.password_button_7:
                input = "7";
                break;
            case R.id.password_button_8:
                input = "8";
                break;
            case R.id.password_button_9:
                input = "9";
                break;
            case R.id.password_button_cancel:
                getActivity().finish();
                return;
            case R.id.password_button_delete:
                for (int i = 3; i >= 0; i--) {
                    if (!"".equals(mInput[i])) {
                        mInput[i] = "";
                        mPasswordInput.setPasswordDot(i-1);
                        break;
                    }
                }
                return;
        }
        for (int i = 0; i < 4; i++) {
            if ("".equals(mInput[i])) {
                mInput[i] = input;
                mPasswordInput.setPasswordDot(i);
                break;
            }
        }
        mPasswordWrongTip.setVisibility(View.INVISIBLE);
        if (!"".equals(mInput[3])) {
            mHandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    verifyPassword();
                }
            }, 50);
        }
    }

    private View getTipView(int resource) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_translucent, null);
        TextView textView = (TextView) view.findViewById(R.id.popup_content);
        Shader shader_gradient = new LinearGradient(0, 10, 0, 80, Color.parseColor("#00f79d"),
                Color.parseColor("#cc0cb7e2"), Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader_gradient);
        textView.setText(getString(resource));
        return view;
    }
}
