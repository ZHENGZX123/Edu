package cn.kiway.activity.main.teaching.netty;

import cn.kiway.IConstant;

/**
 * netty推送客户端
 */
public class PushClient {

	private static NettyClientBootstrap bootstrap;

	public static NettyClientBootstrap getBootstrap() {
		return bootstrap;
	}

	public static void setBootstrap(NettyClientBootstrap bootstrap) {
		PushClient.bootstrap = bootstrap;
	}

	public static void create() {
		NettyClientBootstrap bootstrap = new NettyClientBootstrap();
		PushClient.setBootstrap(bootstrap);
	}

	public static void start() {
		IConstant.executorService.execute(new Runnable() {
			public void run() {
				NettyClientBootstrap bootstrap = PushClient.getBootstrap();
				try {
					bootstrap.startNetty();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				PushClient.setBootstrap(bootstrap);
			}
		});
	}

	/**
	 * 调用此方法关闭通道后会自动重连，如果想真正实现关闭， 需要修改handler中重写的断线处理方法。
	 * 
	 * @see NettyClientHandler
	 */
	public static void close() {
		NettyClientBootstrap bootstrap = PushClient.getBootstrap();
		bootstrap.closeChannel();
		PushClient.setBootstrap(bootstrap);
	}

	/**
	 * @return 返回通道连接状态
	 */
	public static boolean isOpen() {
		NettyClientBootstrap bootstrap = PushClient.getBootstrap();
		return bootstrap.isOpen();
	}
}
