package com.codingapi.txlcn.protocol;


import com.codingapi.txlcn.protocol.message.Connection;
import com.codingapi.txlcn.protocol.message.separate.DataRequestMessage;
import com.codingapi.txlcn.protocol.message.separate.DataResponseMessage;
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
        DataResponseMessage out =  connection.request(requestMessage);
        System.out.println(out);
        for (; ; ) {
            Thread.sleep(100);
        }
    }
}
