package hu.tilos.radio.backend.block;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsBlockRepository extends MongoRepository<NewsBlock, String> {

    List<NewsBlock> findByDateBetween(LocalDateTime from, LocalDateTime to);

    NewsBlock findOneByDateBetweenAndName(LocalDateTime from, LocalDateTime to, String name);

}
