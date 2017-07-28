package cn.kiway.mqtt.hprose;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import cn.kiway.mqtt.cli.TopicProcessService;
import cn.kiway.mqtt.util.SystemProp;
import cn.kiway.yjhz.utils.GlobeVariable;
import hprose.client.ClientContext;
import hprose.common.HproseContext;
import hprose.common.HproseFilter;
import hprose.common.InvokeSettings;
import hprose.io.ByteBufferStream;
import hprose.io.HproseMode;
import hprose.util.concurrent.Promise;

interface ILoginHprose {
    public String regist(String appId, String imei, String type,
                         String mac, String osName, String osVersion,
                         String appName, String appVersion);

    public String addsessionuser(String appid, String user, String passwd, String clientid);
}

public class HproseClient extends hprose.client.HproseClient {

    private static final String TAG = "kiway_HproseClient";
    private static HproseClient mInstance = null;
    private Context mContext;
    private Handler mHandler;
    private String appID;
    private ILoginHprose mILoginHprose;
    private ExecutorService mExecutorService;


    static class ClientSessionFilter implements HproseFilter {
        private final IdentityHashMap<hprose.client.HproseClient, Integer> sidMap = new IdentityHashMap<hprose.client.HproseClient, Integer>();

        @Override
        public ByteBuffer inputFilter(ByteBuffer istream, HproseContext context) {
            hprose.client.HproseClient client = ((ClientContext) context).getClient();
            int len = istream.limit() - 7;
            if (len > 0 &&
                    istream.get() == 's' &&
                    istream.get() == 'i' &&
                    istream.get() == 'd') {
                int sid = ((int) istream.get()) << 24 |
                        ((int) istream.get()) << 16 |
                        ((int) istream.get()) << 8 |
                        (int) istream.get();
                sidMap.put(client, sid);
                return istream.slice();
            }
            istream.rewind();
            return istream;
        }

        @Override
        public ByteBuffer outputFilter(ByteBuffer ostream, HproseContext context) {
            hprose.client.HproseClient client = ((ClientContext) context).getClient();
            if (sidMap.containsKey(client)) {
                int sid = sidMap.get(client);
                ByteBuffer buf = ByteBufferStream.allocate(ostream.remaining() + 7);
                buf.put((byte) 's');
                buf.put((byte) 'i');
                buf.put((byte) 'd');
                buf.put((byte) (sid >> 24 & 0xff));
                buf.put((byte) (sid >> 16 & 0xff));
                buf.put((byte) (sid >> 8 & 0xff));
                buf.put((byte) (sid & 0xff));
                buf.put(ostream);
                ByteBufferStream.free(ostream);
                return buf;
            }
            return ostream;
        }
    }


    final class Request {
        public final ByteBuffer buffer;
        public final Promise<ByteBuffer> result = new Promise<ByteBuffer>();
        public final int timeout;

        public Request(ByteBuffer buffer, int timeout) {
            this.buffer = buffer;
            this.timeout = timeout;
        }
    }


    KwClient client;
    private final static AtomicInteger nextId = new AtomicInteger(0);
    //private String topicName;
    private String token = "";

    public HproseClient(HproseMode mode) {
        super(mode);
        this.setFilter(new ClientSessionFilter());
    }

    public HproseClient(Context context, String appid, Handler handler, KwClient client) {
        super();
        this.mContext = context;
        this.client = client;
        this.appID = appid;
        mHandler = handler;
        this.setFilter(new ClientSessionFilter());
    }

