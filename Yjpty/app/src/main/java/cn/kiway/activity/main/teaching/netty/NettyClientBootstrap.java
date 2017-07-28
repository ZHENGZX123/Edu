package cn.kiway.activity.main.teaching.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;

public class NettyClientBootstrap {
	private int port = 30000;
	public static String host;
	public static SocketChannel socketChannel;
	private static final EventExecutorGroup group = new DefaultEventExecutorGroup(
			20);
	public static boolean isConnect;

	public void startNetty() throws InterruptedException {
		if (socketChannel != null && socketChannel.isOpen()) {
			System.out.println("已经连接");
		} else {
			System.out.println("长链接开始");
			if (start()) {
				System.out.println("长链接成功");
				isConnect = true;
			} else {
				isConnect = false;
				System.out.println("长链接失败...");
			}
		}
	}

	private Boolean start() throws InterruptedException {
	    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoopGroup);
        bootstrap.remoteAddress(host, port);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
                socketChannel.pipeline().addLast("decoder", new StringDecoder());
                socketChannel.pipeline().addLast("encoder", new StringEncoder());
                socketChannel.pipeline().addLast(new NettyClientHandler());
            }
        });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
                System.out.println("connect server  成功---------");
                return true;
            } else {
                System.out.println("connect server  失败---------");
                startNetty();
                return false;
            }
        } catch (Exception e) {
            System.out.println("无法连接----------------");
            return false;
        }
	}

	public void closeChannel() {
		if (socketChannel != null) {
			socketChannel.close();
		}
	}

	public boolean isOpen() {
		if (socketChannel != null) {
			System.out.println(socketChannel.isOpen());
			return socketChannel.isOpen();
		}
		return false;
	}
}