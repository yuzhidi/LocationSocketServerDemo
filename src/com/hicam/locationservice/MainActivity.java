package com.hicam.locationservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
    public static final String TAG = "LeoMainActivity";
    private boolean mIsServerBind;
    private boolean mIsClientBind;
    private LocationSocketServer mServerBinder;
    private LocationSocketClient mClientBinder;
    private NetworkState mNetworkState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start_client).setOnClickListener(this);
        findViewById(R.id.stop_client).setOnClickListener(this);
        findViewById(R.id.start_service).setOnClickListener(this);
        findViewById(R.id.stop_service).setOnClickListener(this);

        findViewById(R.id.start_client_socket).setOnClickListener(this);
        findViewById(R.id.stop_client_socket).setOnClickListener(this);
        findViewById(R.id.start_server_socket).setOnClickListener(this);
        findViewById(R.id.stop_server_socket).setOnClickListener(this);
        mNetworkState = new NetworkState(this);
        mNetworkState.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mNetworkState != null) {
            mNetworkState.stop();
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.start_service:
            Log.v(TAG, "onClick, start service");
            // startService(new Intent(this, LocationSocketServer.class));
            if (!mIsServerBind) {
                bindService(new Intent(this, LocationSocketServer.class),
                        mServerConnection, BIND_AUTO_CREATE);
            }
            break;
        case R.id.stop_service:
            Log.v(TAG, "onClick, stop service");
            if (mIsServerBind) {
                unbindService(mServerConnection);
            }
            // stopService(new Intent(this, LocationSocketServer.class));
            break;
        case R.id.start_client:
            Log.v(TAG, "onClick, start client");
            // startService(new Intent(this, LocationSocketClient.class));
            if (!mIsClientBind) {
                bindService(new Intent(this, LocationSocketClient.class),
                        mClientConnection, BIND_AUTO_CREATE);
            }
            break;
        case R.id.stop_client:
            Log.v(TAG, "onClick, stop client");
            // stopService(new Intent(this, LocationSocketClient.class));
            if (mIsClientBind) {
                unbindService(mClientConnection);
            }
            break;
        case R.id.start_server_socket:
            if (mIsServerBind) {
                if (mServerBinder.getNetworkServer() == null) {
                    Log.v(TAG, "network server is null!!!");
                    break;
                }
                mServerBinder.getNetworkServer().start();
            }
            break;
        case R.id.stop_server_socket:
            if (mIsServerBind) {
                if (mServerBinder.getNetworkServer() == null) {
                    Log.v(TAG, "network server is null!!!");
                    break;
                }
                mServerBinder.getNetworkServer().stop();
            }
            break;
        case R.id.start_client_socket:
            if (mIsClientBind) {
                if (mClientBinder.getNetworkClient() == null) {
                    Log.v(TAG, "network server is null!!!");
                    break;
                }
                mClientBinder.getNetworkClient().start();
            }
            break;
        case R.id.stop_client_socket:
            if (mIsClientBind) {
                if (mClientBinder.getNetworkClient() == null) {
                    Log.v(TAG, "network server is null!!!");
                    break;
                }
                mClientBinder.getNetworkClient().stop();
            }
            break;
        default:
            break;
        }
    }

    private ServiceConnection mServerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            mIsServerBind = true;
            mServerBinder = ((LocationSocketServer.LocalBinder) arg1)
                    .getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mIsServerBind = false;
            mServerBinder = null;
        }
    };

    private ServiceConnection mClientConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIsClientBind = true;
            mClientBinder = ((LocationSocketClient.LocalBinder) service)
                    .getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsClientBind = false;
            mClientBinder = null;
        }

    };
}
