package com.example.android.okcupidassessment.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.okcupidassessment.activity.MainActivity;

/**
 * Created by Sohail on 2/12/16.
 */
public class Utils {

    public static final String REQUEST_URL = "https://www.okcupid.com/matchSample.json";
    public static final boolean DEBUG = true;
    private static boolean isConnected;

    /**
     * checks to see if network is currently available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static boolean isConnected() {
        return isConnected;
    }

    /**
     * {@link NetworkStateReceiver} updates {@link isConnected} flag based on network's availability
     */
    public static void setIsConnected(boolean isConnected) {
        Utils.isConnected = isConnected;
    }
}
