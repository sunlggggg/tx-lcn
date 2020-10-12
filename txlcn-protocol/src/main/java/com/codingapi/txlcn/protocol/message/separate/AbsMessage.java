package com.codingapi.txlcn.protocol.message.separate;

import com.codingapi.txlcn.protocol.Protocoler;
import com.codingapi.txlcn.protocol.await.Lock;
import com.codingapi.txlcn.protocol.await.LockContext;
import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.Message;
import lombok.Data;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

/**
 * @author lorne
 * @date 2020/3/4
 * @description
 */
@Data
public abstract class AbsMessage implements Message {

    protected String messageId;

    public AbsMessage() {
        this.messageId = UUID.randomUUID().toString();
    }

    @Override
    public void handle(ApplicationContext springContext,
                       Protocoler protocoler,
                       Connection connection) throws Exception {
        // 唤醒等待消息
        if (messageId != null) {
            Lock lock = LockContext.getInstance().getKey(messageId);
            if (lock != null) {
                lock.setRes(this);
                lock.signal();
            }
        }
    }
}
