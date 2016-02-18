package com.example.android.okcupidassessment.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * Created by Sohail on 2/17/16.
 *
 * A simple broadcast receiver to check network state
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    public NetworkAvailableListener networkAvailableListener = null;

    public NetworkStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isConnectedToInternet(context)) {
            Utils.setIsConnected(true);
            if (Utils.DEBUG) Log.i(TAG, "Connected to network");
            networkAvailableListener.networkAvailable();
        } else {
            Utils.setIsConnected(false);
            if (Utils.DEBUG) Log.i(TAG, "Not connected to network");
        }
    }

    public boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Callback implemented by {@link com.example.android.okcupidassessment.fragment.SearchFragment}
     * to make network calls once network is available
     */
    public interface NetworkAvailableListener {
        void networkAvailable();
    }
}