package com.pemila.netty.binary.objectecho;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

/**
 * @author pemila
 * @date 2021/12/2 10:07
 **/
public class ObjectEchoClientHandler extends ChannelInboundHandlerAdapter {

    private final List<Integer> firstMessage;
    public ObjectEchoClientHandler(){
        firstMessage = new ArrayList<>(ObjectEchoClient.SIZE);
        for(int i = 0; i < ObjectEchoClient.SIZE; i++){
            firstMessage.add(i);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture future = ctx.writeAndFlush(firstMessage);
        future.addListener(FIRE_EXCEPTION_ON_FAILURE);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write(msg);
        System.out.println("" + msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
