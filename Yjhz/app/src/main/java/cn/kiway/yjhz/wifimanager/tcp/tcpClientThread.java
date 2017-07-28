package cn.kiway.yjhz.wifimanager.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import cn.kiway.yjhz.utils.Logger;

public class tcpClientThread extends Thread {
	public static Socket socketClient = null;
	public static String ipAdress = "192.168.8.16";// tcp地址
	public static int ipPost = 30000;// tcp端口
	static BufferedReader bufferedReaderClient = null;// 获取输入流
	static PrintWriter printWriterClient = null;// 获取输出流
	static boolean isConnected = true;// 是否循环监听消息
	public static boolean isConnectFinish;// 是否连接成功
	static long time;

	@Override
	public void run() {
		try {
			if (socketClient != null && bufferedReaderClient != null
					&& printWriterClient != null) {
				Logger.log("TCP服务已连接：：：：");
				return;
			}
			// 连接服务器
			socketClient = new Socket(ipAdress, ipPost);
			// 取得输入、输出流
			bufferedReaderClient = new BufferedReader(new InputStreamReader(
					socketClient.getInputStream()));
			printWriterClient = new PrintWriter(socketClient.getOutputStream(),
					true);
			isConnectFinish = true;
			isConnected = true;
			Logger.log("TCP服务已连接成功：：：：：");
		} catch (Exception e) {
			try {
				isConnected = false;
				if (socketClient != null)
					socketClient.close();
				if (bufferedReaderClient != null)
					bufferedReaderClient.close();
				if (printWriterClient != null)
					printWriterClient.close();
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			isConnectFinish = false;
			Logger.log(":::::::::" + e);
		}
		char[] buffer = new char[256];
		int count = 0;
		while (isConnected) {
			try {
				if ((count = bufferedReaderClient.read(buffer)) > 0) {
					Logger.log("接收信息 " + "\"" + getInfoBuff(buffer, count)
							+ "\"" + "\n");// 消息换行
					if (Integer.parseInt(getInfoBuff(buffer, count)) == 1) {
					} else if (Integer.parseInt(getInfoBuff(buffer, count)) <= 0) {// 客户端掉线
					}
				}
			} catch (Exception e) {
				return;
			}
		}
	}

	// 解析数据
	private String getInfoBuff(char[] buff, int count) {
		char[] temp = new char[count];
		for (int i = 0; i < count; i++) {
			temp[i] = buff[i];
		}
		return new String(temp);
	}

	// 发送数据
	public static void sendPlayData(String playData) {
		if (System.currentTimeMillis() - time < 500) {// 限制发消息的频率，太快了话，连接会断
			time = System.currentTimeMillis();
			return;
		}
		time = System.currentTimeMillis();
		printWriterClient.print(playData);// 发送给服务器
		printWriterClient.flush();
	}

	public interface TcpCallBack {
		public void tcpSuccessCallBack() throws Exception;

		public void tcpErrorCallBack() throws Exception;
	}

	/**
	 * 关闭scoket
	 * */
	public static void closeScoket() {
		Logger.log("TCP服务已连接关闭");
		if (tcpClientThread.socketClient != null) {
			try {
				tcpClientThread.isConnected = false;
				tcpClientThread.isConnectFinish = false;
				tcpClientThread.socketClient.close();
				tcpClientThread.socketClient = null;
				if (tcpClientThread.printWriterClient != null)
					tcpClientThread.printWriterClient.close();
				tcpClientThread.printWriterClient = null;
				if (tcpClientThread.bufferedReaderClient != null)
					tcpClientThread.bufferedReaderClient.close();
				tcpClientThread.bufferedReaderClient = null;
				interrupted();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
