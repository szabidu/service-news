package hu.tilos.radio.backend;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockNewsFileRepositoryFactory {

    @Bean(autowire = Autowire.NO)
    public NewsFileRepository createNewsFileRepository() {
        return null;
    }
}
