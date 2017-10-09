package com.suma.coffeeshopmvpassignment.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.suma.coffeeshopmvpassignment.Application.CoffeeShopApp;

public class Util {

    //check network available or not
    public static boolean isNetworkAvailable()
    {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) CoffeeShopApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }



}