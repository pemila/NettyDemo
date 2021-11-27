package com.pemila.netty.basic.uptime;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.TimeUnit;

/**
 * @author pemila
 * @date 2021/11/23 21:54
 **/
@ChannelHandler.Sharable
public class UptimeClientHandler extends SimpleChannelInboundHandler<Object> {

    /*
    * 由于客户端同时连接的服务端仅有一个，
    * 因此当前handler虽然被标记为共享的，但实际上可以不考虑线程安全问题
    * */

    long startTime = -1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(startTime<0){
            startTime = System.currentTimeMillis();
        }
        println("connect to :" + ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // discard
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(!(evt instanceof IdleStateEvent)){
            return;
        }

        IdleStateEvent event = (IdleStateEvent) evt;
        if(event.state() == IdleState.READER_IDLE){
            // 一段时间未收到消息时断开连接
            println("Disconnecting due to no inbound traffic");
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        println("Disconnected from :" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        println("sleeping for: "+ UptimeClient.RECONNECT_DELAY + "s");
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                println("Reconnecting to: " + UptimeClient.HOST + ':' + UptimeClient.PORT);
                UptimeClient.connect();
            }
        },UptimeClient.RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    void println(String msg) {
        if (startTime < 0) {
            System.err.format("[SERVER IS DOWN] %s%n", msg);
        } else {
            System.err.format("[UPTIME: %5ds] %s%n", (System.currentTimeMillis() - startTime) / 1000, msg);
        }
    }
}
