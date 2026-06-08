package com.irallyin.server.appgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.irallyin.server", exclude = {MongoAutoConfiguration.class})
public class IRallyInAppGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(IRallyInAppGatewayApplication.class, args);
    }
}
