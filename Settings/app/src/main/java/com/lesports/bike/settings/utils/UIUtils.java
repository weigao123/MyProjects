package com.lesports.bike.settings.utils;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.widget.TextView;

/**
 * Created by gaowei3 on 2016/6/6.
 */
public class UIUtils {
    public static void makeColor(TextView textView, int top, int bottom, int colorTop, int colorBottom) {
        Shader shader_gradient = new LinearGradient(0, top, 0, bottom, colorTop, colorBottom, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(shader_gradient);
    }
}
