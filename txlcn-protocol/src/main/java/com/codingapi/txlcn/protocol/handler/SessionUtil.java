package com.codingapi.txlcn.protocol.handler;

import com.codingapi.txlcn.protocol.message.Connection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunligang
 */
public class SessionUtil {

    static final String SESSION_ATTRIBUTE_KEY = "session";

    static Map<Channel, Object> locks = new ConcurrentHashMap<>();

    public static Connection getSessionAttribute(ChannelHandlerContext ctx) {
        return getSessionAttribute(ctx.channel());
    }

    public static Connection getSessionAttribute(Channel channel) {
        return channel.attr(AttributeKey.<Connection>valueOf(SESSION_ATTRIBUTE_KEY)).get();
    }

    public static void setSessionAttribute(Channel channel, Connection connection) {
        Attribute<Connection> attribute = channel.attr(AttributeKey.<Connection>valueOf(SESSION_ATTRIBUTE_KEY));
        if (attribute.get() == null) {
            synchronized (locks.computeIfAbsent(channel, k -> new Object())) {
                if (attribute.get() == null) {
                    channel.attr(AttributeKey.<Connection>valueOf(SESSION_ATTRIBUTE_KEY)).set(connection);
                }
            }
        }
    }

    public static void setSessionAttribute(ChannelHandlerContext ctx, Connection connection) {
        setSessionAttribute(ctx.channel(), connection);
    }

}
