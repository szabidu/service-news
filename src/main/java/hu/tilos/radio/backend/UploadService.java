package hu.tilos.radio.backend;

import hu.tilos.radio.backend.file.NewsFileRepository;
import hu.tilos.radio.backend.file.NewsFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
            Path destinationPath = Paths.get(importDir).resolve(category).resolve(file.getOriginalFilename());
            Files.createDirectories(destinationPath.getParent());
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            newsFileService.importNewFiles();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
