package com.suma.coffeeshopmvpassignment.Activity;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suma.coffeeshopmvpassignment.Models.NearByApiResponse;
import com.suma.coffeeshopmvpassignment.Presenter.MapsPresenter;
import com.suma.coffeeshopmvpassignment.Presenter.MapsPresenterImpl;
import com.suma.coffeeshopmvpassignment.R;
import com.suma.coffeeshopmvpassignment.Views.MapsView;

import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MapsView,GoogleMap.InfoWindowAdapter{
    private GoogleMap mMap;
    private int mapZoomLevel = 12;
    private MapsPresenter presenter;
    private Response<NearByApiResponse> responseData;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //create object of MapsPresenterIMpl
        presenter = new MapsPresenterImpl(this, this, this);
    }


    /**
     * override on MapReady
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //set info Adapter for showing cafe info dialog once cafe marker clicked
        mMap.setInfoWindowAdapter(this);
        //pass call to omMapReady() of presenter
        presenter.onMapReady();
    }


    /**
     * generate Map
     */
    @Override
    public void generateMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * update location on map once location changed
     * @param location
     */
    @Override
    public void updateLocationOnMap(Location location) {
        MarkerOptions markerOptions =  presenter.addCurrentLocationOnMap(location);
        mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mapZoomLevel));
        presenter.getCoffeeShopList(getString(R.string.type_cafe),location);

    }

    /**
     * get result back from findplaces api and update UI
     * @param response
     * @param location
     */

    @Override
    public void getCoffeeShopListSuccess(Response<NearByApiResponse> response, Location location) {
        try {
            mMap.clear();
            responseData = response;
            MarkerOptions markerOption=  presenter.addCurrentLocationOnMap(location);
            mMap.addMarker(markerOption);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerOption.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(mapZoomLevel));
            for (int i = 0; i < response.body().getResults().size(); i++) {
                Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                String placeName = response.body().getResults().get(i).getName();
                String vicinity = response.body().getResults().get(i).getVicinity();
                String id = response.body().getResults().get(i).getId();
                LatLng latLng = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.coffeemug))
                        .title(placeName + " : " + vicinity)
                        .anchor(0.5f, 1))
                        .setTag(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * handle coffeeShop api failure response
     * @param appErrorMessage
     */
    @Override
    public void onFailure(String appErrorMessage) {
        Toast.makeText(this, appErrorMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @return
     */
    @Override
    public Activity getViewActivity() {
        return MapsActivity.this;
    }

    /**
     *
     */
    @Override
    public void onPermissionsGranted() {

    }

    /**
     *
     */
    @Override
    public void onPermissionsDenied() {

    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //disconnect google api client once activity destroyed
        presenter.disconnectFromLocationService();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    /**
     *
     * @param marker
     * @return
     */
    @Override
    public View getInfoContents(Marker marker) {
        //show coffeeShop info dialog
        return presenter.createCoffeeShopInfoDialog(marker,responseData);
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //handle location permissions
        presenter.onPermissionsResult(requestCode,permissions,grantResults);
    }



}
