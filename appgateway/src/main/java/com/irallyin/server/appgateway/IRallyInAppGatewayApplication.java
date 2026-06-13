package com.irallyin.server.appgateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.nio.file.Paths;
import java.util.Arrays;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.irallyin.server", exclude = {MongoAutoConfiguration.class})
public class IRallyInAppGatewayApplication extends SpringBootServletInitializer {

    private final Environment environment;

    public IRallyInAppGatewayApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(IRallyInAppGatewayApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(IRallyInAppGatewayApplication.class);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logRuntimeInfo() {
        log.info("IRallyIn appgateway started. activeProfiles={}, userDir={}, loggingFilePath={}",
                Arrays.toString(environment.getActiveProfiles()),
                Paths.get("").toAbsolutePath(),
                environment.getProperty("logging.file.path"));
    }
}
