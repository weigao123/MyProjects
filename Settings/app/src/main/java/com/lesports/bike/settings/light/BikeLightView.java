package com.lesports.bike.settings.light;

import android.app.StatusBarManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.application.SettingApplication;

import bike.os.core.BikeStatus;

public class BikeLightView extends FrameLayout implements BikeStatusChangeListener {

    private int mScreenHeight;
    private int mStatusBarHeightt;
    private int yPosition;
    private Boolean viewOpenning = false;
    protected static Boolean viewOpenned = false;
    private Boolean viewClosing = false;
    private Context mContext;
    private Button headLightButton;
    private Button headOutLineLightButton;
    private Button tailLightButton;
    private ImageView headLight;
    private ImageView laserLightLeft;
    private ImageView laserLightRight;
    private ImageView tailLight;
    private int haedLightStatus;
    private int laserLightStatus;
    private int tailLightStatus;
    private int mStartYPosition;

    private LightManager lightManager;
    private BikeStatus mBikeStatus;
    private StatusBarManager mStatusBarManager;
    public static final String STATUS_BAR_SERVICE = "statusbar";
    public static final int HAED_LIGHT_ON = 1;
    public static final int HAED_LIGHT_OFF = 0;
    public static final int LASER_LIGHT_ON = 1;
    public static final int LASER_LIGHT_OFF = 0;
    public static final int TAIL_LIGHT_ON = 1;
    public static final int TAIL_LIGHT_OFF = 0;

    private WindowManager wm = (WindowManager) getContext().getApplicationContext()
            .getSystemService(Context.WINDOW_SERVICE);
    private WindowManager.LayoutParams wmParams = ((SettingApplication) getContext()
            .getApplicationContext()).getMywmParams();

