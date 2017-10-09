package com.suma.coffeeshopmvpassignment.Presenter;

import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suma.coffeeshopmvpassignment.Models.NearByApiResponse;

import retrofit2.Response;

/**
 * map presenter interface
 */
public interface MapsPresenter {

    void connectToLocationService();
    void disconnectFromLocationService();
    void getCoffeeShopList(String placeType, Location location);
    MarkerOptions addCurrentLocationOnMap(Location location);
    void onMapReady();;
     void requestPermissions();
     void onPermissionsResult(int requestCode,
                                    String permissions[], int[] grantResults);

     View createCoffeeShopInfoDialog(Marker marker, Response<NearByApiResponse> nearByApiResponseResponse);

}