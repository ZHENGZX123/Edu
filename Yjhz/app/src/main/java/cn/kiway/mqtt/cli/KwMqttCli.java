package cn.kiway.mqtt.cli;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.kiway.yjhz.utils.GlobeVariable;

public class KwMqttCli implements MqttCallback {
    //public final static KwMqttCli INSTANCE = new KwMqttCli(Settings.INSTANCE.clientid());
    private static final String TAG = "kiway_KwMqttCli";
    private Context mContext;
    private MqttClient client = null;
    private MqttConnectOptions options;
    private MqttDefaultFilePersistence mDataStore;
    private String clientid;
    private String username;
    private String passwd;
    private String topicName;
    private int recode = 0;
    private String remsg = "";
    private String mDeviceId;
    private TopicProcessService loginerrcbk = new LoginErrorCbk();

    private boolean flag = false;

    private Set<String> cbkwildcard = new HashSet<String>();
    private Map<String, TopicProcessService> cbks = new HashMap<String, TopicProcessService>();


    private String host;

    private ScheduledExecutorService scheduler; //周期网络连接

    private Thread mThread;
    private Handler mHandler;
    private Runnable mRunnable;

    public KwMqttCli(Context context, String mDeviceId, Handler h, int i) {
        mContext = context;
        this.mDeviceId = mDeviceId;
        clientid = Settings.getInstance(mContext).clientid() + i;
        mDataStore = Settings.getInstance(mContext).defDataStore();
        username = Settings.getInstance(mContext).mqttUserName();
        passwd = Settings.getInstance(mContext).mqttPasswd();
        topicName = Settings.getInstance(mContext).topicName();
        mHandler = h;
    }

    public KwMqttCli(Context context, String mDeviceId, Handler h, int i, String u, String p) {
        mContext = context;
        this.mDeviceId = mDeviceId;
        clientid = Settings.getInstance(mContext).clientid() + i;
        mDataStore = Settings.getInstance(mContext).defDataStore();
        username = u;
        passwd = p;
        topicName = Settings.getInstance(mContext).topicName();
        mHandler = h;
    }

    public KwMqttCli(Context context, String mDeviceId, Handler h, String tp, int i, String u, String p,
                     TopicProcessService lcbk) {
        mContext = context;
        this.mDeviceId = mDeviceId;
        clientid = Settings.getInstance(mContext).clientid() + i;
        mDataStore = Settings.getInstance(mContext).defDataStore();
        username = u;
        passwd = p;
        this.topicName = tp;
        loginerrcbk = lcbk;
        mHandler = h;
    }

    public KwMqttCli(Context context, String mDeviceId, Handler h, String id) {
        mContext = context;
        this.mDeviceId = mDeviceId;
        clientid = id;
        mDataStore = Settings.getInstance(mContext).defDataStore();
        username = Settings.getInstance(mContext).mqttUserName();
        passwd = Settings.getInstance(mContext).mqttPasswd();
        this.topicName = Settings.getInstance(mContext).topicName();
        mHandler = h;
    }


    public String getUserName() {
        return username;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getTopicName() {
        return topicName;
    }


    public TopicProcessService getLoginerrCbk() {
        return loginerrcbk;
    }

    public String getClientid() {
        return clientid;
    }

    public int getRecode() {
        return recode;
    }

    public String getRemsg() {
        return remsg;
    }

    private void initOptions() {
        if (options == null) {
            options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(username);
            options.setPassword(passwd.toCharArray());
            options.setConnectionTimeout(Settings.getInstance(mContext).connectionTimeout());
            options.setKeepAliveInterval(Settings.getInstance(mContext).keepAliveInterval());
            options.setWill(topicName + "/willtopic/" + clientid, clientid.getBytes(), 2, false);
            Log.d(TAG, "username: " + username + " ; passwd: " + passwd);

            host = "tcp://" + Settings.getInstance(mContext).mqttHost() +
                    ":" + Settings.getInstance(mContext).mqttPort();
            Log.d(TAG, "URL: " + host);
        }
    }

    private void initMqttClient() {
        Log.d(TAG, "initMqttClient");
        try {
            if (client == null)
                client = new MqttClient(host, clientid, mDataStore) {
                    @Override
                    public void unsubscribe(String[] topic) throws MqttException {
                        super.aClient.unsubscribe(topic, null, null).waitForCompletion(10);
                    }
                };
            client.setCallback(KwMqttCli.this);
        } catch (MqttException e) {
            e.printStackTrace();
            recode = e.getReasonCode();
            remsg = e.getMessage();
            loginerrcbk.process(remsg, null, "" + recode);
        }

    }


    /**
     * 开始连接，线程中处理，需要同步
     * 且为耗时处理,只能在线程中处理.
     *
     * @param
     */
    public void start() {
        Log.d(TAG, "start,isConnected(): " + isConnected());

        if (isConnected())
            return;

        initOptions();

        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();

            try {
                scheduler.scheduleAtFixedRate(
//                        new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!isConnected()) {
//                            //startReconnect(topicName);
//
//                        }
//                    }
//                }

                        getRunnable(topicName)
                        , 0 * 1000, 5 * 1000, TimeUnit.MILLISECONDS);
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
                if (scheduler != null)
                    scheduler.shutdown();
                start();
            }

        }
    }


