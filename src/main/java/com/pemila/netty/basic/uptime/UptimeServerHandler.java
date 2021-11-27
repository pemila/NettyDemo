package com.pemila.netty.basic.uptime;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.PlatformDependent;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pemila
 * @date 2021/11/23 21:29
 **/
@ChannelHandler.Sharable
public class UptimeServerHandler extends SimpleChannelInboundHandler<Object> {
    /*
    * @Sharable注解表示当前handler是一个共享的实例，
    * 表示可以被多个channel安全共享，此时当前handler内部需要关注线程安全问题
    */
    /** 错误的示范*/
    private int l = 0;
    /** 正确的示范*/
    private AtomicInteger ll = new AtomicInteger(0);
    volatile int lll = 0;
    ConcurrentMap<Integer,String> map = PlatformDependent.newConcurrentHashMap();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // discard
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
