package com.irallyin.server.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.irallyin.server", exclude = {MongoAutoConfiguration.class})
public class IRallyInWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(IRallyInWebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(IRallyInWebApplication.class);
    }
}
