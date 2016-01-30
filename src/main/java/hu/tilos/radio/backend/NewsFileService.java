package hu.tilos.radio.backend;

import hu.tilos.radio.backend.mongoconverters.ScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.*;
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

    @Value("${news.importDir}")
    private String importDir;

    @Value("${news.inputDir}")
    private String inputDir;

    @Inject
    private NewsFileRepository newsFileRepository;

    @Inject
    private NewsBlockRepository newsBlockRepository;

    @Inject
    private ScriptExecutor scriptExecutor;


    public synchronized void importNewFiles(){
        LocalDateTime latest = getLatestFileRecordDate();
        List<Path> files = checkNewFiles(getImportPath(), latest);
        storeFiles(files);
    }
    public synchronized List<NewsFile> getFiles() {
        List<NewsFile> allFiles = getRecentFiles();
        cleanup(allFiles);
        return getRecentFiles();
    }

    private void cleanup(List<NewsFile> recentFiles) {
        recentFiles.forEach(newsFile -> {
            if (!Files.exists(getInputPath().resolve(newsFile.getPath()))) {
                newsFileRepository.delete(recentFiles);
            }
        });
    }

    private void storeFiles(List<Path> files) {
        for (Path file : files) {
            try {

                copyAndNormalize(file);

                NewsFile newsFile = new NewsFile();
                newsFile.setPath(file.toString());
                newsFile.setDuration(calculateDuration(file));
                newsFile.setCreated(getCreationDate(getInputPath().resolve(file)));
                newsFile.setCategory(file.iterator().next().toString());
                newsFile.setExpiration(newsFile.getCreated().plusDays(detectExpiration(newsFile.getPath().toString())));
                newsFileRepository.save(newsFile);
            } catch (Exception ex) {
                LOG.error("Can't persist " + file, ex);
            }
        }
    }

    private void copyAndNormalize(Path file) throws IOException {
        String fileName = file.getFileName().toString();
        String name = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        Path sourcePath = getImportPath().resolve(file);
        Path destinationPath = getInputPath().resolve(file);
        Path destinationDirPath = destinationPath.getParent();
        Files.createDirectories(destinationDirPath);
        Path tmp1Path = destinationDirPath.resolve(name + ".tmp1." + extension);
        Path tmp2Path = destinationDirPath.resolve(name + ".tmp2." + extension);
        String script = "#!/bin/bash\n" +
                "set -e\n" +
                "sox \"" + sourcePath + "\" -c 2 \"" + tmp1Path + "\"\n" +
                "sox \"" + tmp1Path + "\" \"" + tmp2Path + "\" silence 1 0.1 0.1% reverse silence 1 0.1 0.1% reverse\n" +
                "sox --norm \"" + tmp2Path + "\" \"" + destinationPath + "\"";
        scriptExecutor.executeScript(script, "/tmp", "fileimport");

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
        ProcessBuilder pb = new ProcessBuilder("soxi", "-D", getInputPath().resolve(file).toString());
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
            BasicFileAttributes attr = Files.readAttributes(getInputPath().resolve(file), BasicFileAttributes.class);
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
                    files.add(getImportPath().relativize(entry));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOG.info("found " + files.size() + " new file");
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


    public Path getImportPath() {
        return Paths.get(importDir);
    }

    public Path getInputPath() {
        return Paths.get(inputDir);
    }
}
