package hu.tilos.radio.backend.block;

import hu.tilos.radio.backend.file.NewsFileService;
import hu.tilos.radio.backend.NewsSignalService;
import hu.tilos.radio.backend.Scheduler;
import hu.tilos.radio.backend.file.NewsFile;
import hu.tilos.radio.backend.mongoconverters.ScriptExecutor;
import hu.tilos.radio.backend.selection.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsBlockService {


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
    private NewsBlockRepository newsBlockRepository;

    @Inject
    private ScriptExecutor scriptExecutor;

    @Inject
    private Scheduler scheduler;

    @Inject
    private List<Selector> selectors;

    @Inject
    private NewsSignalService signalService;


    public Path getInputDirPath() {
        return Paths.get(inputDir);
    }

    public Path getOutputDirPath() {
        return Paths.get(outputDir);
    }

    public List<NewsBlock> getBlocks(LocalDate date) {

        List<NewsBlock> scheduled = scheduler.getScheduledBlocks(date);

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
            if (untilThat > 0 && untilThat < 6 * 60 && block.getPath() == null) {
                generate(block);
                LOG.info("Mp3 file is generated for " + block.getDate() + "/" + block.getName());
            } else if (block.getFiles().size() == 0 && untilThat >= 6 * 60 && untilThat < 8 * 60) {
                drawFiles(block);
                newsBlockRepository.save(block);
                LOG.info("News files are selected for " + block.getDate() + "/" + block.getName());
            }
        });

    }

    public void drawFiles(NewsBlock block) {
        List<NewsFile> files = newsFileService.getFiles();
        final String selection = block.getSelection() == null ? "default" : block.getSelection();
        Selector selector1 = selectors.stream().filter(selector -> selector.getClass().getSimpleName().toLowerCase().replace("selector", "").equals(selection)).findFirst().get();

        List<NewsFile> newsFiles = null;
        int minDuration = Integer.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            List<NewsFile> onePossibleSelection = selector1.selectFor(block, files);
            int duration = NewsFile.durationOf(onePossibleSelection);
            if (duration < minDuration) {
                newsFiles = onePossibleSelection;
                minDuration = duration;
            }
        }

        block.setFiles(newsFiles);
    }

    public NewsBlock draw(LocalDate date, String name) {
        NewsBlock block = newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFiled(getOutputDirPath());
        if (block != null) {
            drawFiles(block);
            deleteGeneratedFile(block);
        }
        newsBlockRepository.save(block);
        return getBlock(date, name);
    }

    private void deleteGeneratedFile(NewsBlock block) {
        if (block.getPath() != null) {
            try {
                Files.delete(getOutputDirPath().resolve(block.getPath()));
            } catch (IOException e) {
                LOG.error("Can't delete file", e);
            }
        }
    }

    public NewsBlock getBlock(LocalDate date, String name) {
        return newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFiled(getOutputDirPath());
    }

    public NewsBlock registerPlay(LocalDate date, String name) {
        NewsBlock block = newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFiled(getOutputDirPath());
        block.getLiveAt().add(LocalDateTime.now());
        newsBlockRepository.save(block);
        return getBlock(date, name);
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
            if ((lastCategory == null || !lastCategory.equals(file.getCategory())) && block.isWithSeparatorSignal()) {
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

        Path introPath = getSignalPath().resolve(signalService.getSignal(block.getSignalType()).getIntroPath());
        Path outroPath = getSignalPath().resolve(signalService.getSignal(block.getSignalType()).getOutroPath());

        b.append("sox $TMPDIR/hirekeddig.wav $SIGNALDIR/silence3.wav $TMPDIR/hireketmondunk.wav\n" +
                "sox " + getSignalPath().resolve(block.getBackgroundPath()) + " $TMPDIR/zenemost.wav trim 0 $(soxi -s $TMPDIR/hireketmondunk.wav)s \n" +
                "sox -m $TMPDIR/zenemost.wav $TMPDIR/hireketmondunk.wav $TMPDIR/zeneshirek.wav\n" +
                "sox " + introPath + " $TMPDIR/zeneshirek.wav $TMPDIR/hirek_eleje.wav splice -q $(soxi -D " + introPath + "),2\n" +
                "mkdir -p " + destinationFilePath.getParent() + "\n" +
                "sox $TMPDIR/hirek_eleje.wav " + outroPath + " " + destinationFilePath + " splice -q $(soxi -D $TMPDIR/hirek_eleje.wav),2\n");
        return b.toString();
    }


    public NewsBlock generate(LocalDate localDate, String name) {
        NewsBlock block = getBlock(localDate, name);
        generate(block);
        return getBlock(localDate, name);
    }

    public NewsBlock update(String id, NewsBlockToSave newsBlockToSave) {
        NewsBlock block = newsBlockRepository.findOne(id);
        deleteGeneratedFile(block);
        block.getFiles().clear();
        for (NewsFileReference file : newsBlockToSave.getFiles()) {
            block.getFiles().add(newsFileService.get(file.getId()));
        }
        block.setPath("");
        newsBlockRepository.save(block);

        return newsBlockRepository.findOne(id);
    }

    public void generate(NewsBlock block) {
        if (block.getFiles().isEmpty()) {
            drawFiles(block);
            newsBlockRepository.save(block);
        }
        String generateScript = getGenerateScript(block);
        scriptExecutor.executeScript(generateScript, workDir, "combine");
    }


    public void deleteDay(LocalDate date) {
        LocalDate d = date;
        for (int i = 0; i < 10; i++) {
            for (NewsBlock block : getBlocks(date)) {
                if (block.getLiveAt() == null || block.getLiveAt().size() == 0) {
                    deleteGeneratedFile(block, getOutputDirPath().resolve(block.getPath()), "Can't detele file: " + block.getPath());
                    newsBlockRepository.delete(block);
                }
            }
            d = d.plusDays(1);
        }
    }

    private void deleteGeneratedFile(NewsBlock block, Path resolve, String s) {
        if (block.getPath() != null) {
            try {
                Files.delete(resolve);
            } catch (IOException e) {
                LOG.error(s, e);
            }
        }
    }

    public Path getWorkDirPath() {
        return Paths.get(workDir);
    }

    public Path getSignalPath() {
        return Paths.get(workDir).resolve("signal");
    }

    public void setNewsFileService(NewsFileService newsFileService) {
        this.newsFileService = newsFileService;
    }

    public void setSignalService(NewsSignalService signalService) {
        this.signalService = signalService;
    }

    public NewsFileService getNewsFileService() {
        return newsFileService;
    }
}
