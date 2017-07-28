package cn.kiway.mqtt.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import cn.kiway.mqtt.util.SystemProp;
import cn.kiway.mqtt.cli.ConnectionServiceCbk;
import cn.kiway.mqtt.hprose.KwClient;
import cn.kiway.remote.control.NetworkControlCenter;
import cn.kiway.yjhz.utils.ACache;
import cn.kiway.yjhz.utils.GlobeVariable;


/**
 * 与Activity交互
 */
interface ConnServiceMessage {
    public void sendMessage(String mes);
}


public class ClientService extends Service {


    private static final String TAG = "kiway_ClientService";
    private final Context mContext = ClientService.this;
    private final IBinder binder = new ClientBinder();
    private KwClient mKwClient = null;
    private ConnectivityManager mConnectivityManager; //网络改变接收器

    public class ClientBinder extends Binder implements ConnServiceMessage {

        public ClientService getService() {
            Log.d(TAG, "Client getService()");
            return ClientService.this;
        }

        @Override
        public void sendMessage(String mes) {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        init();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        stop();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        String what;
        if (intent == null || (what = intent.getAction()) == null)
            return super.onStartCommand(intent, flags, startId);

        switch (what) {
            case GlobeVariable.ACTION_START_CONN:
                start();
                break;
            case GlobeVariable.ACTION_STOP_CONN:
                //stop();
                break;
            case GlobeVariable.ACTION_RECONNECT_CONN:
                if (isNetworkAvailable()) {
                    mKwClient.start();
                }
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    //回调接口.
    ConnectionServiceCbk connectionServiceCbk = new ConnectionServiceCbk() {
        @Override
        public Object process(int status) {
            Object value = null;
            switch (status) {
                case GlobeVariable.MQTT_SUBSCRIBE:
                    break;
                case GlobeVariable.MQTT_CONNECTION_SUCC:
                    break;
                case GlobeVariable.MQTT_CONNECTION_FAIL:
                    break;
                case GlobeVariable.MQTT_CONNECTION_STOP:
                    break;
                case GlobeVariable.HPROSE_INIT_SUCC:
                    break;
                case GlobeVariable.HPROSE_REGISTER_TOKER_SUCC:
                    sendMessage(new SystemProp(ClientService.this).getSystemPropJson());
                    sendMessage(ACache.get(mContext).
                           getAsJSONObject(GlobeVariable.statisticsModel));
                    break;
                case GlobeVariable.GET_LIST_INSTALLED_APKS:
                    value = NetworkControlCenter.getInstance().getAllApks(mContext);
                    break;
                case GlobeVariable.GET_LIST_WHITE_APKS:
                    break;
                case GlobeVariable.GET_LIST_BLACK_APKS:
                    break;
                case GlobeVariable.GET_LIST_WHITE_WIFIS:
                    break;
                case GlobeVariable.GET_LIST_BLACK_WIFIS:
                    break;
                case GlobeVariable.UPDATE_LIST_INSTALLED_APKS:
                    break;
                case GlobeVariable.UPDATE_LIST_WHITE_APKS:
                    break;
                case GlobeVariable.UPDATE_LIST_BLACK_APKS:
                    break;
                case GlobeVariable.UPDATE_LIST_WHITE_WIFIS:
                    break;
                case GlobeVariable.UPDATE_LIST_BLACK_WIFIS:
                    break;
                case GlobeVariable.DELETE_LIST_INSTALLED_APKS:
                    break;
                case GlobeVariable.DELETE_LIST_WHITE_APKS:
                    break;
                case GlobeVariable.DELETE_LIST_BLACK_APKS:
                    break;
                case GlobeVariable.DELETE_LIST_WHITE_WIFIS:
                    break;
                case GlobeVariable.DELETE_LIST_BLACK_WIFIS:
                    break;
                case GlobeVariable.REMOTE_INSTALL_APK:
                    break;
                case GlobeVariable.REMOTE_UNINSTALL_APK:
                    break;
                case GlobeVariable.REMOTE_DOWNLOAD_FILES:
                    break;
                case GlobeVariable.REMOTE_DELETE_FILES:
                    break;
                case GlobeVariable.REMOTE_REMOVE_PASSWD:
                    NetworkControlCenter.getInstance().restartPasswd();
                    break;
                case GlobeVariable.REMOTE_GET_LOCATION:
                    value = NetworkControlCenter.getInstance().getLocationInfo(mContext);
                    break;
                case GlobeVariable.REMOTE_RESTART_FACTORY:
                    NetworkControlCenter.getInstance().restartPasswd();
                    break;
            }
            return value;
        }
    };

    /*断开绑定时执行*/
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    private void init() {
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }

    /**
     * 尝试启动"推送服务器，并注册网络改变接收器
     */
    public synchronized void start() {

        connect();
        registerReceiver(mConnectivityReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * 连接服务
     */
    private void connect() {
        Log.e(TAG, "connect ");
        String mDeviceId = (String) SystemProp.getInstance(this)
                .getSystemPropInfo(SystemProp.ANDROID_ID);
        String passwd = this.getPackageName();
        String userName = this.getPackageName() + mDeviceId;
        String topcName = "xtkt666";

        if (mKwClient == null) {
            mKwClient = new KwClient(
                    mContext,
                    mDeviceId,
                    topcName,
                    userName,
                    passwd,
                    connectionServiceCbk);
        }
    }


    /**
     * 停止连接服务
     */
    private synchronized void stop() {
        mKwClient.stop();
        unregisterReceiver(mConnectivityReceiver);
    }

    /**
     * 网络状态发生变化接收器
     */
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                //mKwClient.start();
            } else {
                //mKwClient.stop();
            }
        }
    };

    /**
     * 通过ConnectivityManager查询网络连接状态
     *
     * @return 如果网络状态正常则返回true反之flase
     */
    private boolean isNetworkAvailable() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        return (info == null) ? false : info.isConnected() && info.isAvailable();
    }

    public void sendMessage(JSONObject jsonObject) {
        sendMessage(jsonObject.toString());
    }

    /**
     * 发送消息
     *
     * @param
     */
    public void sendMessage(String str) {
        Log.d(TAG, "sendMessage");
        if (mKwClient != null) {
            mKwClient.sendMessage(str);
        }
    }
}
