package com.codingapi.txlcn.protocol.handler.codec;

import com.alibaba.fastjson.JSON;


/**
 * @author sunligang
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
