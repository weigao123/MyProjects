package com.lesports.bike.settings.light;

import bike.os.core.BikeStatus;

public interface BikeStatusChangeListener {
    public void onChanged(BikeStatus bikeStatus);
}