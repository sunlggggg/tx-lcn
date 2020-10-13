package com.codingapi.txlcn.protocol;


import com.codingapi.txlcn.protocol.config.Config;
import com.codingapi.txlcn.protocol.handler.ProtocolChannelHandler;
import com.codingapi.txlcn.protocol.handler.ProtocolChannelInitializer;
import com.codingapi.txlcn.protocol.handler.SessionUtil;
import com.codingapi.txlcn.protocol.message.Connection;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutionException;


/**
 * @author sunligang
 */
@Slf4j
public class NodeClient {

    private static Bootstrap clientBootstrap;

    public NodeClient(ApplicationContext applicationContext) {
        EventLoopGroup networkEventLoopGroup = new NioEventLoopGroup(8);
        EventExecutorGroup eventExecutorGroup = new NioEventLoopGroup(10);
        ProtocolChannelHandler handler = new ProtocolChannelHandler(new Protocoler(new Config()), applicationContext, new Config());
        clientBootstrap = new Bootstrap();
        clientBootstrap.group(networkEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ProtocolChannelInitializer(new Config(), handler, eventExecutorGroup));
    }

    public static Connection getConnect(EndPoint endPoint) throws InterruptedException {
        ChannelFuture channelFuture = clientBootstrap.connect(endPoint.getIp(), endPoint.getPort()).sync();
        SessionUtil.setSessionAttribute(channelFuture.channel(), new Connection(channelFuture.channel(), new Config()));
        return SessionUtil.getSessionAttribute(channelFuture.channel());
    }


}
