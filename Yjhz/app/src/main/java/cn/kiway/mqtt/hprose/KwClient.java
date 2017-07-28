package cn.kiway.mqtt.hprose;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.kiway.mqtt.cli.ConnectionServiceCbk;
import cn.kiway.mqtt.cli.KwMqttCli;
import cn.kiway.mqtt.cli.RegTopicProcSrv;
import cn.kiway.mqtt.cli.TopicProcessService;
import cn.kiway.yjhz.json.KiwayJson;
import cn.kiway.yjhz.utils.GlobeVariable;

/**
 * Created by arvin on 2017/2/23 0023.
 */


interface IGetMessage {
    public Message<Set<User>> getMessage(User u);
}

interface IGetMessage2 {
    public Message<Set<User>> getMessage();

    public String getName();
}

interface MessageHandler {
    public String sendMessage(String message);

    public String sendRemoteCallback(int remoteid, String result);

    public String sendRemoteCallback(int remoteid, Object object);
}

public class KwClient {

    private static final String TAG = "kiway_KwClient";
    private Context mContext;
    private KwMqttCli kwMqttCli;
    private String topicName;
    private String userName;
    private String password;
    private String appID = "bc803233a7bba636ac50fe774c01d6cd";

    private ConnectionServiceCbk mConnectionServiceCbk = null;
    private MessageHandler messageHandler = null;
    private HproseClient mHproseClient;

    private ExecutorService sendMessageThreadService;
    private ScheduledExecutorService sendMessageThreadMonitor;
    private Runnable messageMonitorRunn;


    //用于储存消息的集合
    //private ConcurrentHashMap<String,String> messageMap;
    private SparseArray<String> messageMap;

    public KwClient(Context context, String mDeviceId) {
        super();
        mContext = context;
        kwMqttCli = new KwMqttCli(mContext, mDeviceId, mHandler, 100000000 + new Random().nextInt(100000000));
        topicName = "mqttRpcs";
        initMessageBind();
    }

    public KwClient(Context context, String mDeviceId, String topicname) {
        super();
        mContext = context;
        kwMqttCli = new KwMqttCli(mContext, mDeviceId, mHandler, 100000000 + new Random().nextInt(100000000));
        topicName = topicname;
        initMessageBind();
    }

    public KwClient(Context context, String mDeviceId, String topicname, String username,
                    String passwd) {
        super();
        mContext = context;
        kwMqttCli = new KwMqttCli(mContext, mDeviceId, mHandler, 100000000 + new Random().nextInt(100000000)
                , username, passwd);
        topicName = topicname;
        initMessageBind();
    }

    public KwClient(Context context, String mDeviceId, String tp, String username,
                    String passwd, ConnectionServiceCbk ccbk) {
        super();
        mContext = context;
        userName = username;
        password = passwd;
        this.topicName = tp;
        kwMqttCli = new KwMqttCli(mContext, mDeviceId, mHandler, topicName, 100000000 + new Random()
                .nextInt(100000000), username, passwd, loginTopProServiceImp);
        mConnectionServiceCbk = ccbk;
        initMessageBind();
        start();
    }

    public void initMessageBind() {
        this.registerNotice(noticeProServiceImp);
        this.register("data", dateTopProServiceImp);
        this.register("message", messageTopProServiceImp);
        this.register("recallMessage", recallMessageTopProServiceImp);
    }


    TopicProcessService loginTopProServiceImp = new TopicProcessService() {
        @Override
        public void process(String topic, MqttMessage message, String time) {
            Log.d(TAG, "initialization mqttclient the end");
            Log.d(TAG, "topic: " + topic);
            Log.d(TAG, "time: " + time);

        }
    };

    TopicProcessService noticeProServiceImp = new TopicProcessService() {
        @Override
        public void process(String topic, MqttMessage message, String time) {
            Log.d(TAG, "this is a notice----" + new String(message.getPayload()));
        }
    };

    TopicProcessService dateTopProServiceImp = new TopicProcessService() {
        @Override
        public void process(String topic, MqttMessage message, String time) {
            Log.d(TAG, "this is a date----" + new String(message.getPayload()));
        }
    };