    public void initToken() {
        if (mExecutorService == null)
            mExecutorService = Executors.newFixedThreadPool(5);

        if (mILoginHprose == null)
            mILoginHprose = this.useService(ILoginHprose.class);


        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                if (mILoginHprose != null) {

                    Log.d(TAG, "initToken_run");

                    token = mILoginHprose.addsessionuser(appID,
                            client.getUserName(),
                            client.getPasswd(),
                            client.getClientid());

                    Log.d(TAG, "initToken_token: " + token);

                    if (token.equals("error")) {
                        mHandler.sendEmptyMessage(GlobeVariable.HPROSE_REGISTER_TOKER_ERROR);
                    } else if (token.equals("regist")) {
                        mHandler.sendEmptyMessage(GlobeVariable.HPROSE_REGISTER_TOKER_REGIST);
                    } else if (token.equals("success")) {
                        mHandler.sendEmptyMessage(GlobeVariable.HPROSE_REGISTER_TOKER_SUCC);
                    }
                }
            }
        });
    }

    public void regishToken(final String userName, final String password) {
        Log.d(TAG, "regishToken");
        if (mExecutorService == null)
            mExecutorService = Executors.newFixedThreadPool(5);

        if (mILoginHprose == null)
            mILoginHprose = this.useService(ILoginHprose.class);


        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "regishToken, run");
                String wifiMac = (String) SystemProp.getInstance(mContext).getSystemPropInfo(
                        SystemProp.WIFI_MAC);
                String androidVersion = (String) SystemProp.getInstance(mContext).getSystemPropInfo(
                        SystemProp.ANDROID_VERSION);
                String apkName = (String) SystemProp.getInstance(mContext).getSystemPropInfo(
                        SystemProp.APK_NAME);
                String apkVersion = (String) SystemProp.getInstance(mContext).getSystemPropInfo(
                        SystemProp.APK_VERSION);

                token = mILoginHprose.regist(appID, userName, password, wifiMac,
                        "Android", androidVersion, apkName, apkVersion);
                Log.d(TAG, "regishToken, token:" + token);
                if (token.equals("error")) {
                    mHandler.sendEmptyMessage(GlobeVariable.HPROSE_REGISTER_TOKER_ERROR);
                } else if (token.equals("regist")) {
                    mHandler.sendEmptyMessage(GlobeVariable.HPROSE_REGISTER_TOKER_REGIST);
                } else if (token.equals("success")) {
                    mHandler.sendEmptyMessage(GlobeVariable.HPROSE_REGISTER_TOKER_SUCC);
                }
            }
        });


    }


    public <T> T initMessageHandler(Class<T> cls) {

        return this.useService(cls);
    }

    public String getToken() {
        return this.token;
    }

    @Override
    public final void close() {
        super.close();
    }

    @Override
    protected Promise<ByteBuffer> sendAndReceive(ByteBuffer buffer,
                                                 ClientContext context) {
        final InvokeSettings settings = context.getSettings();
        final int id = nextId.incrementAndGet() & 0x7fffffff;
        int timeout = settings.getTimeout();

        Log.d(TAG, "sendAndReceive, NO.= " + client.getClientid() + id +
                " isConnected(): " + client.isConnected());
        final Request request = new Request(buffer, timeout);

        byte[] buf = new byte[buffer.remaining()];
        buffer.get(buf);

        String mTopicName = client.getTopicName();
        String clientID = client.getClientid();

       /* try {
            if (client.isConnected()) {
                final String str = mTopicName + "/mqttcbk/" +
                        clientID + id;
                Log.d(TAG, "sendAndReceive,str: " + str);

                client.subscribe(str,
                        new TopicProcessService() {
                            @Override
                            public void process(String topic, MqttMessage message,
                                                String time) {

                                Log.d(TAG, "**sendAndReceive, process,topic="
                                        + topic + " ; time:" + time);

                                request.result.resolve(ByteBuffer.wrap(
                                        message.getPayload()));

                               // client.unSubscribe(str);
                            }
                        });


                String str1 = mTopicName + "/mqttrpc/" + clientID + id;
                Log.d(TAG, "#sendAndReceive,str: " + str);
                client.publish(str1, buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "sendAndReceive,str: " + e.getMessage());
        }*/


        try {
            subscribeRun(mTopicName, clientID, request, id, buf);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }


        /*Timer timer = new Timer(new Runnable() {
            public void run() {
            }
        });
        timer.setTimeout(timeout);*/

        return request.result;
    }

    private void subscribeRun(final String tpName, final String cID,
                              final Request request, final int id, final byte[] buf)
            throws RejectedExecutionException {

        if (mExecutorService == null)
            mExecutorService = Executors.newFixedThreadPool(3);

        mExecutorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        if (client.isConnected()) {
                            final String str = tpName + "/mqttcbk/" +
                                    cID + id;
                            Log.d(TAG, "sendAndReceive,str: " + str);
                            client.subscribe(str,
                                    new TopicProcessService() {
                                        @Override
                                        public void process(String topic, MqttMessage message,
                                                            String time) {

                                            Log.d(TAG, "**sendAndReceive, process,topic="
                                                    + topic + " ; time:" + time);

                                            request.result.resolve(ByteBuffer.wrap(
                                                    message.getPayload()));
                                            client.unSubscribe(str);
                                        }
                                    });
                        }
                    }
                }
        );

        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                if (client.isConnected()) {
                    String str = tpName + "/mqttrpc/" + cID + id;
                    Log.d(TAG, "sendAndReceive,str: " + str);
                    client.publish(str, buf);
                }
            }
        });

    }

    public void stop() {
        mExecutorService.shutdownNow();
    }


}
