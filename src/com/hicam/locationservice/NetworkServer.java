package com.hicam.locationservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.location.Location;
import android.util.Log;

public class NetworkServer {
    public static final String TAG = "NetworkServer";

    private final int sINTERVAL = 300;
    private int mPort = 8000;
    private ServerSocket mServerSocket;
    private ArrayList<Socket> mAcceptSocketList = new ArrayList<Socket>();
    private SendHandler mSendHandler;
    private boolean mRun = true;
    private ExecutorService mExecutorService;
    private final int POOL_SIZE = 4;
    private static NetworkServer sNetworkServer;

    private NetworkServer() {
        int avp = Runtime.getRuntime().availableProcessors();
        Log.v(TAG, "availableProcessors:" + avp);
        mExecutorService = Executors.newFixedThreadPool(avp * POOL_SIZE);

        Log.v(TAG, "Server launch");
    }

    private void service() {
        mExecutorService.execute(new SubMainHandler());
    }

    public static final NetworkServer getInstance() throws IOException {
        if (sNetworkServer != null) {
            return sNetworkServer;
        }

        sNetworkServer = new NetworkServer();
        return sNetworkServer;
    }

    public void start() {
        Log.v(TAG, "start()");
        mRun = true;
        service();
    }

    public void stop() {
        Log.v(TAG, "stop()");
        mRun = false;
        long beginTime = System.currentTimeMillis();
        if(mSendHandler != null) {
            mSendHandler.removeAll();
        }
        try {
            mServerSocket.close();
            for(Socket s: mAcceptSocketList) {
                s.close();
            }
            mAcceptSocketList.clear();
        } catch (IOException e) {
            Log.v(TAG, "stop() close server fail");
            e.printStackTrace();
        }

//        mExecutorService.shutdown();
//        while (!mExecutorService.isTerminated()) {
//            Log.v(TAG, "stop() wait thread terminated"); 
//            try {
//                // time out 1 second
//                mExecutorService.awaitTermination(1, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "terminate thread pool fail");
//                e.printStackTrace();
//            }
//        }
        long endTime = System.currentTimeMillis();
        Log.v(TAG, "close server duration:" + (endTime - beginTime));
    }

    public void setLocation(Location l) {

    }

    private class SubMainHandler implements Runnable {

        @Override
        public void run() {
            while (mRun) {
                Socket socket = null;
                try {
                    socket = mServerSocket.accept();
                    // doc.api.java:Executes the given command at some time in
                    // the
                    // future.
                    mAcceptSocketList.add(socket);
                    if (mSendHandler == null) {
                        mSendHandler = new SendHandler();
                        mExecutorService.execute(mSendHandler);
                    }

                    mExecutorService.execute(new ListenHandler(socket,
                            mSendHandler));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.v(TAG,"SubMainHandler is over");
        }

    }

    private class SendHandler implements Runnable {
        ArrayList<PrintWriter> mSoclist = new ArrayList<PrintWriter>();
        

        public void addSocket(PrintWriter s) {
            Log.v(TAG, "addSocket," + s);
            if (mSoclist.contains(s)) {
                return;
            }
            mSoclist.add(s);
        }

        public void removeSocket(PrintWriter s) {
            Log.v(TAG, "removeSocket," + s);
            mSoclist.remove(s);
        }

        public void removeAll() {
            mSoclist.clear();
        }
        @Override
        public void run() {
            while (mRun) {
                try {
                    Thread.sleep(sINTERVAL);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.v(TAG, "Send thread is die!!!");
                    break;
                }

                if (mSoclist.size() == 0) {
//                    Log.v(TAG, "not socket need send, continue");
                    continue;
                }
                Log.v(TAG, "Send thread send message");
                for (PrintWriter pw : mSoclist) {
                    pw.println("test sender");
                }
            }

        }

    }

    private class ListenHandler implements Runnable {
        private Socket socket;
        private BufferedReader mBr;
        private PrintWriter mPw;
        private SendHandler sender;

        public ListenHandler(Socket socket, SendHandler sender) {
            this.socket = socket;
            this.sender = sender;
        }

        private PrintWriter getWriter(Socket socket) throws IOException {
            OutputStream socketOut = socket.getOutputStream();
            return new PrintWriter(socketOut, true);
        }

        private BufferedReader getReader(Socket socket) throws IOException {
            InputStream socketIn = socket.getInputStream();
            return new BufferedReader(new InputStreamReader(socketIn));
        }

        public void run() {
            try {
                Log.v(TAG, "New connection accepted " + socket.getInetAddress()
                        + ":" + socket.getPort());
                mBr = getReader(socket);
                mPw = getWriter(socket);

                sender.addSocket(mPw);
                String msg = null;

                while (mRun) {
                    if ((msg = mBr.readLine()) == null) {
                        break;
                    }
                    // TODO handle msg
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    mBr.close();
                    mPw.close();
                    if (socket != null) {
                        Log.v(TAG, "Thread " + Thread.currentThread().getId()
                                + " close socket");
                        sender.removeSocket(mPw);
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.v(TAG, "Thread " + Thread.currentThread().getId()
                    + " is over");
        }
    }
}
