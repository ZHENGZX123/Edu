package cn.kiway.status;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by arvin on 2017/3/6 0006.
 */

public class NetworkStatus {
    private static final String TAG = "Kiway_NetworkStatus";


    @SuppressLint("NewApi")
    public static String getNetworkInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            Log.w(TAG, " ConnectivityManager的实例无法获得");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                return info.getTypeName();
            }
        }

        return null;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.d(TAG, "ConnectivityManager的实例无法获得");
        } else {
            Network[] network = connectivity.getAllNetworks();
            if (network != null) {
                for (int i = 0; i < network.length; i++) {
                    NetworkInfo info = connectivity.getNetworkInfo(network[i]);
                    if (info != null)
                        return info.isAvailable();
                }
            }
        }

        return false;
    }

    /**
     * 检查当前网络连接状态
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static NetworkInfo.State checkNetState(Context context) {
        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            Network[] network = connectivity.getAllNetworks();
            if (network != null) {
                for (int i = 0; i < network.length; i++) {
                    NetworkInfo info = connectivity.getNetworkInfo(network[i]);


                    if (info != null)
                        return info.getState();

                }
            }
        }
        return null;
    }

    /**
     * 当前网络连接的连接状态
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
                return info.isConnected();

        }
        return false;

    }

    /**
     * 检查漫游状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null
                    && info.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null && tm.isNetworkRoaming()) {
                    return true;
                } else {
                }
            } else {
            }
        }
        return false;
    }

    /**
     * 检查移动网络是否启用
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isMobileDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileDataEnable = false;

        isMobileDataEnable = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

        return isMobileDataEnable;
    }

    /**
     * 检查WIFI是否启用
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isWifiDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiDataEnable = false;
        isWifiDataEnable = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        return isWifiDataEnable;
    }
}
