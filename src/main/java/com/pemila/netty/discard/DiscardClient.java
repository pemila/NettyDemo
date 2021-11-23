package com.pemila.netty.discard;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

/**
 * discard客户端，持续向指定地址发送随机数
 * @author pemila
 * @date 2021/11/23 10:09
 **/
public class DiscardClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host","127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port","8009"));
    static final int SIZE = Integer.parseInt(System.getProperty("size","256"));

    public static void main(String[] args) throws SSLException, InterruptedException {
        final SslContext sslContext;
        if(SSL){
            sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        }else {
            sslContext = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if(sslContext!=null){
                                p.addLast(sslContext.newHandler(ch.alloc(),HOST,PORT));
                            }
                            p.addLast(new DiscardClientHandler());
                        }
                    });
            ChannelFuture future = b.connect(HOST,PORT).sync();
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }



    }

}
