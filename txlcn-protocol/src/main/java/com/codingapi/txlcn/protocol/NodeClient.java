package com.codingapi.txlcn.protocol;


import com.codingapi.txlcn.protocol.config.Config;
import com.codingapi.txlcn.protocol.handler.ProtocolChannelHandler;
import com.codingapi.txlcn.protocol.handler.ProtocolChannelInitializer;
import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.event.TransactionCommitEvent;
import com.codingapi.txlcn.protocol.message.separate.TransactionMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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
        return new Connection(channelFuture.channel(), new Config());
    }


}
