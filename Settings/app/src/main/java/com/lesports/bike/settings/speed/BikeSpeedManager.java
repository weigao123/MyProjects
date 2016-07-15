
package com.lesports.bike.settings.speed;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import bike.os.sensor.SpeedListener;
import bike.os.sensor.SpeedManager;

public class BikeSpeedManager extends SpeedListener {
    private Context context;
    private boolean isRunning;
    private SpeedManager manager;
    private static BikeSpeedManager bikeSpeedManager;
    private static final String TAG = "BikeSpeedManager";

    private List<SpeedChangeListener> speedListeners = new ArrayList<SpeedChangeListener>();

    private BikeSpeedManager(Context context) {
        this.context = context;
        if (context != null) {
            try {
                manager = (SpeedManager) context
                        .getSystemService(Context.SPEED_SERVICE);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static final synchronized BikeSpeedManager fromApplication(Context context) {
        if (bikeSpeedManager == null) {
            bikeSpeedManager = new BikeSpeedManager(context);
        }
        return bikeSpeedManager;
    }

    public void registerSpeedListener(SpeedChangeListener listener) {
        speedListeners.add(listener);
        checkStart();
    }

    public void unregisterSpeedListener(SpeedChangeListener listener) {
        speedListeners.remove(listener);
        checkStop();
    }

    private void start() {
        if (manager != null) {
            manager.startListening(this);
            isRunning = true;
            Log.i(TAG, "start");
        }
    }

    private void stop() {
        if (manager != null) {
            manager.stopListening();
            isRunning = false;
            Log.i(TAG, "stop");
        }
    }

    private void checkStop() {
        if (speedListeners.isEmpty()) {
            if (isRunning) {
                stop();
            }
        }
    }

    private void checkStart() {
        if (!isRunning) {
            start();
        }
    }

    @Override
    public void onMilliSecondPerCircle(int mspc) {
        // TODO Auto-generated method stub
        super.onMilliSecondPerCircle(mspc);
        for (SpeedChangeListener speedChangeLinstener : speedListeners) {
            speedChangeLinstener.onChanged(mspc);
        }
    }

    public int getCurSpeed() {
        return manager.getCurMspc();
    }

}
