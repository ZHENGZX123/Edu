package cn.kiway.mqtt.cli;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class LoginErrorCbk implements TopicProcessService{

	private static final String TAG = "kiway_LoginErrorCbk";
	@Override
	public void process(String topic, MqttMessage message, String time) {
		Log.d(TAG, "登录失败**");
		Log.d(TAG, "topic: " + topic);
		Log.d(TAG, "time: " + time);

	}

}
