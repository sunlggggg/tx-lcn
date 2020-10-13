package com.codingapi.txlcn.protocol.handler.codec;

import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.Message;
import com.codingapi.txlcn.protocol.util.AESUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.codingapi.txlcn.protocol.handler.SessionUtil.getSessionAttribute;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * @author sunligang
 */
@Slf4j
public class SecureChannelDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final Serializer serializer;


    public SecureChannelDecoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //因为之前编码的时候写入一个Int型，4个字节来表示长度
        if (byteBuf.readableBytes() < 4 * 2) {
            return;
        }
        //标记当前读的位置
        byteBuf.markReaderIndex();
        int classNameLength = byteBuf.readInt();
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < classNameLength + dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] classNameData = new byte[classNameLength];
        //将byteBuf中的数据读入data字节数组
        byteBuf.readBytes(classNameData);
        String className = new String(classNameData, UTF_8);
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        Connection connection = getSessionAttribute(channelHandlerContext);
        if (connection != null && connection.isOk()) {
            String mpPassword = connection.getMpPassword();
            data = AESUtil.decrypt(data, mpPassword);
        }
        Message obj = (Message) serializer.deserialize(Class.forName(className), data);
        list.add(obj);
    }
}
