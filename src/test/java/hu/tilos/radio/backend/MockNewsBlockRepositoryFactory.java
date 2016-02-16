package hu.tilos.radio.backend;

import hu.tilos.radio.backend.block.NewsBlockRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockNewsBlockRepositoryFactory {

    @Bean(autowire = Autowire.NO)
    public NewsBlockRepository createNewsBlockRepository() {
        return null;
    }
}
