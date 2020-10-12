package com.codingapi.txlcn.protocol.handler;

import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.util.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.codingapi.txlcn.protocol.handler.SessionUtil.getSessionAttribute;


/**
 * @author sunligang
 */
@Slf4j
public class SecureChannelDecoder extends MessageToMessageDecoder<ByteBuf> {


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        Connection connection = getSessionAttribute(ctx).get();
        System.out.println("decode ...");
        if (connection != null && connection.isOk()) {
            String mpPassword = connection.getMpPassword();
            out.add(AESUtil.decrypt(msg.array(), mpPassword));
            ctx.fireChannelRead(out);
        } else {
            ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
            buf.writeBytes(msg);
            ctx.fireChannelRead(buf);
        }
    }
}
