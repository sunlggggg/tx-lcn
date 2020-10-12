package com.codingapi.txlcn.protocol.message.separate;

import lombok.Data;

/**
 * ·
 * 通过{@code DataRequestMessage} 在服务端获取的结果
 *
 * @author sunligang
 */
@Data
public class DataResponseMessage extends AbsMessage {
   public DataResponseMessage(String messageId){
       this.messageId = messageId;
   }

    private String value;
}
