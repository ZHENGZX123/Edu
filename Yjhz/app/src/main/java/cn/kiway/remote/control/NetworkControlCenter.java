package cn.kiway.remote.control;

import android.content.Context;
import android.location.Location;
import android.util.ArrayMap;

import cn.kiway.remote.monitor.app.ApksControlCenter;
import cn.kiway.remote.monitor.location.LocationInfo;
import cn.kiway.remote.monitor.security.KiwaySettings;

/**
 * Created by arvin on 2017/3/6 0006.
 */

public class NetworkControlCenter {

    public static NetworkControlCenter mNetworkControlCenter;

    public NetworkControlCenter() {

    }

    public static NetworkControlCenter getInstance() {
        return mNetworkControlCenter == null ? mNetworkControlCenter = new NetworkControlCenter()
                : mNetworkControlCenter;
    }

    public void restartPasswd(){
        KiwaySettings kiwaySettings = new KiwaySettings();
        kiwaySettings.restartPasswd();
    }

    public void restoreFactory(){
        KiwaySettings kiwaySettings = new KiwaySettings();
    }

    public double[] getLocationInfo(Context context){
        LocationInfo locationInfo = new LocationInfo(context);
        Location location = locationInfo.getLocation();
        double latitude = location.getLatitude(); //经度
        double longitude = location.getLongitude();
        double[] doubles= {latitude, longitude};
        return doubles ;
    }

    public ArrayMap<String,String> getAllApks(Context context){
        ApksControlCenter apkc = new ApksControlCenter();
        return apkc.getAllAppInfosList(context);
    }




}
