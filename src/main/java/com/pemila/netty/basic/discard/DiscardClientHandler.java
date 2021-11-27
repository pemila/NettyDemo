package com.pemila.netty.basic.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

/**
 * @author pemila
 * @date 2021/11/23 10:16
 **/
public class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {

    private ByteBuf content;
    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        // 初始化消息
        content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);
        // 发送初始消息
        generateTraffic();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        content.release();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 读取响应，由于discardServer不会回应client，因此这里不做处理
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void generateTraffic() {
        ctx.writeAndFlush(content.retainedDuplicate()).addListener(trafficGenerator);
    }

    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if(channelFuture.isSuccess()){
                generateTraffic();
            }else{
                channelFuture.cause().printStackTrace();
                channelFuture.channel().close();
            }
        }
    };

}
