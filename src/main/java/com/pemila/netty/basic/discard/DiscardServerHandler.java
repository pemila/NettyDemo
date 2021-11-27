package com.pemila.netty.basic.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author pemila
 * @date 2021/11/23 9:39
 **/
public class DiscardServerHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 收到消息后直接丢弃
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(buf.readableBytes());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常时关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
