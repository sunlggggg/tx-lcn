package com.codingapi.txlcn.protocol.message;

import com.codingapi.txlcn.protocol.await.Lock;
import com.codingapi.txlcn.protocol.await.LockContext;
import com.codingapi.txlcn.protocol.config.Config;
import com.codingapi.txlcn.protocol.exception.ProtocolException;
import com.codingapi.txlcn.protocol.message.separate.*;
import io.netty.channel.Channel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;

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

    public Connection(Channel channel, Config config) {
        this.config = config;
        this.remoteAddress = (InetSocketAddress) channel.remoteAddress();
        this.channel = channel;
        this.remoteHost = remoteAddress.getAddress().getHostAddress();
        this.remotePort = remoteAddress.getPort();
        this.uniqueKey = String.format("%s:%d", remoteHost, remotePort);
    }


    public void send(final Message msg) {
        if (channel != null) {
            channel.writeAndFlush(msg);
        } else {
            LOGGER.error("Can not send message " + msg.getClass() + " to " + toString());
        }
    }


    public DataResponseMessage request(final DataRequestMessage msg) {
        if (channel != null) {
            Lock lock = LockContext.getInstance().addKey(msg.getMessageId());
            try {
                LOGGER.debug("send message {}", msg);
                channel.writeAndFlush(msg);
                lock.await(config.getAwaitTime());
                return (DataResponseMessage) lock.getRes();
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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Connection that = (Connection) other;

        return Objects.equals(uniqueKey, that.uniqueKey);
    }

    @Override
    public int hashCode() {
        return uniqueKey != null ? uniqueKey.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "remoteAddress=" + remoteAddress +
                ", isOpen=" + (channel != null) +
                '}';
    }
}
