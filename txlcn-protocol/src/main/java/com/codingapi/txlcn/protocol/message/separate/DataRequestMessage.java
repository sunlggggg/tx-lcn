package com.codingapi.txlcn.protocol.message.separate;

import com.codingapi.txlcn.protocol.Protocoler;
import com.codingapi.txlcn.protocol.message.Connection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;


/**
 * 处理"数据请求"请求
 *
 * @author sunligang
 */
@Slf4j
@Data
public class DataRequestMessage extends AbsMessage {

    private String key;

    @Override
    public void handle(ApplicationContext springContext, Protocoler protocoler, Connection connection) throws Exception {
        // 服务器端处理数据并返回
        DataResponseMessage responseMessage = new DataResponseMessage(messageId);
        responseMessage.setValue("456");
        connection.send(responseMessage);
    }

}
