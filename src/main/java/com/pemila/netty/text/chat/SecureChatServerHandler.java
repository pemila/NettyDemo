package com.pemila.netty.text.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;

/**
 * @author pemila
 * @date 2021/11/27 11:31
 **/
public class SecureChatServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
            (GenericFutureListener<Future<Channel>>) future -> {
                ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
                ctx.writeAndFlush("Your session is protected by " + ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite.\n");
                channels.add(ctx.channel());
            }
        );
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        for(Channel c : channels){
            if(c != ctx.channel()){
                c.writeAndFlush("[" + ctx.channel().remoteAddress() + "]" + msg + "\n");
            }else{
                c.writeAndFlush("[you] " + msg + "\n");
            }
        }

        if("bye".equals(msg.toLowerCase())){
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
