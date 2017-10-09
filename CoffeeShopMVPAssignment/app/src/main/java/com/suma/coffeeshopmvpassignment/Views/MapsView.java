package com.suma.coffeeshopmvpassignment.Views;

import android.app.Activity;
import android.location.Location;

import com.suma.coffeeshopmvpassignment.Models.NearByApiResponse;

import retrofit2.Response;

public interface MapsView {

    void generateMap();
    void updateLocationOnMap(Location location);
    void getCoffeeShopListSuccess(Response<NearByApiResponse> response, Location location);
    void onFailure(String appErrorMessage);
    public Activity getViewActivity();
    public void onPermissionsGranted();
    public void onPermissionsDenied();
}