package cn.kiway.remote.monitor.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import cn.kiway.yjhz.BroadcastReceiver.WifiConnectState;

/**
 * Created by arvin on 2017/3/6 0006.
 */

@SuppressLint("NewApi")
public class WifiList {

    public static final String TAG = "TAG_WifiList";
    public Context mContext;
    public ArrayMap<String, KiWifiInfo> wifiArrayMap = new ArrayMap<String, KiWifiInfo>();
    public KiWifiManage mKiWifiManager;

    public WifiConnectState mWifiConnectState;

    public WifiList(Context context) {
        mContext = context;
        getKiWifiInfos();
        initKiWifiManage();

        wifiArrayMap.put("KWHUAWEI",new KiWifiInfo("KWHUAWEI","KWF58888","",""));
       // wifiArrayMap.put("KWTEST",new KiWifiInfo("KWTEST","KWF58888","",""));
       // wifiArrayMap.put("KWHUAWEI_5G",new KiWifiInfo("KWHUAWEI_5G","KWF58888","",""));
       // wifiArrayMap.put("KWHW2",new KiWifiInfo("KWHW2","KWF58888","",""));
    }

    public ArrayMap<String, KiWifiInfo> getKiWifiInfos() {


        return null;
    }


    public void initKiWifiManage() {

        mKiWifiManager = new KiWifiManage(mContext);

        startConnect();
        //pushPasswdConnect();

    }

    //电子墙内开始连接
    public boolean startConnect() {
        return mKiWifiManager.connect(wifiArrayMap);
    }

    //电子墙内，更新密码连接
    public boolean pushPasswdConnect() {

       return mKiWifiManager.connectPasswd(wifiArrayMap);

    }




}
