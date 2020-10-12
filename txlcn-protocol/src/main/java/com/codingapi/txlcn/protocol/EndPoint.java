package com.codingapi.txlcn.protocol;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author sunligang
 */
@Data
@Builder
@EqualsAndHashCode
public class EndPoint {
    private String ip;
    private Integer port;
}
