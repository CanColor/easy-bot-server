package net.cancolor.easymirai.server;

import net.cancolor.easymirai.config.InitConfiguration;
import net.cancolor.easymirai.handler.WebSocketServerHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 初始化通道服务
 */
@Component
public class WebServer {
    Logger logger = LoggerFactory.getLogger(WebServer.class);
    @Autowired
    InitConfiguration conf;

    public void start() {
      // netty基本操作，两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wokerGroup = new NioEventLoopGroup();
        try{
            //netty的启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,wokerGroup).channel(NioServerSocketChannel.class)
                    //记录日志的handler，netty自带的
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG,1024*1024*10)
                    //设置handler
                    .childHandler(new ChannelInitializer< SocketChannel >(){
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //websocket协议本身是基于Http协议的，所以需要Http解码器
                            pipeline.addLast("http-codec",new HttpServerCodec());
                            //以块的方式来写的处理器
                            pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                            //netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
                            pipeline.addLast("aggregator",new HttpObjectAggregator(1024*1024*1024));
                            //这个是websocket的handler，是netty提供的，也可以自定义，建议就用默认的
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello",null,true,65535));
                            //自定义的handler，处理服务端传来的消息
                            pipeline.addLast(new WebSocketServerHandle());
                        }
                    });
            ChannelFuture channelFuture = null;
            try {
                channelFuture = serverBootstrap.bind(new InetSocketAddress(conf.nettyPort)).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }



    }
}
