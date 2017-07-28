package cn.kiway.yjhz.wifimanager.websocket;

import android.content.Context;
import android.os.Handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by arvin on 2017/3/13 0013.
 */

public class WebsocketChatServerInitializer extends
        ChannelInitializer<SocketChannel> {
    private Context mContext;
    private Handler  mHandler;

    public WebsocketChatServerInitializer(Handler handler){
        mHandler = handler;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(64*1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new WebSocketServerHandler());
        pipeline.addLast( new TextWebSocketFrameHandler(mHandler));

    }
}