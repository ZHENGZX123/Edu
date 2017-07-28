package cn.kiway.remote.monitor.app;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by arvin on 2017/3/7 0007.
 */

public class ApksControlCenter {

    private static Class ServiceManager;
    private static Method getService;
    private static Object oRemoteService;
    private static Class cStub;
    private static Method asInterface;
    private static Object oIUsageStats;
    private static Method getPkgUsageStats = null;
    private static ApksControlCenter mApkinfo;

    public ApksControlCenter() {


    }

    public static ApksControlCenter getInstance() {
        return mApkinfo == null ? mApkinfo = new ApksControlCenter() : mApkinfo;
    }

    public int getLaunchCount(ApplicationInfo app) {
        return getLaunchCount(app.packageName, app.className);
    }

    public long getUseTime(ApplicationInfo app) {
        return getUseTime(app.packageName, app.className);
    }

    private void initMethod() {
        if (getPkgUsageStats == null)
            try {
                // 获得ServiceManager类
                ServiceManager = Class
                        .forName("android.os.ServiceManager");
                // 获得ServiceManager的getService方法
                getService = ServiceManager.getMethod("getService",
                        String.class);
                // 调用getService获取RemoteService
                oRemoteService = getService.invoke(null, "usagestats");
                // 获得IUsageStats.Stub类
                cStub = Class
                        .forName("com.android.internal.app.IUsageStats$Stub");
                // 获得asInterface方法
                asInterface = cStub.getMethod("asInterface",
                        IBinder.class);
                // 调用asInterface方法获取IUsageStats对象

                oIUsageStats = asInterface.invoke(null, oRemoteService);
                // 获得getPkgUsageStats(ComponentName)方法
                getPkgUsageStats = oIUsageStats.getClass().getMethod(
                        "getPkgUsageStats", ComponentName.class);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

    }

    public int getLaunchCount(String packageName, String className) {
        ComponentName aName = new ComponentName(packageName, className);
        int aLaunchCount = 0;
        initMethod();
        try {
            // 调用getPkgUsageStats 获取PkgUsageStats对象
            Object aStats = getPkgUsageStats.invoke(oIUsageStats, aName);
            // 获得PkgUsageStats类
            Class PkgUsageStats = Class
                    .forName("com.android.internal.os.PkgUsageStats");
            //获取启动次数
            aLaunchCount = PkgUsageStats.getDeclaredField("launchCount")
                    .getInt(aStats);
        } catch (Exception e) {
            Log.e("###", e.toString(), e);
        }
        return aLaunchCount;
    }

    public long getUseTime(String packageName, String className) {
        ComponentName aName = new ComponentName(packageName, className);
        long aUseTime = 0;

        initMethod();
        try {
            // 调用getPkgUsageStats 获取PkgUsageStats对象
            Object aStats = getPkgUsageStats.invoke(oIUsageStats, aName);
            // 获得PkgUsageStats类
            Class PkgUsageStats = Class
                    .forName("com.android.internal.os.PkgUsageStats");
            //获取启动时间
            aUseTime = PkgUsageStats.getDeclaredField("usageTime").getLong(
                    aStats);

        } catch (Exception e) {
            Log.e("###", e.toString(), e);
        }
        return aUseTime;
    }

    @SuppressLint("NewApi")
    public ArrayMap<String, String> getAllAppInfosList(Context context) {
        ArrayMap<String, String> arraymap = new ArrayMap<String, String>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);

        for (ApplicationInfo app : apps) {
            // ApplicationInfo.FLAG_SYSTEM为系统应用
            // ApplicationInfo.FLAG_EXTERNAL_STORAGE为非系统应用
            int flags = app.flags;
            String packageName = app.packageName;
            String appName = app.loadLabel(pm).toString();
            Drawable appDrawable = app.loadIcon(pm);
            String className = app.className;
            //app.labelRes;
            // app.icon;
            arraymap.put(appName, packageName);
        }
        return arraymap;
    }

    @SuppressLint("NewApi")
    public ArrayMap<String, String> getSystemAppInfosList(Context context) {
        ArrayMap<String, String> arraymap = new ArrayMap<String, String>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);

        for (ApplicationInfo app : apps) {
            // ApplicationInfo.FLAG_SYSTEM为非系统应用
            if (app.flags == ApplicationInfo.FLAG_SYSTEM) {
                String packageName = app.packageName;
                String appName = app.loadLabel(pm).toString();
                Drawable appDrawable = app.loadIcon(pm);
                String className = app.className;
                String nodeLabeRes = context.getResources().getString(app.labelRes);
                Drawable nodeDrawable = context.getDrawable(app.icon);
                arraymap.put(appName,packageName);
            }
        }

        return arraymap;
    }

    @SuppressLint("NewApi")
    public ArrayMap<String, String> getUserAppInfosList(Context context) {
        ArrayMap<String, String> arraymap = new ArrayMap<String, String>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES);

        for (ApplicationInfo app : apps) {
            // ApplicationInfo.FLAG_EXTERNAL_STORAGE为非系统应用
            if (app.flags == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                String packageName = app.packageName;
                String appName = app.loadLabel(pm).toString();
                Drawable appDrawable = app.loadIcon(pm);
                String className = app.className;
                String nodeLabeRes = context.getResources().getString(app.labelRes);
                Drawable nodeDrawable = context.getDrawable(app.icon);
                arraymap.put(appName,packageName);
            }
        }

        return arraymap;
    }

}
