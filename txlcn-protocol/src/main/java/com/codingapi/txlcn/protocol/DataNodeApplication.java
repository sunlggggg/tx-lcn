package com.codingapi.txlcn.protocol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.crypto.Data;


/**
 * @author sunligang
 */
@SpringBootApplication
public class DataNodeApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DataNodeApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
