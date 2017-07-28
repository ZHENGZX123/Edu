package cn.kiway.yjhz.wifimanager.websocket;

/**
 * Created by arvin on 2017/3/10 0010.
 */

import android.util.Log;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

/**
 * Handles handshakes and messages
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String TAG = "kiway_WebSocket";
    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker webSocketServerHandshaker;
    private ChannelHandlerContext channelHandlerContext;


    /*   public void channelRead(ChannelHandlerContext ctx, FullHttpRequest obj) throws Exception {
           Log.d(TAG, "channelRead,msg: " + obj.toString());

           if (handleHttpRequest(ctx,  obj))
               return;

           super.channelRead(ctx, obj);
       }
   */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest obj) throws Exception {
        Log.d(TAG, "messageReceived,obj: " + obj.toString());

        if (obj instanceof HttpRequest) {
            handleHttpRequest(ctx, obj);
        } else if (obj instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) obj);
        }
    }

    private boolean handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req)
            throws Exception {

        //屏蔽掉非websocket握手请求
        //只接受http GET和headers['Upgrade']为'websocket'的http请求
        if (req.method() == HttpMethod.GET &&
                "websocket".equalsIgnoreCase(req.headers().get("Upgrade").toString())) {

            // Handshake
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    getWebSocketLocation(req), null, false);

            webSocketServerHandshaker = wsFactory.newHandshaker(req);
            if (webSocketServerHandshaker == null) {
                wsFactory.sendUnsupportedVersionResponse(ctx.channel());

            } else {
                //向客户端发送websocket握手,完成握手
                //客户端收到的状态是101 sitching protocol
                webSocketServerHandshaker.handshake(ctx.channel(), req);
                // .addListener(WebSocketServerHandshaker.HANDSHAKE_LISTENER);
                return true;
            }

            channelHandlerContext = ctx;

        } else {
            DefaultHttpResponse resp = new DefaultHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.BAD_REQUEST);
            ctx.write(resp);
            ctx.close();
        }

        return false;
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            channelHandlerContext = null;
            return;
        } else if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content()));
            return;
        } else if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported",
                    frame.getClass().getName()));
        }


        // 处理接受到的数据（转成大写）并返回
        String request = ((TextWebSocketFrame) frame).text();
        Log.d(TAG, String.format("Channel %s received %s", ctx.channel().id(), request));

        ctx.channel().write(new TextWebSocketFrame(request.toUpperCase()));
    }

    /**
     * 消息异常
     *
     * @param ctx
     * @param e
     * @throws Exception
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Exception e) throws Exception {
        super.exceptionCaught(ctx, e);
        e.getCause().printStackTrace();
        ctx.channel().close();
        channelHandlerContext = null;
    }

    private static String getWebSocketLocation(HttpRequest req) {
        return "ws://" + req.headers().get("HOST") + WEBSOCKET_PATH;
    }

    String[] webStrings = null;
    String get;
    String host;
    String connection;
    String pragem;
    String ccacht_control;
    String upgrade;
    String origin;

    private boolean isWebSocket(Object obj) {
        String str = (String) obj;
        Log.d(TAG, "isWebSocket,str: " + str);
        if (str.contains("GET")) {
            if (webStrings == null)
                webStrings = str.split("\\n");

            for (int i = 0; i < webStrings.length; i++) {
                str = webStrings[i];
                Log.d(TAG, "isWebSocket,webStrings[i] : " + webStrings[i]);
                if (str.contains("GET")) {
                    get = str;
                } else if (str.contains("Host")) {
                    host = str.split("\\:\\ ")[1];
                } else if (str.contains("Connection")) {
                    connection = str.split("\\:\\ ")[1];
                } else if (str.contains("Pragma")) {
                    pragem = str.split("\\:\\ ")[1];
                } else if (str.contains("Cache-Control")) {
                    ccacht_control = str.split("\\:\\ ")[1];
                } else if (str.contains("Upgrade")) {
                    upgrade = str.split("\\:\\ ")[1];
                    Log.d(TAG, "isWebSocket,upgrade: " + upgrade);
                } else if (str.contains("Origin")) {
                    origin = str.split("\\:\\ ")[1];
                }
            }
            return true;
        }
        return false;
    }

    public void setMessage(Object msg) {
        if (channelHandlerContext != null)
            channelHandlerContext.channel().write(msg);
    }

}


