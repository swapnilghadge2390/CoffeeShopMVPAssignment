package com.suma.coffeeshopmvpassignment.Manager;

import android.location.Location;

public interface LocationCallback {

    void onLocationApiManagerConnected();
    void onLocationChanged(Location location);
}