package hu.tilos.radio.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadService {

    @Inject
    NewsFileRepository newsFileRepository;

    @Value("${news.importDir}")
    private String importDir;

    @Inject
    NewsFileService newsFileService;

    public void upload(String category, MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), Paths.get(importDir).resolve(category).resolve(file.getOriginalFilename()));
            newsFileService.importNewFiles();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
