package com.codingapi.txlcn.protocol.handler.codec;

import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.Message;
import com.codingapi.txlcn.protocol.util.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.codingapi.txlcn.protocol.handler.SessionUtil.getSessionAttribute;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author sunligang
 */
@Slf4j
@ChannelHandler.Sharable
public class SecureChannelEncoder extends MessageToByteEncoder<Message> {
    private Serializer serializer;

    public SecureChannelEncoder(Serializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        Connection connection = getSessionAttribute(ctx);
        byte[] bytes = serializer.serialize(msg);
        if (connection != null && connection.isOk()) {
            String mpPassword = connection.getMpPassword();
            bytes = AESUtil.encrypt(bytes, mpPassword);
        }

        byte[] clazzNameBytes = msg.getClass().getName().getBytes(UTF_8);
        out.writeInt(clazzNameBytes.length);
        out.writeInt(bytes != null ? bytes.length : 0);
        out.writeBytes(clazzNameBytes);
        out.writeBytes(bytes);
    }

}
