package com.techsavanna.shiloahmsk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.PrintStream;

public class NetworkState {
    static Context context;
    private static NetworkState instance = new NetworkState();
    boolean connected = false;
    ConnectivityManager connectivityManager;

    public static NetworkState getInstance(Context context2) {
        context = context2.getApplicationContext();
        return instance;
    }

    public boolean isConnected() {
        try {
            ConnectivityManager connectivityManager2 = (ConnectivityManager) context.getSystemService("connectivity");
            this.connectivityManager = connectivityManager2;
            if (connectivityManager2 != null) {
                NetworkInfo activeNetworkInfo = connectivityManager2.getActiveNetworkInfo();
                NetworkCapabilities networkCapabilities = this.connectivityManager.getNetworkCapabilities(this.connectivityManager.getActiveNetwork());
                this.connected = activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected() && networkCapabilities != null && networkCapabilities.hasCapability(16);
            }
        } catch (Exception e) {
            PrintStream printStream = System.out;
            printStream.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return this.connected;
    }
}
