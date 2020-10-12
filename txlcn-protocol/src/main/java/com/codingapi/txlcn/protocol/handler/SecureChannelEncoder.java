package com.codingapi.txlcn.protocol.handler;

import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.util.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.codingapi.txlcn.protocol.handler.SessionUtil.getSessionAttribute;

/**
 * @author sunligang
 */
@ChannelHandler.Sharable
public class SecureChannelEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        Connection connection = getSessionAttribute(ctx).get();
        if (connection != null && connection.isOk()) {
            String mpPassword = connection.getMpPassword();
            out.writeBytes(AESUtil.encrypt(msg.array(), mpPassword));
        } else {
            System.out.println("encode ...");
            out.writeBytes(msg);
        }
    }
}
