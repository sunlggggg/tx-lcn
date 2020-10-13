package com.codingapi.txlcn.protocol.message.session;

import cn.hyperchain.sdk.account.Account;
import com.codingapi.txlcn.protocol.Protocoler;
import com.codingapi.txlcn.protocol.config.NodeConstant;
import com.codingapi.txlcn.protocol.handler.SessionUtil;
import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.separate.AbsMessage;
import com.codingapi.txlcn.protocol.util.AccountUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.util.Objects;


/**
 * @author sunligang
 */
@Data
@NoArgsConstructor
public class SessionResponseMessage extends AbsMessage {

    private String publicKey;
    private String mpPassword;

    public SessionResponseMessage(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public void handle(ApplicationContext springContext,
                       Protocoler protocoler,
                       Connection connection) throws Exception {
        Account account = NodeConstant.getNodeAccount();
        String mpPassword = AccountUtil.pointMultiply(account.getPrivateKey(),
                NodeConstant.getAccountPassword(), this.getPublicKey());
        if (Objects.equals(this.mpPassword, mpPassword)) {
            connection.setMpPassword(mpPassword);
            connection.setOk(true);
        }
        super.handle(springContext, protocoler, connection);
    }

}
