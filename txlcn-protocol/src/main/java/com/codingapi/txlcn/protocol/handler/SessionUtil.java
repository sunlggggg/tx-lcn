package com.codingapi.txlcn.protocol.handler;

import com.codingapi.txlcn.protocol.message.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class SessionUtil {

     static final String SESSION_ATTRIBUTE_KEY = "session";

     static Attribute<Connection> getSessionAttribute(ChannelHandlerContext ctx) {
          return ctx.channel().attr(AttributeKey.<Connection>valueOf(SESSION_ATTRIBUTE_KEY));
     }

}
