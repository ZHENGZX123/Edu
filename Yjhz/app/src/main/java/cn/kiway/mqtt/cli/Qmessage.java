package cn.kiway.mqtt.cli;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Qmessage {
	private String topic;
	private MqttMessage message;
	private String time;
	
	public Qmessage(String topic1,MqttMessage message1,String time1){
		topic=topic1;
		message=message1;
		time=time1;
		
	}
	public String topic(){
		return topic;
	}
	public String time(){
		return time;
	}
	public MqttMessage message(){
		return message;
	}
	
}
