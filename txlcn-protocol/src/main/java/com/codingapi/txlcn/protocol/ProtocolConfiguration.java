package com.codingapi.txlcn.protocol;

import cn.hyperchain.sdk.account.Account;
import cn.hyperchain.sdk.account.Algo;
import com.codingapi.txlcn.protocol.config.Config;
import com.codingapi.txlcn.protocol.config.NodeConstant;
import com.codingapi.txlcn.protocol.util.AccountUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lorne
 * @date 2020/3/4
 * @description
 */
@Configuration
public class ProtocolConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "txlcn.protocol")
    @ConditionalOnMissingBean
    public Config config() {
        return new Config();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtocolServer protocolServer(Config config, ApplicationContext applicationContext) {
        Account account = AccountUtil.genAccount(Algo.SMRAW, "123");
        NodeConstant.setNodeAccount(account);
        NodeConstant.setAccountPassword("123");
        return new ProtocolServer(config, applicationContext);
    }


}
