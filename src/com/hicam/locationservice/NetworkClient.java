package com.hicam.locationservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class NetworkClient {
    public static final String TAG = "NetworkClient";

    private String mHost = "localhost";
    private int mPort = 8000;
    private Socket mSocket;
    private static NetworkClient sNetworkClient;
    private ExecutorService mExecutorService;
    private SubMainHandler mSubMainHandler;
    private boolean mRun = true;

    private NetworkClient() throws IOException {
        int avp = Runtime.getRuntime().availableProcessors();
        Log.v(TAG, "availableProcessors:" + avp);
        mExecutorService = Executors.newFixedThreadPool(avp);
    }

    public static final NetworkClient getInstance() throws IOException {
        if (sNetworkClient != null) {
            return sNetworkClient;
        }
        sNetworkClient = new NetworkClient();
        return sNetworkClient;
    }

    public void start() {
        Log.v(TAG, "start()");
        mRun = true;
        // if (mSubMainHandler != null) {
        // return;
        // }
        mSubMainHandler = new SubMainHandler();
        mExecutorService.execute(mSubMainHandler);
    }

    public void stop() {
        mRun = false;
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            Log.v(TAG, "close socket fail");
            e.printStackTrace();
        }
        // mExecutorService.shutdown();
    }

    private PrintWriter getWriter(Socket socket) {
        OutputStream socketOut;
        try {
            socketOut = socket.getOutputStream();
            return new PrintWriter(socketOut, true);
        } catch (IOException e) {
            Log.e(TAG, "getWriter() IOException");
            e.printStackTrace();
        }
        return null;
    }

    private BufferedReader getReader(Socket socket) {
        InputStream socketIn;
        try {
            socketIn = socket.getInputStream();
            return new BufferedReader(new InputStreamReader(socketIn));
        } catch (IOException e) {
            Log.e(TAG, "getReader() IOException");
            e.printStackTrace();
        }
        return null;
    }

    public void connect() throws IOException {
        Log.v(TAG, "connect()");
        mSocket = new Socket(mHost, mPort);
        Log.v(TAG, "new Socket done");
        BufferedReader br = null;
        PrintWriter pw = null;

        br = getReader(mSocket);
        pw = getWriter(mSocket);

        String msg = null;
        while (br != null && mRun) {
            Log.v(TAG, "while(" + mRun + " )");
            if ((msg = br.readLine()) != null) {
                // TODO handle msg
            }
            if (msg == null) {
                break;
            }
            Log.v(TAG, "msg:" + msg);
        }

        try {
            Log.v(TAG, "close socekt");
            mSocket.close();
            if (br != null) {
                Log.v(TAG, "close BufferedReader");
                br.close();
            }
            if (pw != null) {
                Log.v(TAG, "close PrintWriter");
                pw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SubMainHandler implements Runnable {

        @Override
        public void run() {
            try {
                connect();
            } catch (IOException e) {
                Log.e(TAG, "IOException:" + e.getMessage());
                e.printStackTrace();
            }
            Log.v(TAG, "SubMainHandler is over");
        }
    }
}
