package cn.kiway.mqtt.cli;

import android.content.Context;
import android.util.Log;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage;

public class KwMqttAcli implements MqttCallback {

	private static final String TAG = "kiway_KwMqttAcli";
	private Context mContext;
	private MqttAsyncClient client=null;
	private MqttConnectOptions options;
	private String clientid;
	private String username;
	private String passwd;
	private int recode=0;
	private String remsg="";
	private TopicProcessService loginerrcbk=new LoginErrorCbk();
    private Map<String,TopicProcessService> cbks=new HashMap<String,TopicProcessService>();

    public KwMqttAcli(Context context, int i){
		mContext = context;
    	clientid=Settings.getInstance(mContext).clientid()+i;
    	username=Settings.getInstance(mContext).mqttUserName();
    	passwd=Settings.getInstance(mContext).mqttPasswd();

    	init(clientid);
    }
    public KwMqttAcli(Context context,int i,String u,String p){
		mContext = context;
    	clientid=Settings.getInstance(mContext).clientid()+i;
    	username=u;
    	passwd=p;
    	init(clientid);
    }
    
    public KwMqttAcli(Context context,int i,String u,String p,TopicProcessService lcbk){
		mContext = context;
    	clientid=Settings.getInstance(mContext).clientid()+i;
    	username=u;
    	passwd=p;
    	init(clientid);
    	loginerrcbk=lcbk;
    }
    
    public KwMqttAcli(Context context,String id){
		mContext = context;
    	clientid=id;
    	username=Settings.getInstance(mContext).mqttUserName();
    	passwd=Settings.getInstance(mContext).mqttPasswd();
    	init(clientid);
    }
    
    public String getId(){
    	return clientid ;
    }
    public String getUserName(){
    	return username;
    }
    public int getRecode(){
    	return recode;
    }
    
    public String getRemsg(){
    	return remsg;
    }
    
    private void init(String clientid){
    	if(client==null)
        	try {
			
			   String url = "tcp://" + Settings.getInstance(mContext).mqttHost() +
                        ":" + Settings.getInstance(mContext).mqttPort();
				Log.d(TAG, "URL: " + url );
            	client = new MqttAsyncClient(url, clientid);
				 
                client.setCallback(this);
                System.out.println("tcp://"+Settings.getInstance(mContext)
						.mqttHost()+":"+Settings.getInstance(mContext).mqttPort());
                options = new MqttConnectOptions();
                options.setCleanSession(false);
                options.setUserName(username);
                options.setPassword(passwd.toCharArray());
                options.setConnectionTimeout(Settings.getInstance(mContext).connectionTimeout());
                options.setKeepAliveInterval(Settings.getInstance(mContext).keepAliveInterval());
                conn();
                client.subscribe(clientid+"/+/#",2);
            }catch (MqttException e) {
            	recode=e.getReasonCode();
            	remsg=e.getMessage();
            	loginerrcbk.process(remsg,null,""+recode);
            }
    }
    private void conn() {
    	try {
        	client.connect(options);
        } catch (MqttException e) {
        	recode=e.getReasonCode();
        	remsg=e.getMessage();
			Log.d(TAG, "登录失败**##");
			Log.d(TAG, "topic: " + remsg);
			Log.d(TAG, "time: " + recode);;
        	loginerrcbk.process(remsg,null,""+recode);
        }
    }
    
    public void publish(String topic, byte[] data) {
    	try {
    		if(connect())client.publish(topic, data, 2, false);
    		//client.publish(topic, data, 2, false, userContext, callback)
    	} catch (MqttException e) {
            
        }
    	
    }
    public void subscribe(String topicName) {
    	try {
    		if(connect()){
    			client.subscribe(topicName,2);
    		}    		
    	} catch (MqttException e) {
            e.printStackTrace();
        }
    	
    }
    
    public void subscribe(String topicName,TopicProcessService tps) {
    	try {
    		if(connect()){
    			client.subscribe(topicName,2);
    			cbks.put(topicName, tps);
    		}
    		
    	} catch (MqttException e) {
            e.printStackTrace();
        }
    	
    }
    
    public boolean connect() {
    	while (!client.isConnected()&&(recode!=4)){        
        	try {
					Thread.sleep(5000);
			} catch (InterruptedException e) {					
			}        	
        	conn();
        }
    	return client.isConnected();
    }
    public void close(){
    	try {
			if(client.isConnected())client.disconnect();
		} catch (MqttException e) {
			
		}
    }
    
    @Override
    public void connectionLost(Throwable msg) {
    	System.out.println(msg.getMessage());
    	connect();
    }


	/**
	 * publish回调
	 * @param imtk
     */
	@Override
	public void deliveryComplete(IMqttDeliveryToken imtk) {
		switch(imtk.getResponse().getType()){
			case MqttWireMessage.MESSAGE_TYPE_CONNACK:
				System.out.println(""+imtk.getMessageId()+"MESSAGE_TYPE_CONNACK");
			break;
			case MqttWireMessage.MESSAGE_TYPE_CONNECT:
				System.out.println(""+imtk.getMessageId()+"MESSAGE_TYPE_CONNECT");
			break;
			case MqttWireMessage.MESSAGE_TYPE_PUBACK:
				System.out.println(""+imtk.getMessageId()+"MESSAGE_TYPE_PUBACK");
			break;
			case MqttWireMessage.MESSAGE_TYPE_PUBCOMP:
				System.out.println(""+imtk.getMessageId()+"MESSAGE_TYPE_PUBCOMP");
			break;
			case MqttWireMessage.MESSAGE_TYPE_PUBREC:
				System.out.println(""+imtk.getMessageId()+"MESSAGE_TYPE_PUBREC");
			break;
			default:
				System.out.println(""+imtk.getMessageId()+"    "+imtk.getResponse().getType());
		}
        
    }

	/**
	 * subscribe回调
	 * @param topic
	 * @param message
	 * @throws Exception
     */
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String time = new Timestamp(System.currentTimeMillis()).toString();
        
    	if(cbks.containsKey(topic)){
    		TopicProcessService tps=cbks.get(topic);
    		 tps.process(topic, message, time);    		
        }else RegTopicProcSrv.INSTANCE.process(topic, message, time);
		
	}

}

