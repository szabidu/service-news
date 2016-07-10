package hu.tilos.radio.backend;

import hu.tilos.radio.backend.file.NewsFileService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockNewsFileServiceFactory {

    @Bean(autowire = Autowire.NO)
    public NewsFileService createNewsFileService() {
        return Mockito.mock(NewsFileService.class);
    }
}
