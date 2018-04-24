package hu.tilos.radio.backend.selection;

import hu.tilos.radio.backend.NewsSignal;
import hu.tilos.radio.backend.NewsSignalService;
import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.block.transition.GenerateFile;
import hu.tilos.radio.backend.file.FileDuration;
import hu.tilos.radio.backend.file.NewsElement;
import hu.tilos.radio.backend.file.NewsFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DefaultSelector implements Selector {

    protected Random random = new Random();

    @Inject
    private NewsSignalService signalService;
    @Inject
    FileDuration fileDuration;

    @Value("${news.inputDir}")
    private String inputDir;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSelector.class);

    public boolean filter(NewsFile file) {
        return true;
    }

    @Override
    public List<NewsElement> selectFor(NewsBlock block, List<NewsFile> files) {
        files = files.stream().filter(file -> filter(file)).collect(Collectors.toList());
        List<NewsElement> selectedFiles = new ArrayList<>();

        NewsSignal randomSignal = signalService.getRandomSignal();
        selectedFiles.add(randomSignal.getIntro());
        selectedFiles.add(randomSignal.getOutro());
        selectedFiles.add(randomSignal.getLoop());


        List<NewsFile> availableFiles = files.stream().filter(file -> {
            return file.getExpiration().isAfter(block.getDate()) && (file.getValidFrom() == null || file.getValidFrom().isBefore(block.getDate()));
        }).collect(Collectors.toList());

        Set<String> categories = new HashSet<>();

        while (GenerateFile.estimateDuration(selectedFiles) < block.getExpectedDuration()) {
            NewsFile one = pickOne(availableFiles);
            if (one != null) {
                selectedFiles.add(NewsElement.from(one));
                availableFiles.remove(one);
                if (!categories.contains(one.getCategory())) {
                    addCategoryTitle(one, selectedFiles);
                }
                categories.add(one.getCategory());
            } else {
                break;
            }

        }
        try {

            Random randomizer = new Random();
            int randomFileIndex;
            List<NewsElement> filesBefore = getMiscFiles("before");
            if (filesBefore.size() > 0) {
            randomFileIndex = randomizer.nextInt(filesBefore.size()-1);
            selectedFiles.add(filesBefore.get(randomFileIndex)); 
            }

            List<NewsElement> filesAfter = getMiscFiles("after");
            if (filesAfter.size() > 0) {
            randomFileIndex = randomizer.nextInt(filesAfter.size()-1);
            selectedFiles.add(filesAfter.get(randomFileIndex)); 
}

        } catch (IOException e) {
            LOGGER.error("Can't list before or after files files", e);
        }
        block.clearFiles();
        return selectedFiles;
    }

    private List<NewsElement> getMiscFiles(String directoryName) throws IOException {
        Path directory = Paths.get(inputDir).resolve(directoryName);
        try (Stream<Path> files = Files.list(directory)) {
            return files.map(path -> NewsElement.from(Paths.get(inputDir).relativize(path), "news_" + directoryName, fileDuration.calculate(path))).collect(Collectors.toList());
        }

    }

    protected void addCategoryTitle(NewsFile one, List<NewsElement> selectedFiles) {
        Path titleRelativePath = Paths.get("title").resolve(one.getCategory() + ".wav");
        Path titlePath = Paths.get(inputDir).resolve(titleRelativePath);
        if (Files.exists(titlePath)) {
            selectedFiles.add(new NewsElement(null, titleRelativePath.toString(), "cim", fileDuration.calculate(titlePath)));
        } else {
            LOGGER.warn("Can't find the title file for category {} at {}", one.getCategory(), titlePath.toAbsolutePath().toString());
        }

    }


    public NewsFile pickOne(List<NewsFile> files) {
        if (files.size() < 1) {
            return null;
        } else {
            List<NewsFile> importantList = files.stream().filter(file -> file.getCategory().equals("fontos")).collect(Collectors.toList());
            if (!importantList.isEmpty()) {
                return importantList.get(random.nextInt(importantList.size()));
            } else {
                files.sort((a, b) -> a.getCreated().compareTo(b.getCreated()) * -1);
                if (files.size() > 0) {
                    return files.get(0);
                } else {
                    return null;
                }
            }
        }
    }

}
