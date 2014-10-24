package com.hicam.locationservice;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocationSocketClient extends Service {
    public static final String TAG = "LocationSocketClient";
    
    private NetworkClient mNetworkClient;
    private IBinder mLocalBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate()");
        create();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
        destroy();
    }
    
    private void create() {
        try {
            mNetworkClient = NetworkClient.getInstance();
        } catch (IOException e) {
            Log.e(TAG, "NetworkClient.getInstance() fail!!");
            e.printStackTrace();
            return;
        }
//        mNetworkClient.start();
    }
    
    private void destroy() {
        if(mNetworkClient != null) {
            mNetworkClient.stop();
        }
    }

    public class LocalBinder extends Binder {
        LocationSocketClient getService() {
            return LocationSocketClient.this;
        }
    }

    public NetworkClient getNetworkClient() {
        return mNetworkClient;
    }
}
