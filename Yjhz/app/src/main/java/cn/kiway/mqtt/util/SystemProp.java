package cn.kiway.mqtt.util;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import cn.kiway.remote.monitor.BootStatus;
import cn.kiway.remote.monitor.wifi.KiWifiManage;
import cn.kiway.yjhz.R;
import cn.kiway.yjhz.soc.CpuInfo;
import cn.kiway.yjhz.soc.RAMInfo;
import cn.kiway.yjhz.strategy.ChangeState;
import cn.kiway.yjhz.tfcard.TFCard;
import cn.kiway.yjhz.wifimanager.WifiAdmin;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class SystemProp {

    private Context mCotext;
    private static SystemProp mSystemProp;
    public static final String TAG = "kiway_SystemProp";
    //固件版本号
    public static final String BUILD_VERSION = "ro.build.display.id";
    //机器型号
    public static final String MODEL_NUMBER = "ro.product.model";
    //产品型号,一般同机器型号
    public static final String PRODUCT = "ro.build.product";
    //SDK版本
    public static final String SDK_VERSION = "ro.build.version.sdk";
    //android版本
    public static final String ANDROID_VERSION = "ro.build.version.release";
    //内核版本
    public static final String KERNEL_VERSION = "ro.build.date";
    //工程版本
    public static final String TYPE_VERSION = "ro.build.type";
    //CPU版本(架构)
    public static final String CPU_ABI = "ro.product.cpu.abi";
    //主板版本
    public static final String BOARD_VERSION = "ro.board.platform";
    //制造商
    public static final String MANUFACTURER = "ro.product.manufacturer";
    //默认时区
    public static final String DEF_TIME_ZONE = "persist.sys.timezone";
    //默认语言
    public static final String DEF_LANGUAGE = "ro.product.locale.language";
    //默认地区
    public static final String DEF_REGION = "ro.product.locale.region";


    //堆栈初始值
    public static final String HEAP_START_SIZE = "dalvik.vm.heapstartsize";
    //受控堆最大值
    public static final String HEAP_GROWTHLIMIT = "dalvik.vm.heapgrowthlimit";
    //不受控堆栈最大值,超过此值,则会OOM
    public static final String HEAP_SIZE = "dalvik.vm.heapsize";
    //内存使用率,影响后台应用GC
    public static final String HEAP_TARGETUTILIZATION = "dalvik.vm.heaptargetutilization";


    //序列号
    public static final String ANDROID_ID = "android_id";
    //APP版本
    public static final String APK_VERSION = "apk_version";
    //APP名称
    public static final String APK_NAME = "apk_name";
    //APP包名
    public static final String APK_PACKAGE = "apk_package";
    //WIFI MAC
    public static final String WIFI_MAC = "wifi_mac";
    //蓝牙MAC
    public static final String BT_MAC = "bt_mac";
    //所在时区
    public static final String TIME_ZONE = "time_zone";
    //
    public static final String REGION = "region";
    //CPU最大主频
    public static final String CPU_MAX_CUR = "cpu_max_cur";
    //RAM总容量
    public static final String RAM_TOTAL_CAPACITY = "ram_total_capacity";
    //ROM总容量
    public static final String ROM_TOTAL_CAPACITY = "rom_total_capacity";
    //网络状态
    public static final String NETWORK_STATUS = "network_status";
    //IP地址
    public static final String IP_ADDS = "ip_adds";
    //IMEI值
    public static final String IMEI = "imei";
    //NFC
    public static final String NFC = "nfc";

    //默认通知声
    public static final String DEF_RINGTONE = "ro.config.ringtone";
    //默认来电铃声
    public static final String DEF_NOTIFICATION = "ro.config.notification_sound";
    //默认闹铃声
    public static final String DEF_ALARM_ALERT = "ro.config.alarm_alert";
    //通知声
    public static final String RINGTONE = "ringtione";
    //来电声
    public static final String NOTIFICATION = "notification_sound";
    //闹铃声
    public static final String ALARM_ALERT = "alarm_alert";
    //主卡总容量(Data)
    public static final String  EXTERNAL_STORAGE_DIR = "ExternalStorageDirectory";
    //副卡总容量(外置SD卡)
    public static final String SECONDRY_STORAGE_DIR = "SecondaryStorageDirectory";
    //主卡剩余容量
    public static final String  AVAILABLE_EXTERNAL_STORAGE_DIR = "AvailableExternalStorageDirectory";
    //副卡剩余容量
    public static final String AVAILABLE_SECONDRY_STORAGE_DIR = "AvailableSecondaryStorageDirectory";

    //所有APK清单
    //public static final String ALL_APK_LIST = "allApkList";
    //用户APK清单
    //public static final String USER_APK_LIST = "userApkList";
    //系统APK清单
    //public static final String SYSTEM_APK_LIST = "systemApkList";
    //本次系统开机唤醒时间
    public static final String UPTIME_MILLIS = "uptimeMillis";
    //本次系统开机总时间
    public static final String ELAPSED_REALTIME = "elapsedRealTime";
    //激活开机总时间

    private List<String> sPropList;

    private String[] systemPropKeys;

    private String[] allKeys;

    private String[][] allKeysTwoDimensional;

    private ApplicationInfo KiwayAi;
    private PackageInfo KiwayPi;

    private SystemMap mSystemMap;

    public static SystemProp getInstance(Context context) {
        return mSystemProp == null ? mSystemProp = new SystemProp(context) : mSystemProp;
    }

    public SystemProp(Context context) {

        mCotext = context;
        systemPropKeys = mCotext.getResources()
                .getStringArray(R.array.system_prop_key);
        allKeys = mCotext.getResources()
                .getStringArray(R.array.allKeys);
        allKeysTwoDimensional = getTwoDimensionalArray(allKeys);


        if (mSystemMap == null) {
            String typeDate = mCotext.getResources().getString(R.string.tpye_of_data);
            mSystemMap = new SystemMap(typeDate);
        }

        if (sPropList == null)
            sPropList = Arrays.asList(systemPropKeys);
    }

    private Object updateSystemPropInfo(String key[]) {

        return updateSystemPropInfo(key[0], key[1]);
    }

    private Object updateSystemPropInfo(String kkey, String kvalue) {
        if (sPropList == null)
            sPropList = Arrays.asList(systemPropKeys);

        boolean flag = sPropList.contains(kvalue);
        Object value = null;


        if (flag) {
            value = "";
        } else {
            value = getOtherSystemInfo(kvalue);
        }


        if (value != null)
            mSystemMap.put(kkey, value);

        return value;

    }

    @SuppressLint("NewApi")
    private Object getOtherSystemInfo(String key) {
        Object value = null;
        switch (key) {
            case ANDROID_ID:
                value = Settings.Secure.getString(mCotext.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                break;

            case APK_VERSION:

                if (KiwayPi == null) {
                    PackageManager pm = mCotext.getPackageManager();
                    try {
                        KiwayPi = pm.getPackageInfo(mCotext.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                if (KiwayPi != null)
                    value = KiwayPi.versionName;

                break;
            case APK_PACKAGE:

                value = mCotext.getPackageName();

                break;

            case APK_NAME:

                if (KiwayAi == null) {
                    PackageManager pm = mCotext.getPackageManager();
                    try {
                        KiwayAi = pm.getApplicationInfo(mCotext.getPackageName(), 0);

                        if (KiwayAi != null)
                            value = (String) pm.getApplicationLabel(KiwayAi);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case WIFI_MAC:
                value = WifiAdmin.getDefault(mCotext).getWifiMacAddress();
                break;
            case BT_MAC:
                BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
                m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                value = m_BluetoothAdapter.getAddress();
                break;
            case TIME_ZONE:
                java.util.TimeZone tz = java.util.TimeZone.getDefault();
                value = tz.getDisplayName(false, TimeZone.SHORT) + tz.getID();
                break;
            case REGION:
                break;
            case ROM_TOTAL_CAPACITY:
                break;
            case RAM_TOTAL_CAPACITY:
                value = RAMInfo.getDefault().getRamSize();
                break;
            case CPU_MAX_CUR:
                value = CpuInfo.getDefault().getCpuMaxFrep();
                break;
            case NETWORK_STATUS:
                break;
            case IP_ADDS:
                value = new KiWifiManage(mCotext).getIPAddress();
                break;
            case IMEI:
                TelephonyManager telephonyManager = (TelephonyManager)
                        mCotext.getSystemService(Context.TELEPHONY_SERVICE);
                value = telephonyManager.getDeviceId();
                break;
            case NFC:
                value = String.valueOf(isNFC(mCotext));
                break;
            case RINGTONE:
                Uri uri = RingtoneManager.getActualDefaultRingtoneUri(mCotext,
                        RingtoneManager.TYPE_RINGTONE);
                value = getRealFilePath(mCotext,uri);
                break;
            case NOTIFICATION:
                uri = RingtoneManager.getActualDefaultRingtoneUri(mCotext,
                        RingtoneManager.TYPE_NOTIFICATION);
                value = getRealFilePath(mCotext,uri);
                break;
            case ALARM_ALERT:

                uri = RingtoneManager.getActualDefaultRingtoneUri(mCotext,
                        RingtoneManager.TYPE_ALARM);
                value = getRealFilePath(mCotext,uri);

                break;

            case EXTERNAL_STORAGE_DIR:
                value = TFCard.readTotalSDCard(TFCard.getExternalStorageDirectory());
                break;
            case SECONDRY_STORAGE_DIR:
                value = TFCard.readTotalSDCard(TFCard.getSecondaryStorageDirectory(mCotext));
                break;
            case AVAILABLE_EXTERNAL_STORAGE_DIR:
                value = TFCard.readAvailableSDCard(TFCard.getExternalStorageDirectory());
                break;
            case AVAILABLE_SECONDRY_STORAGE_DIR:
                value = TFCard.readAvailableSDCard(TFCard.getSecondaryStorageDirectory(mCotext));
                break;

           /* case ALL_APK_LIST:
                value = ApksControlCenter.getInstance().getAllAppInfosList(mCotext);
                break;
            case USER_APK_LIST:
                value = ApksControlCenter.getInstance().getUserAppInfosList(mCotext);
                break;
            case SYSTEM_APK_LIST:
                value = ApksControlCenter.getInstance().getSystemAppInfosList(mCotext);
                break;*/
            case UPTIME_MILLIS:
                value = new BootStatus().getUptimeMillis();
                break;
            case ELAPSED_REALTIME:
                value = new BootStatus().getElapsedRealTime();
                break;
            default:
                value = null;
                break;
        }


        Log.d(TAG, "key: " + key + " ; value: "+ value);
        return value;
    }

    private String[][] getTwoDimensionalArray(String[] array) {
        String[][] twoDimensionalArray = null;
        for (int i = 0; i < array.length; i++) {
            String[] tempArray = array[i].split(",");
            if (twoDimensionalArray == null) {
                twoDimensionalArray = new String[array.length][tempArray.length];
            }
            for (int j = 0; j < tempArray.length; j++) {
                twoDimensionalArray[i][j] = tempArray[j];
            }
        }
        return twoDimensionalArray;
    }

    /**
     *
     * @param key
     * @return 当key为APK_*_LIST时,返回对象为ArrayList(String[2]);
     */
    public Object getSystemPropInfo(String key) {
        String kValue = key;

        for (int i = 0; i < allKeysTwoDimensional.length; i++) {
            String keys[] = allKeysTwoDimensional[i];
            if (keys != null && keys.length > 0
                    && keys[keys.length - 1].equals(kValue)) {
                return updateSystemPropInfo(keys);
            }
        }
        return null;
    }

    public SystemMap getSystemMap() {
        for (int i = 0; i < allKeysTwoDimensional.length; i++) {
            if (mSystemMap.get(allKeysTwoDimensional[i][1]) == null) {
                updateSystemPropInfo(allKeysTwoDimensional[i]);
            } else {
                Log.d(TAG, "Key: " +
                        allKeysTwoDimensional[i][0] + "; value:" +
                        mSystemMap.get(allKeysTwoDimensional[i][0]));
            }
        }

        return mSystemMap;
    }

    public JSONObject getSystemPropJson() {
        isRoot();
        //HashMap<String, Object> map = new HashMap<>();
        //map.put("dataType", "DEVICE");
        //map.put("data", getSystemMap().getHashMap());

        JSONObject jsonObject = new JSONObject(getSystemMap().getHashMap());

        Log.d(TAG, "jsonObject: " + jsonObject);
        return jsonObject;
    }

    public void isRoot() {
        ChangeState mChangeState = new ChangeState();
        Log.d(TAG, "hasRooted(): " + mChangeState.hasRooted());
        Log.d(TAG, "isRoot(): " + mChangeState.isRoot());
    }

    class SystemMap {
        private String dataType;
        private HashMap<String, Object> hashMap = new HashMap<String, Object>();
        private ArrayMap<String, Object> arrayMap;

        public SystemMap(String dataType, ArrayMap<String, Object> map) {
            this.dataType = dataType;
            this.arrayMap = map;
            this.hashMap.put("dataType", dataType);
            this.hashMap.put("data", arrayMap);
        }

        public SystemMap(String dataType) {
            this.dataType = dataType;
            this.arrayMap = new ArrayMap<>();
            this.hashMap.put("dataType", dataType);
            this.hashMap.put("data", arrayMap);
        }

        public void setArrayMap(ArrayMap<String, String> map) {

        }

        public ArrayMap<String, Object> getArrayMap() {
            return arrayMap == null ? null : arrayMap;
        }

        public HashMap<String,Object> getHashMap(){
            if(hashMap != null)
                return hashMap;
            return null;
        }

        public void put(String key, Object value) {
            if (key != null && arrayMap != null)
                arrayMap.put(key, value);
        }

        public Object get(String key) {
            return arrayMap == null ? null : arrayMap.get(key);
        }


    }

    public static boolean isNFC(Context context) {
        boolean flag = false;
        if (context == null)
            return flag;
        NfcManager manager = (NfcManager) context.
                getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            flag = true;
        }
        return flag;
    }

    public static boolean isRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }


    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }

        return data.substring(data.lastIndexOf("/") + 1, data.length());
    }


}
