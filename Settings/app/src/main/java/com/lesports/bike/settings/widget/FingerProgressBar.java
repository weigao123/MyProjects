package com.lesports.bike.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


import com.lesports.bike.settings.R;

import java.lang.ref.WeakReference;

/**
 * Created by zhouying on 16-6-12.
 */
public class FingerProgressBar extends View {
    private static final int PGOGRESS_LOC = 95;

    private Bitmap mFingerprintImage;
    private Bitmap mFingerprintMask;//
    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;
    private int mProgress = -1;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private long mTickDuration = 40;

    private int mAnimPercent;
    private Runnable mPreAnimRunnable;
    private Handler mPreAnimHandler;
    private int widgetWidth;
    private int widgetHeight;
    private int left;
    private int lineHeight = 3;

    public FingerProgressBar(Context context) {
        super(context);
    }

    public FingerProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FingerProgressBar(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mPaint.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "DINCond-Bold.otf"));
        mProgress = 0;
        this.mPreAnimRunnable = new PreAnimationRunnable(this);
        this.mPreAnimHandler = new Handler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Resources res = getResources();
        widgetWidth = this.getWidth();
        widgetHeight = this.getHeight();
        mFingerprintImage = decodeResource(res, R.drawable.fingerprint_progress_bar);
        mBitmapWidth = mFingerprintImage.getWidth();
        mBitmapHeight = mFingerprintImage.getHeight();
        mFingerprintMask = decodeResource(res, R.drawable.fingerprint_progress_mask);
        left = (widgetWidth - mBitmapWidth) / 2;
    }

    public void setProgress(int progress) {
        if (progress < 0)
            return;
        else if (progress > 100) {
            progress = 100;
        }
        if (progress != mProgress) {
            mProgress = progress;
            mPreAnimHandler.post(mPreAnimRunnable);
        }
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inMutable = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inTargetDensity = value.density;
        opts.inScaled = false;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    private class PreAnimationRunnable implements Runnable {
        private WeakReference<View> mViewReference;

        public PreAnimationRunnable(View view) {
            mViewReference = new WeakReference<View>(view);
        }

        @Override
        public void run() {
            FingerProgressBar view = (FingerProgressBar) mViewReference
                    .get();
            if (mAnimPercent < mProgress) {
                view.invalidate();
                mAnimPercent++;
                mPreAnimHandler.postDelayed(this, view.mTickDuration);
            } else if (mAnimPercent > mProgress) {
                view.invalidate();
                mAnimPercent--;
                mPreAnimHandler.postDelayed(this, view.mTickDuration);
            }

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        int curY = mBitmapHeight * mAnimPercent / 100;

        //指纹彩色
        canvas.save();
        canvas.clipRect(left, 0, left + mBitmapWidth, curY);
        canvas.drawBitmap(mFingerprintImage, left, 0, mPaint);
        canvas.restore();

        //渐变
        Paint p = new Paint();
        LinearGradient lg = new LinearGradient(0, curY, 0, mBitmapHeight,
                0x5c00f79d, 0x0000f79d, Shader.TileMode.CLAMP);
        p.setShader(lg);
        canvas.drawRect(widgetWidth / 2 - 120, curY, widgetWidth / 2 + 120, mBitmapHeight, p);
        //指纹灰色
        canvas.save();
        canvas.clipRect(left, curY, left + mBitmapWidth, mBitmapHeight);
        canvas.drawBitmap(mFingerprintMask, left, 0, mPaint);
        canvas.restore();
        //扫描线

        if (mAnimPercent < 100) {
            mPaint.setColor(getResources().getColor(R.color.green));
            mPaint.setStrokeWidth(lineHeight);
            canvas.drawLine(widgetWidth / 2 - 120, curY, widgetWidth / 2 + 120, curY, mPaint);
        }
        //文字
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(44);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(4);
        float textWidth = mPaint.measureText(mAnimPercent + "%");
        canvas.drawText(mAnimPercent + "%", left + mBitmapWidth / 2 - textWidth / 2, mBitmapHeight + PGOGRESS_LOC, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
        if (mFingerprintMask != null) {
            mFingerprintMask.recycle();
            mFingerprintMask = null;
        }
        if (mFingerprintImage != null) {
            mFingerprintImage.recycle();
            mFingerprintImage = null;
        }
    }
}
