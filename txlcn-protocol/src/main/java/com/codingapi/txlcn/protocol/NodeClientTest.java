package com.codingapi.txlcn.protocol;


import cn.hyperchain.sdk.account.Account;
import cn.hyperchain.sdk.account.Algo;
import com.codingapi.txlcn.protocol.config.NodeConstant;
import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.separate.DataRequestMessage;
import com.codingapi.txlcn.protocol.message.separate.DataResponseMessage;
import com.codingapi.txlcn.protocol.message.session.SessionRequestMessage;
import com.codingapi.txlcn.protocol.message.session.SessionResponseMessage;
import com.codingapi.txlcn.protocol.util.AccountUtil;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


/**
 * @author sunligang
 */
@Slf4j
public class NodeClientTest {

    @Test
    public void testV1() throws InterruptedException, ExecutionException, TimeoutException {


        NodeClient client = new NodeClient(null);
        Connection connection = NodeClient.getConnect(EndPoint.builder().ip("127.0.0.1").port(8888).build());
        DataRequestMessage requestMessage = new DataRequestMessage();
        requestMessage.setKey("123");
        DataResponseMessage out = connection.request(requestMessage);
        System.out.println(out);
        for (; ; ) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testBuildConnect() throws InterruptedException, ExecutionException, TimeoutException {
        NodeClient client = new NodeClient(null);
        Connection connection = NodeClient.getConnect(EndPoint.builder().ip("127.0.0.1").port(8888).build());
        Account account = AccountUtil.genAccount(Algo.SMRAW, "123");
        NodeConstant.setNodeAccount(account);
        System.out.println("aaa" + connection.getUniqueKey());
        NodeConstant.setAccountPassword("123");
        SessionRequestMessage requestMessage = new SessionRequestMessage();
        requestMessage.setPublicKey(account.getPublicKey());

        SessionResponseMessage responseMessage = connection.request(requestMessage, future -> {
                    System.out.println("完成");
                }
        );

        System.out.println(responseMessage);

        Thread.sleep(100);

        System.out.println(connection.isOk());

        DataRequestMessage requestMessage2 = new DataRequestMessage();
        requestMessage2.setKey("123");
        DataResponseMessage out = connection.request(requestMessage2);
        System.out.println(out);

        for (; ; ) {
            Thread.sleep(100);
        }
    }
}
