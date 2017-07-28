package cn.kiway.remote.monitor.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.kiway.status.NetworkStatus;

/**
 * Created by arvin on 2017/3/6 0006.
 */

public class KiWifiManage {

    private static final String TAG = "kiway_KiWifiManage";
    private Context mContext;
    private ArrayList<WifiInfo> wifiInfoArrayList;
    private ExecutorService mExecutorService;//WIFI连接线程

    public WifiManager mWifiManager;
    private android.net.wifi.WifiInfo mWifiInfo;

    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiManager.WifiLock mWifiLock;

    public KiWifiManage(Context context) {
        mContext = context;
        // Read();
        initWifiManage();
    }


    public ArrayList<WifiInfo> getWifiInfos() {
        return wifiInfoArrayList;
    }

    public ArrayList<WifiInfo> Read() {
        wifiInfoArrayList = new ArrayList<WifiInfo>();

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream
                    .writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }


        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {
                WifiInfo wifiInfo = new WifiInfo();

                wifiInfo.setSsid(ssidMatcher.group(1));

                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find()) {
                    wifiInfo.setPasswd(pskMatcher.group(1));
                } else {
                    wifiInfo.setPasswd("");
                }

                Pattern keyMgmt = Pattern.compile("key_mgmt=\"([^\"]+)\"");
                Matcher keyMgmtMatcher = keyMgmt.matcher(networkBlock);
                wifiInfo.setKeyMgmt(pskMatcher.group(1));

