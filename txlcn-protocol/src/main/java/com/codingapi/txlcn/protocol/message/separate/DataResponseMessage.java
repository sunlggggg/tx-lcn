package com.codingapi.txlcn.protocol.message.separate;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ·
 * 通过{@code DataRequestMessage} 在服务端获取的结果
 *
 * @author sunligang
 */
@Data
@NoArgsConstructor
public class DataResponseMessage extends AbsMessage {
    public DataResponseMessage(String messageId) {
        this.messageId = messageId;
    }

    private String value;
}
