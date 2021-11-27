package com.pemila.netty.basic.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 *
 * discard服务：丢弃所有收到的消息
 * @author pemila
 * @date 2021/11/23 9:10
 **/
public class DiscardServer {

    private static final boolean SSL = System.getProperty("ssl")!=null;
    private static final int PORT = Integer.parseInt(System.getProperty("PORT","8009"));


    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        final SslContext sslContext;
        if(SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();
        }else{
            sslContext = null;
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if(sslContext!=null){
                                p.addLast(sslContext.newHandler(ch.alloc()));
                            }
                            p.addLast(new DiscardServerHandler());
                        }
                    });
            // 绑定端口，并启动准备接受请求
            ChannelFuture future = b.bind(PORT).sync();
            // sync() 方法会调用await()使主线程进入等待状态
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
