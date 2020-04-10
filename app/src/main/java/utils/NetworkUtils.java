package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by cognoscis on 13/3/18.
 */

public class NetworkUtils {

    public static final int TYPE_NOT_CONNECTED = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_ETHERNET = 3;

    public static int getConnectivityStatus(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
/*
        connectivityManager.getActiveNetworkInfo();
        This may return null when there is no default network.
        Note that if the default network is a VPN, this method will return the NetworkInfo for one of its underlying networks instead,
        or null if the VPN agent did not specify any.
*/
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return TYPE_ETHERNET;
            }
        }

        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtils.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtils.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtils.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtils.TYPE_ETHERNET) {
            status = "Ethernet enabled";
        } else if (conn == NetworkUtils.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
}
