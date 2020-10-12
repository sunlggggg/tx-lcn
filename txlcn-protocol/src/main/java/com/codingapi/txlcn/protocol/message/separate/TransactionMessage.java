package com.codingapi.txlcn.protocol.message.separate;

import com.codingapi.txlcn.protocol.Protocoler;
import com.codingapi.txlcn.protocol.await.Lock;
import com.codingapi.txlcn.protocol.await.LockContext;
import com.codingapi.txlcn.protocol.message.Connection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationContext;
import sun.jvm.hotspot.memory.Generation;

/**
 * @author lorne
 * @date 2020/3/4
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TransactionMessage extends AbsMessage {

    private String key;

    private String value;


    @Override
    public void handle(ApplicationContext springContext, Protocoler protocoler, Connection connection) throws Exception {
        super.handle(springContext, protocoler, connection);
        // 服务器端处理数据并返回
        if (key.equals("123")) {
            value = "456";
        }
        connection.send(this);
    }
}
