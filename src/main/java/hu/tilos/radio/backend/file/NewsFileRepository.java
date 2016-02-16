package hu.tilos.radio.backend.file;

import hu.tilos.radio.backend.file.NewsFile;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface NewsFileRepository extends MongoRepository<NewsFile, String> {

    public NewsFile findOneByOrderByCreatedDesc();

}
