package com.lesports.bike.settings.light;

import android.content.Context;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import bike.os.core.BikeStatus;
import bike.os.core.BikeStatusListener;
import bike.os.core.BikeStatusManager;
import bike.os.light.BikeLightManager;

public class LightManager extends BikeStatusListener {

    private BikeStatusManager bikeStatusManager;
    private BikeLightManager bikeLightManager;
    private BikeStatus bikeStatus;
    private static final int LIGHT_EXECUTE_INTERVAL = 150;
    private Context mContext;
    //private BikeStatusChangeListener mListener;
    private Button headLightButton;
    private Button headOutLineLightButton;
    private Button tailLightButton;
    private static LightManager lightManagerSingleton;
    private List<BikeStatusChangeListener > listeners = new ArrayList<BikeStatusChangeListener>();


    private LightManager(Context context) {
        mContext = context;
        bikeStatusManager = (BikeStatusManager) mContext
                .getSystemService(Context.BIKE_STATUS_SERVICE);
        if (context != null) {
            bikeLightManager = (BikeLightManager) mContext
                    .getSystemService(Context.BIKELIGHT_SERVICE);
        }
        if (bikeStatusManager != null)
            bikeStatusManager.startListening(LightManager.this);

    }

    public static synchronized final LightManager fromApplication(Context context){
        if (lightManagerSingleton == null){
            lightManagerSingleton = new LightManager(context);
        }
        return lightManagerSingleton;
    }

    @Override
    public void mcuStatus(BikeStatus stat) {
        // TODO Auto-generated method stub
        super.mcuStatus(stat);
        //mListener.onChanged(stat);
        for (BikeStatusChangeListener bikeStatusManagerListener : listeners) {
            bikeStatusManagerListener.onChanged(stat);
        }
        this.bikeStatus = stat;
    }

    public BikeStatus getBikeStatus() {
        refreshStatus();
        return bikeStatus;
    }

    public void refreshStatus() {
        bikeStatusManager.mcuStatus();
    }

    public boolean openLight(int index, boolean open) {
        int result = 0;
        if (open) {
            result = bikeLightManager.open(index);
        } else {
            result = bikeLightManager.close(index);
        }
        return true;
    }

    public boolean openLaserLight(boolean isOpen) {
        boolean openLight = openLight(BikeLightManager.LASER_LAMP,
                isOpen);
        return openLight;
    }

    public boolean openTailLight(boolean isOpen) {

        boolean openLight = openLight(BikeLightManager.TAIL, isOpen);
        return openLight;
    }

    public boolean openHeadLight(boolean isOpen) {
        boolean openLight = openLight(BikeLightManager.HEAD, isOpen);
        sleepInterval();
        openLight(BikeLightManager.SIDE_DECORATE, isOpen);
        return openLight;
    }

    private void sleepInterval() {
        try {
            Thread.sleep(LIGHT_EXECUTE_INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

//    public void setListener(BikeStatusChangeListener listener) {
//        mListener = listener;
//    }
    public void registerListener(BikeStatusChangeListener listener){
        listeners.add(listener);
    }

    public void unregisterListener(BikeStatusChangeListener listener){
        listeners.remove(listener);
    }

}
