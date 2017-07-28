package cn.kiway.yjhz.wifimanager.netty;

import cn.kiway.yjhz.wifimanager.MessageHander.AccpectMessageHander;

/**
 * netty推送服务端
 * 
 */
public class PushServer {

	public static void start(AccpectMessageHander accpectMessageHander) {
		try {
			new NettyServerBootstrap(accpectMessageHander,30000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

}
