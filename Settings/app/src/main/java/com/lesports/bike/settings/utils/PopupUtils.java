package com.lesports.bike.settings.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;


/**
 * Created by gaowei3 on 2016/5/28.
 */
public class PopupUtils {
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static final int TIME_TO_FINISH_ACTIVITY = 1500;

    public static void popupToastView(final Context context, int resource) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        final TextView textView = new TextView(context);
        final FrameLayout frameLayout = new FrameLayout(context);
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        textView.setText(context.getResources().getString(resource));
        textView.setTextSize(20);
        textView.setPadding(40, 20, 40, 20);
        textView.setIncludeFontPadding(false);
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Shader border_gradient = new LinearGradient(0, 0, textView.getWidth(), textView.getHeight(), Color.parseColor("#00f79d"),
                        Color.parseColor("#cc0cb7e2"), Shader.TileMode.CLAMP);
                Shader text_gradient = new LinearGradient(0, 0, 0, textView.getTextSize(), Color.parseColor("#00f79d"),
                        Color.parseColor("#cc0cb7e2"), Shader.TileMode.CLAMP);
                Shader bg_gradient = new LinearGradient(0, 0, 0, textView.getHeight(), new int[]{Color.parseColor("#007249"),
                        Color.parseColor("#002417"), Color.parseColor("#060707")}, new float[]{0, 0.5f, 1}, Shader.TileMode.CLAMP);
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                textView.buildDrawingCache();
                Bitmap bitmap = textView.getDrawingCache();
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setShader(bg_gradient);
                canvas.drawRect(0, 0, textView.getWidth(), textView.getHeight(), paint);
                paint.reset();
                paint.setStyle(Paint.Style.STROKE);
                paint.setShader(border_gradient);
                paint.setStrokeWidth(8);
                canvas.drawRect(0, 0, textView.getWidth(), textView.getHeight(), paint);
                textView.getPaint().setShader(text_gradient);
                textView.setBackground(new BitmapDrawable(context.getResources(), bitmap));

                Bitmap screen = takeBlurScreen(context);
                frameLayout.setBackground(new BitmapDrawable(context.getResources(), screen));
            }
        });

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER;
        frameLayout.addView(textView, textParams);
        try {
            windowManager.addView(frameLayout, params);
        } catch (RuntimeException e) {
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    windowManager.removeView(frameLayout);
                } catch (RuntimeException e) {
                }
            }
        }, TIME_TO_FINISH_ACTIVITY);
    }

    public static void popupView(Context context, View view) {
        popupView(context, view, false, false);
    }

    public static void popupView(final Context context, final View view, final boolean isAutoClose, final boolean isCloseActivity) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        Bitmap screen = takeBlurScreen(context);
        view.setBackground(new BitmapDrawable(context.getResources(), screen));
        try {
            windowManager.addView(view, params);
        } catch (RuntimeException e) {
        }
        if (isAutoClose) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeView(context, view);
                    if (isCloseActivity && context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            }, TIME_TO_FINISH_ACTIVITY);
        }
    }

    public static void popupTranslucentView(Context context, View view) {
        popupTranslucentView(context, view, false, false);
    }

    public static void popupTranslucentView(final Context context, final View view, final boolean isAutoClose, final boolean isCloseActivity) {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.format = PixelFormat.TRANSLUCENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        try {
            windowManager.addView(view, params);
        } catch (RuntimeException e) {
        }
        if (isAutoClose) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeView(context, view);
                    if (isCloseActivity && context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            }, TIME_TO_FINISH_ACTIVITY);
        }
    }

    public static void removeView(Context context, View view) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        try {
            windowManager.removeView(view);
        } catch (RuntimeException e) {
        }
    }

    private static Bitmap takeBlurScreen(Context context) {
        Point point = SizeUtils.getScreenSize(context);
        Bitmap screen = SurfaceControl.screenshot(point.x, point.y);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        float brightness = 0.8f;
        ColorMatrix cm = new ColorMatrix();
        cm.set(new float[]
                { brightness, 0, 0, 0, 0,
                        0, brightness, 0, 0, 0,
                        0, 0, brightness, 0, 0,
                        0, 0, 0,          1, 0 });
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        int scaleFactor = 8;
        Bitmap resizeBmp = Bitmap.createBitmap(screen.getWidth()/scaleFactor, screen.getHeight()/scaleFactor, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resizeBmp);
        canvas.scale(1f/scaleFactor, 1f/scaleFactor);
        canvas.drawBitmap(screen, 0, 0, paint);
        return FastBlur.doBlur(resizeBmp, 4, true);
    }
}

