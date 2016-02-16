package hu.tilos.radio.backend.selection;

import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.file.NewsFile;
import hu.tilos.radio.backend.NewsSignalService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultSelector implements Selector {

    private Random random = new Random();

    @Inject
    private NewsSignalService signalService;

    public boolean filter(NewsFile file) {
        return true;
    }

    @Override
    public List<NewsFile> selectFor(NewsBlock block, List<NewsFile> files) {
        files = files.stream().filter(file -> filter(file)).collect(Collectors.toList());
        List<NewsFile> selectedFiles = new ArrayList<>();
        List<NewsFile> availableFiles = files.stream().filter(file -> {
            return file.getExpiration().isAfter(block.getDate()) && (file.getValidFrom() == null || file.getValidFrom().isBefore(block.getDate()));
        }).collect(Collectors.toList());
        while (NewsFile.durationOf(selectedFiles) + 2 * signalService.getSignal(block.getSignalType()).getSumLength() + (selectedFiles.size() * 3) < block.getExpectedDuration()) {
            NewsFile one = pickOne(availableFiles);
            if (one != null) {
                selectedFiles.add(one);
                availableFiles.remove(one);
            } else {
                break;
            }

        }
        block.getFiles().clear();
        return sort(selectedFiles);
    }


    public List<NewsFile> sort(List<NewsFile> files) {
        List<NewsFile> result = new ArrayList<>();
        result.addAll(files);
        Collections.sort(result, new Comparator<NewsFile>() {
            @Override
            public int compare(NewsFile o1, NewsFile o2) {
                return getWeight(o1.getCategory()).compareTo(getWeight(o2.getCategory()));
            }
        });
        return result;
    }

    public Integer getWeight(String category) {
        if (category.equals("fontos")) {
            return 0;
        } else if (category.equals("kozerdeku")) {
            return 1;
        } else if (category.equals("szines")) {
            return 300;
        } else if (category.equals("idojaras")) {
            return 301;
        } else {
            return Integer.valueOf((int) category.charAt(0));
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
                return files.get(random.nextInt(files.size()));
            }
        }
    }


}
