package com.pemila.netty.text.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * 基于TLS的聊天服务器
 * @author pemila
 * @date 2021/11/27 11:21
 **/
public class SecureChatServer {

    static final int PORT = Integer.parseInt(System.getProperty("port","8992"));

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslContext = SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new SecureChatServerInitializer(sslContext));

            b.bind(PORT).sync().channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
