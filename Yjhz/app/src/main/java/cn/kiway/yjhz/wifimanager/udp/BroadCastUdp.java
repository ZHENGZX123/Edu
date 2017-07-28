package cn.kiway.yjhz.wifimanager.udp;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadCastUdp extends Thread {
	private String dataString;
	public static final int DEFAULT_PORT = 43708;// 发送广播的端口

	private static final int MAX_DATA_PACKET_LENGTH = 40;// 发送广播数据的最大长度

	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];// 发送的数据

	public static boolean isRun = true;// 是否发送广播

	public static DatagramSocket udpSocket;// 广播

	public BroadCastUdp(String dataString) {
		this.dataString = dataString;
		isRun = true;
	}

	public void setMessageData(String data) {
		this.dataString = data;
	}

	public void run() {
		DatagramPacket dataPacket = null;
		try {
			if (udpSocket == null)
				udpSocket = new DatagramSocket(DEFAULT_PORT);
			dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
			byte[] data = dataString.getBytes();
			dataPacket.setData(data);
			dataPacket.setLength(data.length);
			dataPacket.setPort(DEFAULT_PORT);
			InetAddress broadcastAddr;
			broadcastAddr = InetAddress.getByName("255.255.255.255");
			dataPacket.setAddress(broadcastAddr);
		} catch (Exception e) {
			Log.e("-----", e.toString());
		}
		while (isRun) {
			try {
				udpSocket.send(dataPacket);
				sleep(2000);
				System.out.println("发送广播：：：" + dataString);
			} catch (Exception e) {
			}
		}
	}
}
