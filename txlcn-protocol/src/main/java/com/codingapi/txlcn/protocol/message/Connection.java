package com.codingapi.txlcn.protocol.message;

import com.codingapi.txlcn.protocol.await.Lock;
import com.codingapi.txlcn.protocol.await.LockContext;
import com.codingapi.txlcn.protocol.config.Config;
import com.codingapi.txlcn.protocol.exception.ProtocolException;
import com.codingapi.txlcn.protocol.message.separate.*;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;

import static com.codingapi.txlcn.protocol.handler.SessionUtil.getSessionAttribute;
import static com.codingapi.txlcn.protocol.handler.SessionUtil.setSessionAttribute;

/**
 * Maintains a TCP connection between the local peer and a neighbour
 */
public class Connection {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    @Getter
    private final InetSocketAddress remoteAddress;

    @Getter
    private Channel channel;

    @Getter
    private final String remoteHost;

    @Getter
    private final int remotePort;

    @Getter
    private final String uniqueKey;

    private final Config config;

    @Getter
    @Setter
    private volatile boolean isOk;

    @Getter
    @Setter
    private String mpPassword;


    public Connection(Channel channel, Config config) {
        this.config = config;
        this.remoteAddress = (InetSocketAddress) channel.remoteAddress();
        this.channel = channel;
        this.remoteHost = remoteAddress.getAddress().getHostAddress();
        this.remotePort = remoteAddress.getPort();
        this.uniqueKey = String.format("%s:%d", remoteHost, remotePort);
    }

    public void send(final Message msg, GenericFutureListener<? extends Future<? super Void>> listener) {
        if (channel != null) {
            channel.writeAndFlush(msg, channel.newPromise().addListener(listener));
        } else {
            LOGGER.error("Can not send message " + msg.getClass() + " to " + toString());
        }
    }

    public void send(final Message msg) {
        if (channel != null) {
            channel.writeAndFlush(msg);
        } else {
            LOGGER.error("Can not send message " + msg.getClass() + " to " + toString());
        }
    }

    public <T extends AbsMessage> T request(final AbsMessage msg, GenericFutureListener<? extends Future<? super Void>> listener) {
        if (channel != null) {
            Lock lock = LockContext.getInstance().addKey(msg.getMessageId());
            try {
                LOGGER.debug("send message {}", msg);
                channel.writeAndFlush(msg, channel.newPromise().addListener(listener));
                lock.await(config.getAwaitTime());
                return (T) lock.getRes();
            } finally {
                lock.clear();
            }
        } else {
            LOGGER.error("Can not send message " + msg.getClass() + " to " + toString());
            throw new ProtocolException("can't send message . ");
        }
    }


    public <T extends AbsMessage> T request(final AbsMessage msg) {
        if (channel != null) {
            Lock lock = LockContext.getInstance().addKey(msg.getMessageId());
            try {
                LOGGER.debug("send message {}", msg);
                channel.writeAndFlush(msg);
                lock.await(config.getAwaitTime());
                return (T) lock.getRes();
            } finally {
                lock.clear();
            }
        } else {
            LOGGER.error("Can not send message " + msg.getClass() + " to " + toString());
            throw new ProtocolException("can't send message . ");
        }
    }

    public void close() {
        LOGGER.debug("Closing session of {}", toString());
        if (channel != null) {
            channel.close();
            channel = null;
        }
    }

    @Override
    public String toString() {
        return "Connection{" +
                "remoteAddress=" + remoteAddress +
                ", isOpen=" + (channel != null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Connection that = (Connection) o;
        return remotePort == that.remotePort &&
                remoteHost.equals(that.remoteHost) &&
                uniqueKey.equals(that.uniqueKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteHost, remotePort, uniqueKey);
    }
}
