package com.pemila.netty.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

/**
 * @author pemila
 * @date 2021/10/19 13:07
 **/
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final byte[] CONTENT = "hello world".getBytes();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest){
            HttpRequest req = (HttpRequest) msg;
            FullHttpResponse resp = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, Unpooled.wrappedBuffer(CONTENT));
            resp.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.TEXT_PLAIN)
                    .setInt(HttpHeaderNames.CONTENT_LENGTH,resp.content().readableBytes());
            ChannelFuture f = ctx.write(resp);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
