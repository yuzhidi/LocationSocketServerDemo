package com.hicam.locationservice;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class LocationSocketServer extends Service {
    public static final String TAG = "LocationSocketServer";

    private static final int UPDATE_DELAY = 300;
    private static final int MSG_NOTICE = 10;

    private MyLocation mL;
    private NetworkServer mNetworkServer;

    private Handler mHandler = new MyHandler();
    private final IBinder mBinder = new LocalBinder();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_NOTICE:
                updateDate();
                sendEmptyMessageDelayed(MSG_NOTICE, UPDATE_DELAY);
                break;
            default:
                break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");

        create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");

        destory();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand, flags:"+flags+",startId:"+startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnBind");
        return super.onUnbind(intent);
    }

    private void create() {
        mL = new MyLocation(this);
        try {
            mNetworkServer = NetworkServer.getInstance();
        } catch (IOException e) {
            Log.e(TAG, "NetworkServer.getInstance() fail!!");
            e.printStackTrace();
            return;
        }
//        mNetworkServer.start();
    }

    private void destory() {
        if (mNetworkServer != null) {
            mNetworkServer.stop();
        }
    }

    private void updateDate() {
        Log.v(TAG, "updateDate()");
        mNetworkServer.setLocation(mL.getLocation());
    }

    public class LocalBinder extends Binder {
        LocationSocketServer getService() {
            return LocationSocketServer.this;
        }
    }

    public NetworkServer getNetworkServer() {
        return mNetworkServer;
    }
}
