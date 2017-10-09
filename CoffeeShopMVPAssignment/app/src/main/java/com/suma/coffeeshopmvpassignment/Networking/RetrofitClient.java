package com.suma.coffeeshopmvpassignment.Networking;

import android.Manifest;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.suma.coffeeshopmvpassignment.Application.CoffeeShopApp;
import com.suma.coffeeshopmvpassignment.Constants.Constants;
import com.suma.coffeeshopmvpassignment.Util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//singleton retrofit client for handling api call
public class RetrofitClient {
    private static final String TAG = RetrofitClient.class.getSimpleName();

    private static volatile Retrofit sRetrofit = null;
    private static ResponseInterface retrofitService;

    public RetrofitClient() {
    }

    public static ResponseInterface getApiService() {
        return initRetrofitService();
    }

    private static ResponseInterface initRetrofitService() {
        if (retrofitService == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitService == null) {
                    retrofitService = getRetrofit().create(ResponseInterface.class);
                }
            }
        }
        return retrofitService;
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    private synchronized static Retrofit getRetrofit() {
        if (sRetrofit == null) {
            synchronized (RetrofitClient.class) {
                if (sRetrofit == null) {
                    sRetrofit = new Retrofit.Builder()
                            .baseUrl(Constants.baseUrl)
                            .client(createClient())
                            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                            .build();
                }
            }
        }
        return sRetrofit;
    }

    private static OkHttpClient createClient() {
        return new Builder()
                .cache(new Cache(new File(CoffeeShopApp.getAppContext().getCacheDir(), "http"), 1024 * 1024 * 10))
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(createInterceptor()).build();
    }

    private static Interceptor createInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (request.method().equals("POST")) {
                    if (Util.isNetworkAvailable()) {
                        Log.d(TAG, "intercept:  Intercept  connected " + request.url());
                        request.newBuilder().header("Cache-Control", "only-if-cached").build();
                    } else {
                        Log.d(TAG, "intercept:  Intercept not connected " + request.url());
                        request.newBuilder().header("Cache-Control", String.format(Locale.getDefault(), "public,   max-stale=%d", 86400));
                    }
                }
                Response response = chain.proceed(request);
                //rewrite response CC Header to  force use of cache
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age=86400")
                        .header("Pragma", "public")
                        .build();
            }
        };
    }
}