    public BikeLightView(Context context, int screenHeight, int statusBarHeightt) {
        super(context);
        mContext = context;
        mScreenHeight = screenHeight;
        mStatusBarHeightt = statusBarHeightt;
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.bike_light_view, this, true);
        initDateAndView();

    }

    public void initDateAndView() {

        lightManager = LightManager.fromApplication(mContext);
        lightManager.registerListener(this);
        headLightButton = (Button) findViewById(R.id.head_light_button);
        headOutLineLightButton = (Button) findViewById(R.id.head_out_line_light_button);
        tailLightButton = (Button) findViewById(R.id.tail_light_button);
        headLight = (ImageView) findViewById(R.id.head_light);
        laserLightLeft = (ImageView) findViewById(R.id.laser_light_left);
        laserLightRight = (ImageView) findViewById(R.id.laser_light_right);
        tailLight = (ImageView) findViewById(R.id.tail_light);

        headLightButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                changeHeadLightStatus();
            }
        });
        headOutLineLightButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                changeLaserLightStatus();
            }
        });
        tailLightButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                changeTailLightStatus();
            }
        });
        syncLightStatus();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lightManager.refreshStatus();
                mStartYPosition = (int) event.getY();

                yPosition = (int) event.getRawY();
                if ((yPosition > mScreenHeight - 25 && yPosition < mScreenHeight) && !viewOpenned) {
                    viewOpenning = true;
                }
                if ((yPosition > mStatusBarHeightt && yPosition < mStatusBarHeightt + 100)
                        && viewOpenned) {
                    viewClosing = true;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                yPosition = (int) event.getRawY();
                if (viewOpenning || viewClosing) {
                    updateViewPosition();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (viewOpenning) {
                    if (yPosition < mScreenHeight * 2 / 3) {
                        wmParams.y = 0;

                        wm.updateViewLayout(this, wmParams);
                        disableTitleBar();
                        viewOpenned = true;
                    } else {
                        wmParams.y = mScreenHeight - mStatusBarHeightt - 3;
                        wm.updateViewLayout(this, wmParams);
                        enableTitleBar();
                        viewOpenned = false;
                    }
                }
                if (viewClosing) {
                    if (yPosition < mScreenHeight / 3) {
                        wmParams.y = 0;

                        wm.updateViewLayout(this, wmParams);
                        disableTitleBar();
                        viewOpenned = true;
                    } else {
                        wmParams.y = mScreenHeight - mStatusBarHeightt - 3;
                        wm.updateViewLayout(this, wmParams);
                        enableTitleBar();
                        viewOpenned = false;
                    }
                }
                viewOpenning = false;
                viewClosing = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateViewPosition() {
        wmParams.y = yPosition - mStatusBarHeightt - mStartYPosition;
        if (wmParams.y > 0 || wmParams.y == 0) {
            // 更新浮动窗口位置参数
            wm.updateViewLayout(this, wmParams);
        } else {
            wmParams.y = 0;
            wm.updateViewLayout(this, wmParams);
        }
    }

    @Override
    public void onChanged(BikeStatus bikeStatus) {
        // TODO Auto-generated method stub
        if (bikeStatus.headLight != HAED_LIGHT_OFF) {
            syncHeadLightStatus(true);
        } else {
            syncHeadLightStatus(false);
        }

        if (bikeStatus.laserLight != LASER_LIGHT_OFF) {
            syncLaserLightStatus(true);
        } else {
            syncLaserLightStatus(false);
        }

        if (bikeStatus.tailLight != LASER_LIGHT_OFF) {
            syncTailLightStatus(true);
        } else {
            syncTailLightStatus(false);
        }
    }

    public void changeHeadLightStatus() {
        if (haedLightStatus == HAED_LIGHT_ON) {
            syncHeadLightStatus(false);
            lightManager.openHeadLight(false);
        } else {
            syncHeadLightStatus(true);
            lightManager.openHeadLight(true);
        }
    }

    public void changeLaserLightStatus() {
        if (laserLightStatus == LASER_LIGHT_ON) {
            syncLaserLightStatus(false);
            lightManager.openLaserLight(false);
        } else {
            syncLaserLightStatus(true);
            lightManager.openLaserLight(true);
        }
    }

    public void changeTailLightStatus() {
        if (tailLightStatus == LASER_LIGHT_ON) {
            syncTailLightStatus(false);
            lightManager.openTailLight(false);
        } else {
            syncTailLightStatus(true);
            lightManager.openTailLight(true);
        }
    }

    public void syncLightStatus() {
        if (haedLightStatus == HAED_LIGHT_ON) {
            syncHeadLightStatus(true);
        } else {
            syncHeadLightStatus(false);
        }

        if (laserLightStatus == LASER_LIGHT_ON) {
            syncLaserLightStatus(true);
        } else {
            syncLaserLightStatus(false);
        }

        if (tailLightStatus == LASER_LIGHT_ON) {
            syncTailLightStatus(true);
        } else {
            syncTailLightStatus(false);
        }
    }

    private void syncHeadLightStatus(Boolean status) {
        if (status) {
            haedLightStatus = HAED_LIGHT_ON;
            headLightButton.setBackgroundResource(R.drawable.head_light_button_open);
            headLight.setVisibility(VISIBLE);
        } else {
            haedLightStatus = HAED_LIGHT_OFF;
            headLightButton.setBackgroundResource(R.drawable.head_light_button_close);
            headLight.setVisibility(GONE);
        }
    }

    private void syncLaserLightStatus(Boolean status) {
        if (status) {
            laserLightStatus = LASER_LIGHT_ON;
            headOutLineLightButton
                    .setBackgroundResource(R.drawable.laser_light_button_open);
            laserLightLeft.setVisibility(VISIBLE);
            laserLightRight.setVisibility(VISIBLE);
        } else {
            laserLightStatus = LASER_LIGHT_OFF;
            headOutLineLightButton
                    .setBackgroundResource(R.drawable.laser_light_button_close);
            laserLightLeft.setVisibility(GONE);
            laserLightRight.setVisibility(GONE);
        }
    }

    private void syncTailLightStatus(Boolean status) {
        if (status) {
            tailLightStatus = TAIL_LIGHT_ON;
            tailLightButton.setBackgroundResource(R.drawable.tail_light_button_open);
            tailLight.setVisibility(VISIBLE);
        } else {
            tailLightStatus = TAIL_LIGHT_OFF;
            tailLightButton.setBackgroundResource(R.drawable.tail_light_button_close);
            tailLight.setVisibility(GONE);
        }
    }

    public void enableTitleBar() {
        if (mStatusBarManager == null) {
            mStatusBarManager = (StatusBarManager)
                    mContext.getSystemService(STATUS_BAR_SERVICE);
        }
        mStatusBarManager.disable(StatusBarManager.DISABLE_NONE);
    }

    public void disableTitleBar() {
        if (mStatusBarManager == null) {
            mStatusBarManager = (StatusBarManager)
                    mContext.getSystemService(STATUS_BAR_SERVICE);
        }
        mStatusBarManager.disable(StatusBarManager.DISABLE_EXPAND);
    }

}
