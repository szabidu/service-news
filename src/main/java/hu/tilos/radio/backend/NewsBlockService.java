package hu.tilos.radio.backend;

import hu.tilos.radio.backend.mongoconverters.ScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsBlockService {

    private Random random = new Random();

    private static final Logger LOG = LoggerFactory.getLogger(NewsBlockService.class);

    @Value("${news.workDir}")
    private String workDir;

    @Value("${news.inputDir}")
    private String inputDir;

    @Value("${news.outputDir}")
    private String outputDir;

    @Inject
    NewsFileService newsFileService;

    @Inject
    private NewsFileRepository newsFileRepository;

    @Inject
    private NewsBlockRepository newsBlockRepository;

    @Inject
    private ScriptExecutor scriptExecutor;

    public Path getInputDirPath() {
        return Paths.get(inputDir);
    }

    public Path getOutputDirPath() {
        return Paths.get(outputDir);
    }

    public List<NewsBlock> getBlocks(LocalDate date) {

        List<NewsBlock> scheduled = getScheduledBlocks(date);

        List<NewsBlock> persistent = getPersistentBlocks(date);

        persistMissing(scheduled, persistent);

        return getPersistentBlocks(date).stream().map(block -> block.findGeneratedFiled(getOutputDirPath())).collect(Collectors.toList());
    }

    private void persistMissing(List<NewsBlock> scheduled, List<NewsBlock> persistent) {
        List<String> collect = scheduled.stream().map(NewsBlock::getName).collect(Collectors.toList());
        for (NewsBlock persistentBlock : persistent) {
            if (collect.contains(persistentBlock.getName())) {
                scheduled = scheduled.stream().filter(block -> !block.getName().equals(persistentBlock.getName())).collect(Collectors.toList());
            }
        }

        LocalDate now = LocalDate.now();
        for (NewsBlock missing : scheduled) {
            newsBlockRepository.save(missing);
        }
    }

    private List<NewsBlock> getPersistentBlocks(LocalDate date) {
        return newsBlockRepository.findByDateBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    private List<NewsBlock> getScheduledBlocks(LocalDate date) {
        List<NewsBlock> result = new ArrayList<>();

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            result.add(new NewsBlock("reggel", date.atTime(8, 55), 11 * 60 + 30));
            result.add(new NewsBlock("este", date.atTime(20, 0), 11 * 60 + 30));
        } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            result.add(new NewsBlock("reggel", date.atTime(8, 50), 11 * 60 + 30));
            result.add(new NewsBlock("este", date.atTime(20, 0), 11 * 60 + 30));
        } else if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            result.add(new NewsBlock("orankenti1", date.atTime(5, 0), 3));
            result.add(new NewsBlock("reggel", date.atTime(6, 48), 9 * 60 + 2 * 3 * 60));
            result.add(new NewsBlock("orankenti2", date.atTime(8, 0), 3 * 60));
            result.add(new NewsBlock("orankenti3", date.atTime(9, 57), 6 * 60));
            result.add(new NewsBlock("orankenti4", date.atTime(11, 57), 6 * 60));
            result.add(new NewsBlock("orankenti5", date.atTime(13, 28, 30), 3 * 60));
            result.add(new NewsBlock("orankenti6", date.atTime(14, 57, 30), 6 * 60));
            result.add(new NewsBlock("orankenti7", date.atTime(16, 28, 30), 3 * 60));
            result.add(new NewsBlock("orankenti8", date.atTime(17, 30), 3 * 60));
            result.add(new NewsBlock("orankenti9", date.atTime(18, 30), 3 * 60));
            result.add(new NewsBlock("este", date.atTime(19, 48), 12 * 60 + +2 * 3 * 30));
            result.add(new NewsBlock("orankenti10", date.atTime(21, 00), 3 * 60));
            result.add(new NewsBlock("orankenti11", date.atTime(22, 00), 3 * 60));
        } else if (date.getDayOfWeek() == DayOfWeek.TUESDAY) {
            result.add(new NewsBlock("orankenti1", date.atTime(5, 0), 3));
            result.add(new NewsBlock("reggel", date.atTime(6, 48), 9 * 60 + 2 * 3 * 60));
            result.add(new NewsBlock("orankenti2", date.atTime(8, 0), 3 * 60));
            result.add(new NewsBlock("orankenti3", date.atTime(9, 57), 6 * 60));
            result.add(new NewsBlock("orankenti4", date.atTime(11, 57), 6 * 60));
            result.add(new NewsBlock("orankenti5", date.atTime(13, 57, 0), 6 * 60));
            result.add(new NewsBlock("orankenti6", date.atTime(14, 57, 30), 6 * 60));
            result.add(new NewsBlock("orankenti7", date.atTime(16, 28, 30), 3 * 60));
            result.add(new NewsBlock("orankenti8", date.atTime(17, 57), 6 * 60));
            result.add(new NewsBlock("este", date.atTime(19, 48), 12 * 60 + +2 * 3 * 30));
            result.add(new NewsBlock("orankenti10", date.atTime(21, 00), 3 * 60));
            result.add(new NewsBlock("orankenti11", date.atTime(22, 00), 3 * 60));
        } else {
            result.add(new NewsBlock("reggel", date.atTime(6, 48), 11 * 60 + 30));
            result.add(new NewsBlock("este", date.atTime(19, 48), 11 * 60 + 30));
            result.add(new NewsBlock("rovid-5", date.atTime(6, 0), 3 * 60));
            result.add(new NewsBlock("rovid-6", date.atTime(6, 0), 3 * 60));
            for (int i = 8; i < 19; i++) {
                result.add(new NewsBlock("rovid-" + i, date.atTime(i, 0), 3 * 60));
            }
            for (int i = 20; i < 23; i++) {
                result.add(new NewsBlock("rovid-" + i, date.atTime(i, 0), 3 * 60));
            }
        }
        return result;

    }


    public List<NewsBlock> getTodo() {
        LocalDateTime now = LocalDateTime.now();
        List<NewsBlock> scheduledBlocks = getBlocks(LocalDate.now());
        return scheduledBlocks.stream().filter(block -> {
            long untilThat = now.until(block.getDate(), ChronoUnit.MINUTES);
            return block.getFiles().size() == 0 && untilThat > 0 && untilThat < 120;
        }).collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void prepare() {
        LOG.debug("Checking new files to generate");
        LocalDateTime now = LocalDateTime.now();
        List<NewsBlock> scheduledBlocks = getBlocks(LocalDate.now());
        scheduledBlocks.forEach(block -> {
            long untilThat = now.until(block.getDate(), ChronoUnit.MINUTES);
            if (untilThat > 0 && untilThat < 120 && block.getPath() == null) {
                generate(block);
                LOG.info("Mp3 file is generated for " + block.getDate() + "/" + block.getName());
            } else if (block.getFiles().size() == 0 && untilThat >= 120 && untilThat < 240) {
                drawFiles(block);
                newsBlockRepository.save(block);
                LOG.info("News files are selected for " + block.getDate() + "/" + block.getName());
            }
        });

    }

    private void drawFiles(NewsBlock block) {
        selectFiles(block, newsFileService.getFiles());
    }

    private void selectFiles(NewsBlock block, List<NewsFile> files) {
        List<NewsFile> selectedFiles = new ArrayList<>();
        List<NewsFile> availableFiles = files.stream().filter(file -> file.getExpiration().isAfter(block.getDate())).collect(Collectors.toList());
        while (durationOf(selectedFiles) + 2 * 20 + (selectedFiles.size() * 3) < block.getExpectedDuration()) {
            NewsFile one = pickOne(availableFiles);
            if (one != null) {
                selectedFiles.add(one);
                availableFiles.remove(one);
            } else {
                break;
            }

        }
        block.getFiles().clear();
        sort(selectedFiles).forEach(block::addFile);


    }

    private int durationOf(List<NewsFile> selectedFiles) {
        return selectedFiles.stream().mapToInt(NewsFile::getDuration).sum();
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

    private NewsFile pickOne(List<NewsFile> files) {
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

    public NewsBlock draw(LocalDate date, String name) {
        NewsBlock block = newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFiled(getOutputDirPath());
        if (block != null) {
            selectFiles(block, newsFileService.getFiles());
            if (block.getPath() != null) {
                try {
                    Files.delete(getOutputDirPath().resolve(block.getPath()));
                } catch (IOException e) {
                    LOG.error("Can't delete file", e);
                }
            }
        }
        newsBlockRepository.save(block);
        return getBlock(date, name);
    }

    public NewsBlock getBlock(LocalDate date, String name) {
        return newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFiled(getOutputDirPath());
    }

    public String getGenerateScript(LocalDate date, String name) {
        return getGenerateScript(getBlock(date, name));
    }

    public String getGenerateScript(NewsBlock block) {
        Path destinationFilePath = getOutputDirPath().resolve(block.createDestinationPath());

        StringBuilder b = new StringBuilder();
        b.append("#!/bin/bash\n" +
                "set -e\n" +
                "export TMPDIR=./tmp\n" +
                "rm -rf $TMPDIR\n" +
                "mkdir -p $TMPDIR\n" +
                "export SIGNALDIR=./signal\n");
        b.append("sox $SIGNALDIR/silence6.wav $TMPDIR/temp.wav\n");
        b.append("mv $TMPDIR/temp.wav $TMPDIR/hirekeddig.wav\n");
        String lastCategory = null;
        for (NewsFile file : block.getFiles()) {
            if (lastCategory == null || !lastCategory.equals(file.getCategory())) {
                Path signalPath = Paths.get(workDir, "signal", file.getCategory() + ".wav");
                if (Files.exists(signalPath)) {
                    b.append("sox $TMPDIR/hirekeddig.wav \"" + signalPath + "\" $SIGNALDIR/silence3.wav $TMPDIR/temp.wav\n");
                    b.append("mv $TMPDIR/temp.wav $TMPDIR/hirekeddig.wav\n");
                }
            }
            b.append("sox $TMPDIR/hirekeddig.wav \"" + getInputDirPath().resolve(file.getPath()) + "\" $SIGNALDIR/silence3.wav $TMPDIR/temp.wav\n");
            b.append("mv $TMPDIR/temp.wav $TMPDIR/hirekeddig.wav\n");
            lastCategory = file.getCategory();
        }
        b.append("sox $TMPDIR/hirekeddig.wav $SIGNALDIR/silence3.wav $TMPDIR/hireketmondunk.wav\n" +
                "sox $SIGNALDIR/hirekzene.mp3 $TMPDIR/zenemost.wav trim 0 $(soxi -s $TMPDIR/hireketmondunk.wav)s \n" +
                "sox -m $TMPDIR/zenemost.wav $TMPDIR/hireketmondunk.wav $TMPDIR/zeneshirek.wav\n" +
                "sox $SIGNALDIR/Hirek_intro.wav $TMPDIR/zeneshirek.wav $TMPDIR/hirek_eleje.wav splice -q $(soxi -D $SIGNALDIR/Hirek_intro.wav),2\n" +
                "mkdir -p " + destinationFilePath.getParent() + "\n" +
                "sox $TMPDIR/hirek_eleje.wav $SIGNALDIR/Hirek_outro.wav " + destinationFilePath + " splice -q $(soxi -D $TMPDIR/hirek_eleje.wav),2\n");
        return b.toString();
    }


    public NewsBlock generate(LocalDate localDate, String name) {
        NewsBlock block = getBlock(localDate, name);
        generate(block);
        return getBlock(localDate, name);
    }

    public void generate(NewsBlock block) {
        if (block.getFiles().isEmpty()) {
            selectFiles(block, newsFileService.getFiles());
            newsBlockRepository.save(block);
        }
        String generateScript = getGenerateScript(block);
        scriptExecutor.executeScript(generateScript, workDir, "combine");
    }


    public void deleteDay(LocalDate date) {
        for (NewsBlock block : getBlocks(date)) {
            if (block.getPath() != null) {
                try {
                    Files.delete(getOutputDirPath().resolve(block.getPath()));
                } catch (IOException e) {
                    LOG.error("Can't detele file: " + block.getPath(), e);
                }
            }
            newsBlockRepository.delete(block);
        }

    }
}
