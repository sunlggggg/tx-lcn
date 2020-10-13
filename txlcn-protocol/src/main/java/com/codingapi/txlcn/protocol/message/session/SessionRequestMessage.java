package com.codingapi.txlcn.protocol.message.session;

import cn.hyperchain.sdk.account.Account;
import com.codingapi.txlcn.protocol.Protocoler;
import com.codingapi.txlcn.protocol.config.NodeConstant;
import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.separate.AbsMessage;
import com.codingapi.txlcn.protocol.util.AccountUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import org.springframework.context.ApplicationContext;


/**
 * 处理建立连接请求
 *
 * @author sunligang
 */
@Data
public class SessionRequestMessage extends AbsMessage {

    private String publicKey;

    @Override
    public void handle(ApplicationContext springContext,
                       Protocoler protocoler,
                       Connection connection) throws Exception {
        Account account = NodeConstant.getNodeAccount();
        String mpPassword = AccountUtil.pointMultiply(account.getPrivateKey(),
                NodeConstant.getAccountPassword(), this.getPublicKey());
        connection.setMpPassword(mpPassword);
        SessionResponseMessage responseMessage = new SessionResponseMessage(messageId);
        responseMessage.setPublicKey(account.getPublicKey());
        responseMessage.setMpPassword(mpPassword);
        connection.send(responseMessage, future -> {
            connection.setOk(true);
        });
    }
}
