package hu.tilos.radio.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@EnableWebSecurity
@Configuration
public class NewsStarter {

    private static final Logger LOG = LoggerFactory.getLogger(NewsStarter.class);

    public static void main(String[] args) {
        SpringApplication.run(NewsStarter.class, args);

    }

}