                Pattern priority = Pattern.compile("priority=\"([^\"]+)\"");
                Matcher priorityMatcher = priority.matcher(networkBlock);
                wifiInfo.setPriority(priorityMatcher.group(1));
                wifiInfoArrayList.add(wifiInfo);
            }
        }

        return wifiInfoArrayList;
    }


    public class WifiInfo {
        private String ssid;
        private String passwd;
        private String keyMgmt;
        private String priority;


        public String getSsid() {
            return this.ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public String getKeyMgmt() {
            return keyMgmt;
        }

        public void setKeyMgmt(String key_mgmt) {
            this.keyMgmt = key_mgmt;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

    }


    //=======
    public void initWifiManage() {

        // 取得WifiManager对象
        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        openWifi();

        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        creatWifiLock();
        startScan();
    }

    private void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }


    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(mContext, "尝试打开WIFI失败,请手动打开开关,然后返回",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(android.provider.
                                    Settings.ACTION_WIFI_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    mContext.startActivity(intent);
                    break;

                case 2:
                    Toast.makeText(mContext, "WIFI打开成功",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    // 打开WIFI
    public void openWifi() {
        final int flag = mWifiManager.getWifiState();

        if(flag == WifiManager.WIFI_STATE_ENABLED)
            return;

        if (mExecutorService == null)
            mExecutorService = Executors.newFixedThreadPool(1);

        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                mWifiManager.setWifiEnabled(true);
                try {

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final int flag = mWifiManager.getWifiState();
                switch (flag) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.d(TAG, "WifiManager.WIFI_STATE_DISABLED");
                        mWifiManager.setWifiEnabled(true);
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.d(TAG, "WifiManager.WIFI_STATE_DISABLING");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d(TAG, "WifiManager.WIFI_STATE_ENABLED");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.d(TAG, "WifiManager.WIFI_STATE_ENABLING");
                        handler.sendEmptyMessage(2);
                        return;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Log.d(TAG, "WifiManager.WIFI_STATE_UNKNOWN");
                        break;
                }

                handler.sendEmptyMessage(1);
            }
        });

    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }


    private boolean connectConfiguration(String ssid) {

        for (WifiConfiguration wifiCon : mWifiConfiguration) {
            Log.d(TAG, "已配制的网络,wifiCon.SSID： " + wifiCon.SSID);
            if (wifiCon.SSID.replace("\"", "").equals(ssid)) {
                Log.d(TAG, "连接已保存的网络： " + ssid);
                // 连接存在的网格
                return mWifiManager.enableNetwork(wifiCon.networkId, true);
            } else {
                Log.d(TAG, "无法上接连接未保存未网： " + ssid);
            }
        }
        return false;
    }

    public List<WifiConfiguration> getWifiConfigurationList() {
        if (mWifiConfiguration == null)
            mWifiConfiguration = mWifiManager.getConfiguredNetworks();
        return mWifiConfiguration;
    }

    public StringBuilder lookUpSavedList() {
        StringBuilder stringBuilder = new StringBuilder();
        List<WifiConfiguration> configs = getWifiConfigurationList();
        for (int i = 0; i < configs.size(); i++) {
            stringBuilder.append(configs.get(i).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }


    // 得到扫描到的网络列表
    public List<ScanResult> getScaWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到IP地址
    public String getIPAddress() {
        return (mWifiInfo == null) ? "null" : intToIp(mWifiInfo.getIpAddress());
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);

    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public boolean disconnectWifi(String ssid) {
        for (WifiConfiguration wifiCon : mWifiConfiguration) {
            if (wifiCon.SSID.equals(ssid)) {
                Log.d(TAG, "断开连接的网络： " + ssid);
                // 断开连接的网络
                mWifiManager.disableNetwork(wifiCon.networkId);
                return mWifiManager.disconnect();
            }
        }
        return false;
    }

    public static enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    @SuppressLint("NewApi")
    public boolean connect(ArrayMap<String, KiWifiInfo> arrayMap) {
        Log.d(TAG, "connect");
        ArrayMap<String, KiWifiInfo> map = arrayMap;

        if (mWifiList == null)
            mWifiList = mWifiManager.getScanResults();

        //对已扫描的WIFI列号按信号强弱排序。
        sortByLevel(mWifiList);
        for (int j = 0; j < map.size(); j++) {
            //优先对信号对好的WIFI进行连接
            for (int i = 0; i < mWifiList.size(); i++) {
                String mssid = mWifiList.get(i).SSID;
                int level = mWifiList.get(i).level;

                Log.d(TAG, "mWifiList.get(i).SSID: " + mWifiList.get(i).SSID
                        + "\n mWifiList.get(i).level:" + level
                        + "\n map.valueAt(j).getSsid(): " + map.valueAt(j).getSsid());

                //扫描出的与白名单对比
                if (mssid.equals(map.valueAt(j).getSsid())) {
                    //WIFI有连接，但不是连接的白名单
                    if (this.getSSID() == null || !this.getSSID().equals(mssid)) {
                        if (connectConfiguration(mssid)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void sortByLevel(List<ScanResult> list) {

        Collections.sort(list, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                return rhs.level - lhs.level;
            }
        });
    }

    @SuppressLint("NewApi")
    public boolean connectPasswd(ArrayMap<String, KiWifiInfo> m) {
        Log.d(TAG, "connectPasswd");
        ArrayMap<String, KiWifiInfo> map = m;
        if (mWifiList == null)
            mWifiList = mWifiManager.getScanResults();
        //对已扫描的WIFI列号按信号强弱排序。
        sortByLevel(mWifiList);

        for (int j = 0; j < map.size(); j++) {
            for (int i = 0; i < mWifiList.size(); i++) {
                String mssid = mWifiList.get(i).SSID;

                Log.d(TAG, "mWifiList.get(i).SSID: " + mssid
                        + "\n map.valueAt(j).getSsid(): " + map.valueAt(j).getSsid());

                if (mssid.equals(map.valueAt(j).getSsid())) {
                    if (connect(map.valueAt(j).getSsid(), map.valueAt(j).getPasswd(), null)) {

                        return true;
                    } else {
                        Log.d(TAG, "预置WIFI未连接成功: " + mssid);
                    }
                } else {
                    Log.d(TAG, "mWifiList.get(i).SSID: " + mssid
                            + "!=  map.valueAt(j).getSsid(): " + map.valueAt(j).getSsid());
                }
            }
        }

        return false;
    }

    //对外的连接接口。
    public boolean connect(String SSID, String Password, WifiCipherType Type) {
       /* if(!openWifi())
            return false;
        //打开WIFI是耗时，故需循环监听。

        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(300);
            } catch (InterruptedException ie) {
            }
        }*/

        WifiConfiguration wifiConfig = this.createWifiInfo(SSID, Password, Type);
        //
        if (wifiConfig == null) {
            return false;
        }
        WifiConfiguration tempConfig = this.IsExsits(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        /*
             try {
            //高级选项
            String ip  ="192.168.1.201";
            int networkPrefixLength =24;
            InetAddress intetAddress  = InetAddress.getByName(ip);
            int intIp = inetAddressToInt(intetAddress);
            String dns = (intIp & 0xFF ) + "." + ((intIp >> 8 ) & 0xFF) + "." + ((intIp >> 16 ) & 0xFF) + ".1";
            //"STATIC" or "DHCP" for dynamic setting
            setIpAssignment("STATIC", wifiConfig);
            setIpAddress(intetAddress, networkPrefixLength, wifiConfig);
            setGateway(InetAddress.getByName(dns), wifiConfig);
            setDNS(InetAddress.getByName(dns), wifiConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        int netID = mWifiManager.addNetwork(wifiConfig);
        boolean bRet = mWifiManager.enableNetwork(netID, true);
        //mWifiManager.updateNetwork(wifiConfig);


        return bRet;
    }


    //尝试传入密码重连
    private WifiConfiguration createWifiInfo(String SSID, String Password,
                                             WifiCipherType Type) {
        Log.d(TAG, "createWifiInfo");

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == null)
            Type = WifiCipherType.WIFICIPHER_WPA;

        // no passwd
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wep
        else if (Type == WifiCipherType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                if (isHexWepKey(Password)) {
                    config.wepKeys[0] = Password;
                } else {
                    config.wepKeys[0] = "\"" + Password + "\"";
                }
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        else if (Type == WifiCipherType.WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String ssid) {
        Log.d(TAG, "IsExsits ---SSID: " + ssid);
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            Log.d(TAG, "Exsit--SSID: " + existingConfig.SSID.replace("\"", ""));
            if (existingConfig.SSID.replace("\"", "").equals(ssid)) {
                return existingConfig;
            } else {
                Log.d(TAG, "***** ");
            }
        }
        return null;
    }

    private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }

    /***
     * Convert a IPv4 address from an InetAddress to an integer
     *
     * @param inetAddr is an InetAddress corresponding to the IPv4 address
     * @return the IP address as an integer in network byte order
     */
    public static int inetAddressToInt(InetAddress inetAddr)
            throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        if (addr.length != 4) {
            throw new IllegalArgumentException("Not an IPv4 address");
        }
        return ((addr[3] & 0xff) << 24) | ((addr[2] & 0xff) << 16) |
                ((addr[1] & 0xff) << 8) | (addr[0] & 0xff);
    }

    public static void setIpAssignment(String assign, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[]{InetAddress.class, int.class});
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);
        ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[]{InetAddress.class});
        Object routeInfo = routeInfoConstructor.newInstance(gateway);
        ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // or add a new dns address , here I just want to replace DNS1
        mDnses.add(dns);
    }

    public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

}
