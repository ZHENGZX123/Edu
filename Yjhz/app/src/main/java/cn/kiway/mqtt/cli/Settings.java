package cn.kiway.mqtt.cli;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.IOException;
import java.util.Properties;


public class Settings extends Properties {
    private static final String TAG = "kiway_Settings";
    private final static String CONFIG_NAME = "mqtttopic.properties";
    private Context mContext;
    private static Settings instance;
    private static Properties properties = new Properties();
    private static MqttDefaultFilePersistence defDataStore;

    private Settings(Context context) {
        mContext = context;
        try {
            properties.load(context.getAssets().open(CONFIG_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Settings getInstance(Context context) {
        return instance == null ? instance = new Settings(context) : instance;
    }

    public String mqttHost() {
        String ret = properties.getProperty("mqttserver.tcp.host", "localhost");
        return ret;
    }

    public Integer mqttPort() {
        return tryParse(properties.getProperty("mqttserver.tcp.port", "1883"));
    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String clientid() {
        return properties.getProperty("mqttclient.connect.id", "KiwayIMcenter001");
    }

    public MqttDefaultFilePersistence defDataStore() {
        return defDataStore == null ? defDataStore = new MqttDefaultFilePersistence(
                mContext.getCacheDir().getAbsolutePath()) : defDataStore;
    }

    public String mqttUserName() {
        return properties.getProperty("mqttclient.connect.username", "admin");
    }

    public String mqttPasswd() {
        return properties.getProperty("mqttclient.connect.passwd", "passwd");
    }

    public String topicName(){
        return properties.getProperty("mqttclient.connect.topicName", "topicName");
    }

    public String topics() {
        return properties.getProperty("mqttclient.subscribe.topics", "");
    }

    public int keepAliveInterval() {
        return tryParse(properties.getProperty("mqttclient.keep.Alive.Interval", "10"));
    }

    public int connectionTimeout() {
        return tryParse(properties.getProperty("mqttclient.connection.Timeout", "300"));
    }




}
