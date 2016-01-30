package hu.tilos.radio.backend;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;


public interface NewsFileRepository extends MongoRepository<NewsFile, String> {

    public NewsFile findOneByOrderByCreatedDesc();

}
