package com.lesports.bike.settings.utils;

import android.os.CountDownTimer;

/**
 * Created by gaowei3 on 2016/6/1.
 */
public class TimeUtils {

    public static void timeLoop(final TimeProcess timeProcess, int interval) {
        CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeProcess.onTick();
            }
            @Override
            public void onFinish() {
                timeProcess.onFinish();
            }
        };
        timer.start();
    }

    public interface TimeProcess {
        void onTick();
        void onFinish();
    }

}