    TopicProcessService messageTopProServiceImp = new TopicProcessService() {
        @Override
        public void process(String topic, MqttMessage message, String time) {
            String msgString = new String(message.getPayload());
            Log.d(TAG, "this is a message----" + msgString);


            try {
                JSONArray jsonArray = new JSONArray(msgString);
                Log.d(TAG, "jsonArray----" + jsonArray);

                for(int i =0 ; i < jsonArray.length() ; i ++){
                   String s = (String) jsonArray.get(i);
                    Log.d(TAG, "s");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    TopicProcessService recallMessageTopProServiceImp = new TopicProcessService() {
        @Override
        public void process(String topic, MqttMessage message, String time) {
            Log.d(TAG, "this is a recallMessage----" + new String(message.getPayload()));
        }
    };

    public boolean isConnected() {
        return kwMqttCli == null ? false : kwMqttCli.isConnected();
    }

    public String getUserName() {
        return kwMqttCli == null ? null : kwMqttCli.getUserName();
    }

    public String getPasswd() {
        return kwMqttCli == null ? null : kwMqttCli.getPasswd();
    }

    public String getTopicName() {
        return kwMqttCli == null ? null : kwMqttCli.getTopicName();
    }


    public TopicProcessService getLoginerrCbk() {
        return kwMqttCli.getLoginerrCbk();
    }

    public String getClientid() {
        String id = null;
        try {
            id = kwMqttCli.getClientid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public int getRecode() {
        return this.getRecode();
    }

    public String getRemsg() {
        return this.getRemsg();
    }

    public <L> L useService(Class<L> tClass) {
        return mHproseClient == null ? null : (L) mHproseClient.useService((Class) tClass);
    }

    public void unSubscribe(String topName) {
        if (isConnected())
            getMqttClient().unsubscribe(topName);
    }

    public void subscribe(String topName) {
        if (isConnected())
            getMqttClient().subscribe(topName);
    }

    public void subscribe(String topName, TopicProcessService tps) {
        if (isConnected())
            getMqttClient().subscribe(topName, tps);
    }

    public void publish(String topic, byte[] data) {
        if (isConnected())
            getMqttClient().publish(topic, data);
    }


    private KwMqttCli getMqttClient() {
        return kwMqttCli;
    }


    public void registerNotice(TopicProcessService cbk) {
        RegTopicProcSrv.INSTANCE.subscribe(topicName + "/" +
                kwMqttCli.getUserName() + "/notice", cbk);
    }

    public void register(String tag, TopicProcessService cbk) {
        RegTopicProcSrv.INSTANCE.subscribe(topicName + "/" +
                kwMqttCli.getUserName() + "/" + tag, cbk);
    }


    /**
     * 开始连接,如需要
     * @throws
     */
    public void start() {
        if (kwMqttCli != null) kwMqttCli.start();
    }

    /**
     * 主动断开连接
     * @throws
     */
    public synchronized void stop() {
        Log.d(TAG, "stop()");
        if (kwMqttCli != null) kwMqttCli.stop();
        if (mHproseClient != null) mHproseClient.stop();

        if (sendMessageThreadService != null && !sendMessageThreadService.isShutdown()) {
            sendMessageThreadService.shutdownNow();
            sendMessageThreadService = null;
        }

        if (sendMessageThreadMonitor != null && !sendMessageThreadMonitor.isShutdown()) {
            sendMessageThreadService.shutdown();
            sendMessageThreadService = null;
        }
    }

    /**
     * 强制重连
     * @param
     */
    public synchronized void reconnectIfNecessary() {

        if (kwMqttCli != null)
            kwMqttCli.reconnectIfNecessary();
    }

    public void initHproseClient() {
        Log.d(TAG, "initHproseClient == null: " + (mHproseClient == null));

        if (mHproseClient == null)
            mHproseClient = new HproseClient(
                    mContext, appID, mHandler, KwClient.this);

        mHandler.sendEmptyMessage(GlobeVariable.HPROSE_INIT_SUCC);

    }


    public MessageHandler getMessageHandler() {
        return messageHandler;
    }


    private Runnable getMessageMonitorRunn(final SparseArray<String> sparseArray) {

        if (messageMonitorRunn == null)
            messageMonitorRunn = new Runnable() {
                @Override
                public void run() {
                    synchronized (messageMap) {
                        int length = sparseArray.size();
                        Log.d(TAG, "messageMap.size: " + length);
                        for (int i = 0; i < length; i++) {
                            String mesString = sparseArray.valueAt(i);
                            if (mesString != null && !mesString.equals("")) {

                                android.os.Message msg = new android.os.Message();
                                msg.what = GlobeVariable.MQTT_SEND_MESSAGE;
                                msg.obj = mesString;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                }
            };

        Log.d(TAG, "getMessageMonitorRunn,hasCode: " + messageMonitorRunn.hashCode());

        return messageMonitorRunn;
    }

    /**
     * 接收到需要发送的消息,将其收录到集合中,发送成功后删除
     * @param str
     */
    public void sendMessage(String str) {
        addMessage(str);

        if (sendMessageThreadMonitor == null)
            sendMessageThreadMonitor = Executors.newSingleThreadScheduledExecutor();

        sendMessageThreadMonitor.scheduleAtFixedRate(
                getMessageMonitorRunn(messageMap),
                0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS
        );


    }

    private void addMessage(String str) {
        if (messageMap == null)
            messageMap = new SparseArray<String>();

        if (messageMap.indexOfValue(str) > -1) {
            messageMap.put(messageMap.indexOfValue(str), str);
        } else {
            messageMap.put(messageMap.size(), str);
        }

    }

    private void deleteMesage(String str) {
        if (messageMap != null && messageMap.size() > 0) {
            int i = messageMap.indexOfValue(str);
            if (i > -1) {
                messageMap.delete(i);
                Log.d(TAG, "集合中删除消息");
                if (messageMap.size() == 0) {
                    if (!sendMessageThreadMonitor.isShutdown()) {
                        //当消息集合中没有消息时,则执行完线程后清空线程池.
                        sendMessageThreadMonitor.shutdown();
                        sendMessageThreadMonitor = null;
                    }
                }
            }
        }
    }


    private void sendMessageRunnable(final String str, final Handler handler) {

        //此处限定为单线程池
        //当有多条消息需要发送时
        //亦是排队处理,确保发送失败在removeMessages
        // -计时handler时不会相互干扰.
        if (sendMessageThreadService == null)
            sendMessageThreadService = Executors.newSingleThreadExecutor();
        //sendMessageThreadService =  Executors.newFixedThreadPool(2);

        try {
            sendMessageThreadService.submit(new Runnable() {
                @Override
                public void run() {
                    try {

                        String string = null;
                        if (messageHandler != null) {
                            Log.d(TAG, "Send Message：" + str);
                            string = messageHandler.sendMessage(str);
                        } else {
                            //messageHandler为空时,未连接到服务器
                            //需要重新连接
                            Log.d(TAG, "messageHandler == null,重新连接");
                            return;
                        }

                        if (string != null) {
                            Log.d(TAG, "发送状态：" + string);
                            android.os.Message msg = new android.os.Message();
                            msg.what = GlobeVariable.MQTT_SEND_MESSAGE_SUCC;
                            msg.obj = str;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "发送失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            Log.d(TAG, "发送消息线程起动失败: " + e.toString());
        }


    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            String str;
            switch (msg.what) {
                case GlobeVariable.MQTT_SUBSCRIBE:
                    break;
                case GlobeVariable.MQTT_CONNECTION_SUCC:
                    initHproseClient();
                    break;
                case GlobeVariable.MQTT_CONNECTION_FAIL:
                    Log.d(TAG, "MQTT,,连接失败，系统正在重连");
                    break;

                case GlobeVariable.MQTT_CONNECTION_STOP:

                    break;
                case GlobeVariable.HPROSE_INIT_SUCC:
                    Log.d(TAG, "获取Token");
                    Log.d(TAG, "connect: mHproseClient = null: " + (mHproseClient == null)
                            + " ; messageHandler = null: " + (messageHandler == null));
                    if (mHproseClient != null) {
                        mHproseClient.initToken();

                    }
                    break;
                case GlobeVariable.MQTT_SEND_MESSAGE:
                    str = (String) msg.obj;
                    if (str != null && !str.equals("")) {
                        mHandler.removeMessages(GlobeVariable.MQTT_SEND_MESSAGE);
                        KwClient.this.sendMessageRunnable(str, mHandler);
                    }

                    break;
                case GlobeVariable.MQTT_SEND_MESSAGE_SUCC:
                    str = (String) msg.obj;
                    Log.d(TAG, "发送消息状态成功：" + str);
                    deleteMesage(str);
                    break;

                case GlobeVariable.HPROSE_REGISTER_TOKER:
                    break;
                case GlobeVariable.HPROSE_REGISTER_TOKER_REGIST:
                    Log.d(TAG, "新注册：HPROSE_REGISTER_TOKER_REGIST");
                    if (mHproseClient != null)
                        mHproseClient.regishToken(userName, password);

                    break;
                case GlobeVariable.HPROSE_REGISTER_TOKER_SUCC:
                    Log.d(TAG, "注册成功：HPROSE_REGISTER_TOKER_SUCC");
                    if (messageHandler == null || mHproseClient != null)
                        messageHandler = mHproseClient.initMessageHandler(MessageHandler.class);
                    break;

                case GlobeVariable.HPROSE_REGISTER_TOKER_ERROR:
                    Log.d(TAG, "注册失败：HPROSE_REGISTER_TOKER_ERROR");
                    mHandler.sendEmptyMessageDelayed(GlobeVariable.HPROSE_INIT_SUCC, 5000);
                    break;
                case GlobeVariable.GET_LIST_INSTALLED_APKS:
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

                default:
                    break;
            }

            //
            if (mConnectionServiceCbk != null) {
                Object obj = mConnectionServiceCbk.process(msg.what);
                if (obj != null && !obj.equals(""))
                    messageHandler.sendRemoteCallback(0, obj);
            }
        }
    };


}
