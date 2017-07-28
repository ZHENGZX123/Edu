package cn.kiway.remote.monitor.app;

import android.os.SystemClock;

/**
 * Created by arvin on 2017/3/6 0006.
 */

public class ApksInfo {

    private static final String TAG = "kiway_ApksInfo";
    private String packageName;
    private String className;
    private String appName;

    public ApksInfo(){

    }

    public ApksInfo( String packageName,String className,String appName){
        this.packageName = packageName;
        this.className = className;
        this.appName = appName;

    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
