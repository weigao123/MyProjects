package com.lesports.bike.settings.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import bike.os.core.BikeStatus;

import com.lesports.bike.settings.R;
import com.lesports.bike.settings.light.BikeLightView;
import com.lesports.bike.settings.light.BikeStatusChangeListener;
import com.lesports.bike.settings.light.LightManager;

/**
 * Created by gwball on 2016/5/25.
 */
public class LightFragment extends BaseFragment implements BikeStatusChangeListener{
    private View lightView;
    private Button headLightButton;
    private Button headOutLineLightButton;
    private Button tailLightButton;
    private ImageView headLight;
    private ImageView laserLightLeft;
    private ImageView laserLightRight;
    private ImageView tailLight;
    private LightManager lightManager;
    private int haedLightStatus;
    private int laserLightStatus;
    private int tailLightStatus;

    @Override
    protected void initViewAndData() {
        lightManager = LightManager.fromApplication(getActivity());
        lightManager.refreshStatus();
        lightManager.registerListener(this);
        headLightButton = (Button) lightView.findViewById(R.id.head_light_button_in_setting);
        headOutLineLightButton = (Button) lightView.findViewById(R.id.head_out_line_light_button_in_setting);
        tailLightButton = (Button) lightView.findViewById(R.id.tail_light_button_in_setting);
        headLight = (ImageView) lightView.findViewById(R.id.head_light_in_setting);
        laserLightLeft = (ImageView) lightView.findViewById(R.id.laser_light_left_in_setting);
        laserLightRight = (ImageView) lightView.findViewById(R.id.laser_light_right_in_setting);
        tailLight = (ImageView) lightView.findViewById(R.id.tail_light_in_setting);

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
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        lightView = inflater.inflate(R.layout.fragment_light, container, false);
        return lightView;
    }

    @Override
    protected String getTitleName() {
        return getResources().getString(R.string.light);
    }

    @Override
    public void onChanged(BikeStatus bikeStatus) {
        // TODO Auto-generated method stub
        if (bikeStatus.headLight != BikeLightView.HAED_LIGHT_OFF) {
            syncHeadLightStatus(true);
        } else {
            syncHeadLightStatus(false);
        }

        if (bikeStatus.laserLight != BikeLightView.LASER_LIGHT_OFF) {
            syncLaserLightStatus(true);
        } else {
            syncLaserLightStatus(false);
        }

        if (bikeStatus.tailLight != BikeLightView.LASER_LIGHT_OFF) {
            syncTailLightStatus(true);
        } else {
            syncTailLightStatus(false);
        }

    }

    public void changeHeadLightStatus() {
        if (haedLightStatus == BikeLightView.HAED_LIGHT_ON) {
            syncHeadLightStatus(false);
            lightManager.openHeadLight(false);
        } else {
            syncHeadLightStatus(true);
            lightManager.openHeadLight(true);
        }
    }

    public void changeLaserLightStatus() {
        if (laserLightStatus == BikeLightView.LASER_LIGHT_ON) {
            syncLaserLightStatus(false);
            lightManager.openLaserLight(false);
        } else {
            syncLaserLightStatus(true);
            lightManager.openLaserLight(true);
        }
    }

    public void changeTailLightStatus() {
        if (tailLightStatus == BikeLightView.LASER_LIGHT_ON) {
            syncTailLightStatus(false);
            lightManager.openTailLight(false);
        } else {
            syncTailLightStatus(true);
            lightManager.openTailLight(true);
        }
    }

    public void syncLightStatus() {
        if (haedLightStatus == BikeLightView.HAED_LIGHT_ON) {
            syncHeadLightStatus(true);
        } else {
            syncHeadLightStatus(false);
        }

        if (laserLightStatus == BikeLightView.LASER_LIGHT_ON) {
            syncLaserLightStatus(true);
        } else {
            syncLaserLightStatus(false);
        }

        if (tailLightStatus == BikeLightView.LASER_LIGHT_ON) {
            syncTailLightStatus(true);
        } else {
            syncTailLightStatus(false);
        }
    }

    private void syncHeadLightStatus(Boolean status) {
        if (status) {
            haedLightStatus = BikeLightView.HAED_LIGHT_ON;
            headLightButton.setBackgroundResource(R.drawable.head_light_button_open);
            headLight.setVisibility(android.view.View.VISIBLE);
        } else {
            haedLightStatus = BikeLightView.HAED_LIGHT_OFF;
            headLightButton.setBackgroundResource(R.drawable.head_light_button_close);
            headLight.setVisibility(android.view.View.GONE);
        }
    }

    private void syncLaserLightStatus(Boolean status) {
        if (status) {
            laserLightStatus = BikeLightView.LASER_LIGHT_ON;
            headOutLineLightButton
                    .setBackgroundResource(R.drawable.laser_light_button_open);
            laserLightLeft.setVisibility(android.view.View.VISIBLE);
            laserLightRight.setVisibility(android.view.View.VISIBLE);
        } else {
            laserLightStatus = BikeLightView.LASER_LIGHT_OFF;
            headOutLineLightButton
                    .setBackgroundResource(R.drawable.laser_light_button_close);
            laserLightLeft.setVisibility(android.view.View.GONE);
            laserLightRight.setVisibility(android.view.View.GONE);
        }
    }

    private void syncTailLightStatus(Boolean status) {
        if (status) {
            tailLightStatus = BikeLightView.TAIL_LIGHT_ON;
            tailLightButton.setBackgroundResource(R.drawable.tail_light_button_open);
            tailLight.setVisibility(android.view.View.VISIBLE);
        } else {
            tailLightStatus = BikeLightView.TAIL_LIGHT_OFF;
            tailLightButton.setBackgroundResource(R.drawable.tail_light_button_close);
            tailLight.setVisibility(android.view.View.GONE);
        }
    }
}
