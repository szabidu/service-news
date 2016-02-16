package hu.tilos.radio.backend.block;

import hu.tilos.radio.backend.block.NewsBlock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsBlockRepository extends MongoRepository<NewsBlock, String> {
    public List<NewsBlock> findByDateBetween(LocalDateTime from, LocalDateTime to);

    public NewsBlock findOneByDateBetweenAndName(LocalDateTime from, LocalDateTime to, String name);

}