    private void startReconnect(final String tpcName) {
        Log.d(TAG, "startReconnect");

        if (host == null)
            host = "tcp://" + Settings.getInstance(mContext).mqttHost() +
                    ":" + Settings.getInstance(mContext).mqttPort();
        initMqttClient();

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized (client) {
                    Log.d(TAG, "startReconnect,isConnected() = " + isConnected());
                    if (!isConnected()) {
                        try {
                            client.connect(options);
                            client.subscribe(clientid + "/+/#", 2);
                            client.subscribe(tpcName + "/" + username + "/#");
                            Log.d(TAG, "startReconnect,connect:" + isConnected());
                            Message msg = new Message();
                            if (isConnected()) {
                                msg.what = GlobeVariable.MQTT_CONNECTION_SUCC;
                                mHandler.sendMessage(msg);
                            } else {
                                msg.what = GlobeVariable.MQTT_CONNECTION_FAIL;
                                mHandler.sendMessage(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "startReconnect,Exception：" + e.getMessage());
                            mHandler.sendEmptyMessage(GlobeVariable.MQTT_CONNECTION_FAIL);
                        }
                    }
                }
            }
        });

        mThread.start();
    }

    public Runnable getRunnable(final String tpcName) {
        if (host == null)
            host = "tcp://" + Settings.getInstance(mContext).mqttHost() +
                    ":" + Settings.getInstance(mContext).mqttPort();

        initMqttClient();

        if (mRunnable == null)
            mRunnable = new Runnable() {
                @Override
                public void run() {

                    synchronized (client) {

                        if (!isConnected()) {
                            Log.d(TAG, "startReconnect,isConnected() = " + isConnected());
                            try {
                                client.connect(options);
                                client.subscribe(clientid + "/+/#", 2);
                                client.subscribe(tpcName + "/" + username + "/#");
                                Log.d(TAG, "startReconnect,connect:" + isConnected());
                                Message msg = new Message();
                                if (isConnected()) {
                                    msg.what = GlobeVariable.MQTT_CONNECTION_SUCC;
                                    mHandler.sendMessage(msg);
                                } else {
                                    msg.what = GlobeVariable.MQTT_CONNECTION_FAIL;
                                    mHandler.sendMessage(msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "startReconnect,Exception：" + e.getMessage());
                                mHandler.sendEmptyMessage(GlobeVariable.MQTT_CONNECTION_FAIL);
                            }
                        }
                    }
                }
            };

        Log.d(TAG ,"mqttRunnable hasCode: " + mRunnable.hashCode());
        return mRunnable;
    }


    /**
     * 断开连接
     */
    public void stop() {
        Log.e(TAG, "试图断开连接stop()");
        if (!isConnected()) {
            Log.e(TAG, "试图停止推送服务器但是推送服务并没有运行或连接");
            return;
        }

        try {
            client.disconnect();
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        scheduler.shutdownNow();
    }


    /**
     * 强制重连
     */
    public synchronized void reconnectIfNecessary() {
        if (isConnected()) {
            try {
                client.disconnect();
                client = null;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        start();

    }

    public boolean isConnected() {
        return client == null ? false : client.isConnected();
    }


    public void publish(String topic, byte[] data) {
        try {
            if (isConnected()) client.publish(topic, data, 2, false);
        } catch (MqttException e) {

        }
    }


    public void unsubscribe(String topicName) {
        try {
            if (isConnected()) {
                client.unsubscribe(topicName);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    public void subscribe(final String topicName) {
        if (topicName == null) {
            Log.d(TAG, "subscribe,  topicName = null:" + topicName);
            return;
        }

        try {
            if (isConnected())
                client.subscribe(topicName, 2);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void subscribe(final String topicName, TopicProcessService tps) {
        if (topicName == null && tps == null) {
            Log.d(TAG, "subscribe, topicName = null:  " + (topicName == null));
            Log.d(TAG, "subscribe, tps = null:" + (tps == null));
            return;
        }
        try {
            if (isConnected()) {
                client.subscribe(topicName, 2);

                if ((topicName.indexOf("#") > 0)
                        || (topicName.indexOf("+") > 0)) cbkwildcard.add(topicName);
                cbks.put(topicName, tps);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


  /*  private boolean connect() {
        while ( client != null && !client.isConnected() && (recode != 4)) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            start();
        }
        return client.isConnected();
    }*/

    public void close() {
        try {
            if (isConnected()) client.disconnect();
        } catch (MqttException e) {

        }
    }

    public boolean isFlag() {
        return flag;
    }

    /**
     * 网络断开连接后的回调
     *
     * @param msg
     */
    @Override
    public void connectionLost(Throwable msg) {
        Log.d(TAG, "connectionLost: " + msg.getMessage());
        synchronized (client) {
            mHandler.sendEmptyMessage(GlobeVariable.MQTT_CONNECTION_FAIL);
        }
        start();
    }

    /**
     * publish发送消息后回调
     *
     * @param imtk
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken imtk) {
        switch (imtk.getResponse().getType()) {
            case MqttWireMessage.MESSAGE_TYPE_CONNACK:
                System.out.println();
                Log.d(TAG, "" + imtk.getMessageId() + "MESSAGE_TYPE_CONNACK");
                break;
            case MqttWireMessage.MESSAGE_TYPE_CONNECT:
                Log.d(TAG, "" + imtk.getMessageId() + "MESSAGE_TYPE_CONNECT");
                break;
            case MqttWireMessage.MESSAGE_TYPE_PUBACK:
                Log.d(TAG, "" + imtk.getMessageId() + "MESSAGE_TYPE_PUBACK");
                break;
            case MqttWireMessage.MESSAGE_TYPE_PUBCOMP:
                Log.d(TAG, "" + imtk.getMessageId() + "MESSAGE_TYPE_PUBCOMP");
                break;
            case MqttWireMessage.MESSAGE_TYPE_PUBREC:
                Log.d(TAG, "" + imtk.getMessageId() + "MESSAGE_TYPE_PUBREC");
                break;
            default:
                Log.d(TAG, "" + imtk.getMessageId() + "    " + imtk.getResponse().getType());
        }

    }

    /**
     * subscribe订阅后的回调
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        String time = new Timestamp(System.currentTimeMillis()).toString();

        Log.d(TAG, "topic: " + topic + " message_ID: " +
                message.hashCode() + " ; " + message.getPayload().toString() + " time: " + time);

        boolean nofind = false;
        if (cbks.containsKey(topic)) {
            TopicProcessService tps = cbks.get(topic);
            tps.process(topic, message, time);

        } else {
            nofind = true;
            for (String topicfilter : cbkwildcard) {
                if (TopicMatcher.match(topicfilter, topic) && cbks.containsKey(topicfilter)) {
                    TopicProcessService tps = cbks.get(topicfilter);
                    tps.process(topic, message, time);
                    nofind = false;
                }
            }
        }

        Message msg = new Message();
        if (nofind) {
            RegTopicProcSrv.INSTANCE.process(topic, message, time);
            msg.what = GlobeVariable.MQTT_SUBSCRIBE;
            msg.arg1 = message.hashCode();
            msg.obj = topic;
        } else {

        }

        mHandler.sendMessage(msg);
    }


}
