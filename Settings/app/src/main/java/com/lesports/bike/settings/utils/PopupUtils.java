package com.lesports.bike.settings.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lesports.bike.settings.R;

/**
 * Created by gaowei3 on 2016/5/28.
 */
public class PopupUtils {
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static int TIME_TO_FINISH_ACTIVITY = 1500;

    public static void popupOverlayView(final Context context, int resource, final boolean isCloseActivity) {
        final View view = LayoutInflater.from(context).inflate(R.layout.view_translucent, null);
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowManager.addView(view, params);

        TextView textView = (TextView) view.findViewById(R.id.popup_content);
        Shader shader_gradient = new LinearGradient(0, 10, 0, 80, Color.parseColor("#00f79d"),
                Color.parseColor("#cc0cb7e2"), Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader_gradient);
        textView.setText(context.getResources().getString(resource));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                windowManager.removeView(view);
                if (isCloseActivity && context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        }, TIME_TO_FINISH_ACTIVITY);
    }
}
