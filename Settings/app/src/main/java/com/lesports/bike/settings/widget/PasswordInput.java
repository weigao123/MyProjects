package com.lesports.bike.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lesports.bike.settings.R;

/**
 * Created by gaowei3 on 2016/5/26.
 */
public class PasswordInput extends LinearLayout {

    private ImageView[] mInputView = new ImageView[4];
    private View[] mUnderlineView = new View[4];

    public PasswordInput(Context context) {
        super(context);
        parseStyle(context, null);
    }

    public PasswordInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs);
    }

    public PasswordInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseStyle(context, attrs);
    }

    private void parseStyle(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.widget_password_input, this);
        mInputView[0] = (ImageView)findViewById(R.id.password_input_1);
        mInputView[1] = (ImageView)findViewById(R.id.password_input_2);
        mInputView[2] = (ImageView)findViewById(R.id.password_input_3);
        mInputView[3] = (ImageView)findViewById(R.id.password_input_4);
        mUnderlineView[0] = findViewById(R.id.password_underline_1);
        mUnderlineView[1] = findViewById(R.id.password_underline_2);
        mUnderlineView[2] = findViewById(R.id.password_underline_3);
        mUnderlineView[3] = findViewById(R.id.password_underline_4);
    }

    public void setPasswordDot(int which) {
        for (int i = 0 ; i < 4 ; i++) {
            if (i <= which) {
                mInputView[i].setImageResource(R.drawable.password_dot);
                mUnderlineView[i].setVisibility(INVISIBLE);
            } else {
                mInputView[i].setImageResource(R.color.colorBlack);
                mUnderlineView[i].setVisibility(VISIBLE);
            }
        }
    }
}
