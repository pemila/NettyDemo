package com.pemila.netty.qotm;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

/**
 * @author pemila
 * @date 2021/11/25 22:14
 **/
public class QuoteOfTheMomentClient {
    static final int PORT = Integer.parseInt(System.getProperty("port","7686"));

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new QuoteOfTheMomentClientHandler());
            Channel ch = b.bind(0).sync().channel();
            ch.writeAndFlush(
                new DatagramPacket(
                    Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
                    SocketUtils.socketAddress("255.255.255.255",PORT)
                )
            ).sync();

            if(!ch.closeFuture().await(5000)){
                System.err.println("QOTM request time out");
            }
        }finally {
            group.shutdownGracefully();
        }
    }
}
