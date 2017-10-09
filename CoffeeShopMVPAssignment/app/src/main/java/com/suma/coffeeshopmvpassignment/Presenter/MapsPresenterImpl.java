package com.suma.coffeeshopmvpassignment.Presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suma.coffeeshopmvpassignment.Manager.GoogleLocationApiManager;
import com.suma.coffeeshopmvpassignment.Manager.LocationCallback;
import com.suma.coffeeshopmvpassignment.Models.NearByApiResponse;
import com.suma.coffeeshopmvpassignment.Networking.RetrofitClient;
import com.suma.coffeeshopmvpassignment.R;
import com.suma.coffeeshopmvpassignment.Views.MapsView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsPresenterImpl implements MapsPresenter, LocationCallback{
    private int PROXIMITY_RADIUS = 10000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = MapsPresenterImpl.class.getSimpleName();
    private MapsView view;
    private GoogleLocationApiManager googleLocationApiManager;
    private Context context;


    public MapsPresenterImpl(MapsView view, FragmentActivity fragmentActivity, Context context) {
        this.context = context;
        if(view == null) throw new NullPointerException(context.getString(R.string.view_error));
        if(fragmentActivity == null) throw new NullPointerException(context.getString(R.string.fragment_error));
        this.view = view;
        //request permissions if build os is M or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
        this.googleLocationApiManager = new GoogleLocationApiManager(fragmentActivity, context);
        this.googleLocationApiManager.setLocationCallback(this);
        this.view.generateMap();
    }


    @Override
    public void onLocationApiManagerConnected() {

    }


    @Override
    public void onLocationChanged(Location location) {
        view.updateLocationOnMap(location);

    }


    @Override
    public void connectToLocationService() {
        googleLocationApiManager.connect();
    }


    @Override
    public void disconnectFromLocationService() {
        if(googleLocationApiManager.isConnectionEstablished()) {
            googleLocationApiManager.disconnect();
        }
    }



    @Override
    public void getCoffeeShopList(String placeType, final Location location) {
        Call<NearByApiResponse> call = RetrofitClient.getApiService().getNearbyPlaces(placeType, location.getLatitude() + "," + location.getLongitude(), PROXIMITY_RADIUS);
        call.enqueue(new Callback<NearByApiResponse>() {
            @Override
            public void onResponse(Call<NearByApiResponse> call, Response<NearByApiResponse> response) {
                     view.getCoffeeShopListSuccess(response,location);
            }

            @Override
            public void onFailure(Call<NearByApiResponse> call, Throwable t) {
                view.onFailure(t.getMessage());
            }
        });
    }

    // add current location on map
    @Override
    public MarkerOptions addCurrentLocationOnMap(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(context.getString(R.string.current_position));

            // Adding colour to the marker
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            return markerOptions;

    }


    @Override
    public void onMapReady() {
        connectToLocationService();
    }



     // Request locations permissionss
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(view.getViewActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(view.getViewActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(view.getViewActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(view.getViewActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
        else
        {
            view.onPermissionsGranted();
        }
    }


    //handle location permissions result
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted. Do the
                        // contacts-related task you need to do.
                        if (view.getViewActivity().checkSelfPermission(
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            view.onPermissionsGranted();
                        }

                    } else {
                        Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                        view.onPermissionsDenied();
                    }
                    return;
                }

            }
        }
    }

    /// create CoffeeShop info dialog
    @Override
    public View createCoffeeShopInfoDialog(Marker marker, Response<NearByApiResponse> responseData) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myContentsView = inflater.inflate(R.layout.coffee_shop_info_dialog, null);
        try {
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            ImageView cafeImageView = ((ImageView) myContentsView.findViewById(R.id.cafeImage));
            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
            RatingBar ratingbar = (RatingBar) myContentsView.findViewById(R.id.ratingBar);
            TextView isOpenTextView = ((TextView) myContentsView.findViewById(R.id.isOpenTextView));
            double ratings = 0;
            boolean isOpen = false;
            try {
                if (responseData != null) {
                    for (int i = 0; i < responseData.body().getResults().size(); i++) {
                        if (responseData.body().getResults().get(i).getId().equalsIgnoreCase(String.valueOf(marker.getTag()))) {
                            isOpen = responseData.body().getResults().get(i).getOpeningHours().getOpenNow();
                            ratings = responseData.body().getResults().get(i).getRating();
                            break;
                        }
                    }
                    //if current position is user location hide rating bar and open textview
                    if (marker.getTitle().equalsIgnoreCase(context.getString(R.string.current_position))) {
                        isOpenTextView.setVisibility(View.GONE);
                        ratingbar.setVisibility(View.GONE);
                        cafeImageView.setVisibility(View.GONE);
                    } else {
                        isOpenTextView.setVisibility(View.VISIBLE);
                        ratingbar.setVisibility(View.VISIBLE);
                        cafeImageView.setVisibility(View.VISIBLE);
                        if (isOpen) {
                            isOpenTextView.setText(context.getString(R.string.open_text)+" "+ context.getString(R.string.open_status_yes));
                        } else {
                            isOpenTextView.setText(context.getString(R.string.open_text)+" "+context.getString(R.string.open_status_no));
                        }
                        float f = (float) ratings;
                        ratingbar.setRating(f);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            tvTitle.setText(marker.getTitle());
            tvSnippet.setText(marker.getSnippet());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
         return myContentsView;
    }



}

