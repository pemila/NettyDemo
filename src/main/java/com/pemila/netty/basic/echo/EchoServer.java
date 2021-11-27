package com.pemila.netty.basic.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author pemila
 * @date 2021/10/19 12:38
 **/
public class EchoServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final EchoServerHandler echoHandler = new EchoServerHandler();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(echoHandler);
                        }
                    });
            ChannelFuture f = b.bind(8090).sync();
            f.channel().closeFuture().sync();


        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}
