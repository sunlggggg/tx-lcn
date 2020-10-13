package com.codingapi.txlcn.protocol.config;

import cn.hyperchain.sdk.account.Account;

/**
 * @author xqw
 * @description: 数据节点配置
 * @date 2020/9/17
 */
public class NodeConstant {

    private static Account nodeAccount;

    private static String accountPassword;

    public static Account getNodeAccount() {
        return nodeAccount;
    }

    public static void setNodeAccount(Account nodeAccount) {
        if (nodeAccount != null) {
            NodeConstant.nodeAccount = nodeAccount;
        }
    }

    public static String getAccountPassword() {
        return accountPassword;
    }

    public static void setAccountPassword(String accountPassword) {
        NodeConstant.accountPassword = accountPassword;
    }
}
