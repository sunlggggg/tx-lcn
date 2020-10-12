package com.codingapi.txlcn.protocol;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

/**
 * @author sunligang
 */
@Configuration
public class Runner implements SmartLifecycle {

    @Autowired
    private ProtocolServer protocolServer;

    @SneakyThrows
    @Override
    public void start() {
        protocolServer.start();
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        //越小越先执行
        return 10;
    }
}
