package com.example.saveat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot check network status.");
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Log.e(TAG, "ConnectivityManager is null.");
            return false;
        }

        // Untuk Android M (API 23) dan yang lebih baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                Log.d(TAG, "No active network.");
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                Log.d(TAG, "Network capabilities are null.");
                return false;
            }

            // Memeriksa konektivitas yang tervalidasi (memiliki akses internet)
            boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            Log.d(TAG, "Network is validated: " + hasInternet);
            return hasInternet;

        } else {
            // Untuk versi Android yang lebih lama
            @SuppressWarnings("deprecation")
            android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                Log.d(TAG, "NetworkInfo is null for older Android version.");
                return false;
            }
            boolean isConnected = networkInfo.isConnected();
            Log.d(TAG, "Network is connected (older Android): " + isConnected);
            return isConnected;
        }
    }
}