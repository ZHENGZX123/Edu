package cn.kiway.remote.monitor.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by arvin on 2017/3/10 0010.
 */

public class LocationInfo {
    private Context mContext;
    private String serviceString;
    private LocationManager locationManager;

    private String gpsProvider = LocationManager.GPS_PROVIDER;
    private String networkProvider = LocationManager.NETWORK_PROVIDER;


    public LocationInfo(Context context) {
        mContext = context;
        serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) mContext.getSystemService(serviceString);
    }


    //注册位置改变监听
    public void initLocation() {
        if (gpsProvider != null && gpsLocationListener != null)
            locationManager.requestLocationUpdates(gpsProvider, 2000, 10, gpsLocationListener);
        if (networkProvider != null && networkLocationListener != null)
            locationManager.requestLocationUpdates(networkProvider, 2000, 10, networkLocationListener);
    }

    //通过网络获取地址,用于测试
    public Location getLocation(){
        Location location = locationManager.getLastKnownLocation(networkProvider);
        return location;
    }



    private final LocationListener gpsLocationListener = new LocationListener() {

        //满足位移或者时间触发
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

        }

        //GPS断开时触发
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        //禁用定位服务时
        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }
        //启用定位服务时
        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

    };

    private final LocationListener networkLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

    };

}
