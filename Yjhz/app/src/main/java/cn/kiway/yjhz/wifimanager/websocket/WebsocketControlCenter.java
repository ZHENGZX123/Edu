package cn.kiway.yjhz.wifimanager.websocket;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by arvin on 2017/3/13 0013.
 */

public class WebsocketControlCenter {
    private Context mContext;
    private static final String TAG = "kiway_WebsocketControlCenter";
    private int port;
    private ChannelFuture mChannelFuture;
    private Handler mHandler;
    //需要为静态变量,才可开启websocket服务,原因暂不知.
    private static ServerBootstrap mServerBootstrap;

    public WebsocketControlCenter(int port, Handler handler) {
        this.port = port;
        mHandler = handler;
        initWebsocket();
    }

    @SuppressLint("LongLogTag")
    public void initWebsocket() {
        Log.d(TAG, "initWebsocket");
        if (mServerBootstrap != null)
            return;

        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            mServerBootstrap = new ServerBootstrap(); // (2)
            mServerBootstrap.group(bossGroup, workerGroup);
            mServerBootstrap.channel(NioServerSocketChannel.class); // (3)
            mServerBootstrap.childHandler(new WebsocketChatServerInitializer(mHandler)); //(4)
            mServerBootstrap.option(ChannelOption.SO_BACKLOG, 128);             // (5)
            mServerBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);     // (6)
            // 通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
            mServerBootstrap.option(ChannelOption.TCP_NODELAY, true);
            // 保持长连接状态
            mServerBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口并开始接收
            mChannelFuture = mServerBootstrap.bind(port).sync(); // (7)

            if (mChannelFuture.isSuccess()) {
                Log.d(TAG, "服务端启动：：：nettyWebsocket server start---------------");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "initWebsocket,e: " + e.getMessage());
        }
    }

    public void closeFuture() {
        if (mChannelFuture == null)

            try {
                mChannelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }


}
