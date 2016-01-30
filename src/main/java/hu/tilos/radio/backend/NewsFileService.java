package hu.tilos.radio.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NewsFileService {

    private static final Logger LOG = LoggerFactory.getLogger(NewsFileService.class);

    @Value("${news.inputDir}")
    private String root;

    @Inject
    private NewsFileRepository newsFileRepository;

    @Inject
    private NewsBlockRepository newsBlockRepository;
    private Path rootPath;

    public List<NewsFile> getFiles() {
        newsFileRepository.deleteAll();
        LocalDateTime latest = getLatestFileRecordDate();
        List<Path> files = checkNewFiles(getRootPath(), latest);
        storeFiles(files);
        List<NewsFile> allFiles = getRecentFiles();
        cleanup(allFiles);
        return getRecentFiles();
    }

    private void cleanup(List<NewsFile> recentFiles) {
        recentFiles.forEach(newsFile -> {
            if (!Files.exists(getRootPath().resolve(newsFile.getPath()))) {
                newsFileRepository.delete(recentFiles);
            }
        });
    }

    private void storeFiles(List<Path> files) {
        for (Path file : files) {
            try {
                NewsFile newsFile = new NewsFile();
                newsFile.setPath(file.toString());
                newsFile.setDuration(calculateDuration(file));
                newsFile.setCreated(getCreationDate(getRootPath().resolve(file)));
                newsFile.setCategory(file.iterator().next().toString());
                newsFile.setExpiration(newsFile.getCreated().plusDays(detectExpiration(newsFile.getPath().toString())));
                newsFileRepository.save(newsFile);
            } catch (Exception ex) {
                LOG.error("Can't persist " + file, ex);
            }
        }
    }

    private static Pattern expirationPattern = Pattern.compile(".*\\((\\d+)\\).*");

    public int detectExpiration(String fileName) {
        Matcher matcher = expirationPattern.matcher(fileName);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 1;
        }

    }

    private int calculateDuration(Path file) {
        ProcessBuilder pb = new ProcessBuilder("soxi", "-D", getRootPath().resolve(file).toString());
        try {
            Process start = pb.start();
            String result = new Scanner(start.getInputStream()).useDelimiter("//Z").next();
            start.waitFor();
            return Double.valueOf(result.trim()).intValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private LocalDateTime getCreationDate(Path file) {
        try {
            BasicFileAttributes attr = Files.readAttributes(getRootPath().resolve(file), BasicFileAttributes.class);
            return LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Path> checkNewFiles(Path dir, LocalDateTime fromDate) {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    files.addAll(checkNewFiles(entry, fromDate));
                } else if (getCreationDate(entry).isAfter(fromDate)) {
                    files.add(getRootPath().relativize(entry));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

    public LocalDateTime getLatestFileRecordDate() {
        NewsFile newsFile = newsFileRepository.findOneByOrderByCreatedDesc();
        if (newsFile != null) {
            return newsFile.getCreated();
        } else return LocalDateTime.MIN;
    }

    private List<NewsFile> getRecentFiles() {
        return newsFileRepository.findAll().stream().filter(block -> block.getExpiration().isAfter(LocalDateTime.now())).collect(Collectors.toList());
    }


    public Path getRootPath() {
        return Paths.get(root);
    }
}
