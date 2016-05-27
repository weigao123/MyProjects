package com.lesports.bike.settings.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lesports.bike.settings.R;

/**
 * Created by gaowei3 on 2016/5/27.
 */
public class TranslucentActivity extends Activity {
    public static final String TEXT_TIP = "text_tip";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translucent);
        String text = getIntent().getStringExtra(TEXT_TIP);
        TextView textView = (TextView) findViewById(R.id.text_tip);
        textView.setText(text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
