package cn.kiway.yjhz.wifimanager.websocket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.kiway.yjhz.utils.GlobeVariable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by arvin on 2017/3/13 0013.
 */

public class TextWebSocketFrameHandler extends
        SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final String TAG = "kiway_TextWebSocket";
    private static Handler mhandler;

    public TextWebSocketFrameHandler(Handler handler) {
        mhandler = handler;
    }

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


/*    public void channelRead(ChannelHandlerContext ctx,
                            TextWebSocketFrame object) throws Exception { // (1)
        Log.d(TAG, "channelRead, obj: " + object.toString());
        if(object instanceof TextWebSocketFrame){
            TextWebSocketFrame msg =(TextWebSocketFrame) object;
            Channel incoming = ctx.channel();
            for (Channel channel : channels) {
                if (channel != incoming){
                    channel.writeAndFlush(new TextWebSocketFrame("[" + incoming.remoteAddress() + "]" + msg.text()));
                } else {
                    channel.writeAndFlush(new TextWebSocketFrame("[you]" + msg.text() ));
                }
            }
            return;
        }
    }*/

    public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame ttframe) throws Exception {
        Log.d(TAG, "messageReceived: " + ttframe.toString()); if (ttframe instanceof TextWebSocketFrame) {
            // 返回应答消息
            String request = ttframe.text();
            Log.d(TAG, "messageReceived, 收到消息: " + request);
            ctx.channel().write(new TextWebSocketFrame("服务器收到并返回：" + request));
            if (mhandler != null) {
                Message msg = new Message();
                msg.what = GlobeVariable.SHOW_WEBSOCKET_MESSAGE;
                msg.obj = request;
                mhandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));
        }
        channels.add(ctx.channel());
        Log.d(TAG, "Client,handlerAdded:" + incoming.remoteAddress() + "加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 离开"));
        }
        Log.d(TAG, "Client,handlerRemoved:" + incoming.remoteAddress() + "离开");
        channels.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
        Log.d(TAG, "Client,channelActive:" + incoming.remoteAddress() + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        Log.d(TAG, "Client, exceptionCaught :" + incoming.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }


}