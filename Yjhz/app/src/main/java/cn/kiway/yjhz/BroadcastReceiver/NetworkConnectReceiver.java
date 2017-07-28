package cn.kiway.yjhz.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkConnectReceiver extends BroadcastReceiver {

    private static final String TAG = "TAG_NetworkConnectReceiver";

    //为电子圈栏中回调接口，如需要监听需新建接口。
    private WifiConnectState mWallWifiConnectState;

    @Override
    public void onReceive(Context context, Intent intent) {

        // 这个监听wifi的打开与关闭，与wifi的连接无关
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.d(TAG, "wifiState" + wifiState);

            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:

                    break;
                case WifiManager.WIFI_STATE_DISABLING:

                    break;
                case WifiManager.WIFI_STATE_ENABLING:

                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    if (mWallWifiConnectState != null)
                        mWallWifiConnectState.process(wifiState);
                    break;
            }
        }

        // 一个慢速广播。当网络状态发生变化，会发出。
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

        }
    }

    public void setWallWifiConnectState(WifiConnectState conn) {
        mWallWifiConnectState = conn;
    }
}


