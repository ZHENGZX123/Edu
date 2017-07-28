package cn.kiway.activity.main.teaching.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;


public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {
	// 设置心跳时间 开始
	public static final int MIN_CLICK_DELAY_TIME = 1000 * 30;
	public static ChannelHandlerContext ctx;
	private long lastClickTime = 0;

	// 设置心跳时间 结束

	// 利用写空闲发送心跳检测消息
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			case WRITER_IDLE:
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
					lastClickTime = System.currentTimeMillis();
					ctx.writeAndFlush("heard");
					System.out.println("send ping to server----------");
				}
				break;
			default:
				break;
			}
		}
	}

	// 这里是断线要进行的操作
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("重连了。---------");
		NettyClientBootstrap bootstrap = PushClient.getBootstrap();
		bootstrap.startNetty();
	}

	// 这里是出现异常的话要进行的操作
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		System.out.println("出现异常了。。。。。。。。。。。。。");
		cause.printStackTrace();
	}

	// 这里是接受服务端发送过来的消息
	@Override
	protected void messageReceived(ChannelHandlerContext channelHandlerContext,
			Object msg) throws Exception {
		ctx = channelHandlerContext;
		if (msg instanceof String) {
			System.out.println(msg);
			if (callBack != null) {
				callBack.Message((String) msg);
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	@Skip
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		this.ctx = ctx;
	}

	public static void sendMessage(String context) {
		if (ctx != null && !ctx.isRemoved())
			ctx.channel().writeAndFlush(context);
	}

	@Override
	@Skip
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		super.close(ctx, promise);
		ctx.channel().writeAndFlush("13");
	}

	@SuppressWarnings("static-access")
	@Override
	@Skip
	public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		this.ctx = ctx;
		super.disconnect(ctx, promise);
	}

	static NettyMessageCallBack callBack;

	public static NettyMessageCallBack getCallBack() {
		return callBack;
	}

	public static void setCallBack(NettyMessageCallBack callBack) {
		NettyClientHandler.callBack = callBack;
	}

	public interface NettyMessageCallBack {
		public void Message(String string) throws Exception;
	}
}
