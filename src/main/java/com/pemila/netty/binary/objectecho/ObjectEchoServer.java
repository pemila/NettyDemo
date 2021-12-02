package com.pemila.netty.binary.objectecho;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author pemila
 * @date 2021/12/2 9:39
 **/
public class ObjectEchoServer {

    static final boolean SSL = System.getProperty("ssl")!=null;
    static final int PORT = Integer.parseInt(System.getProperty("port","8007"));

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        final SslContext sslContext;
        if(SSL){
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();
        }else{
            sslContext = null;
        }
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslContext!=null){
                                pipeline.addLast(sslContext.newHandler(ch.alloc()));
                            }
                            pipeline.addLast(new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null))
                                    ,new ObjectEchoServerHandler());
                        }
                    });

            b.bind(PORT).sync().channel().closeFuture().sync();
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }






    }


}
