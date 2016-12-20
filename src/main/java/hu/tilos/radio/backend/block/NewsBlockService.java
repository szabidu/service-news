package hu.tilos.radio.backend.block;

import hu.tilos.radio.backend.Scheduler;
import hu.tilos.radio.backend.block.transition.DrawFiles;
import hu.tilos.radio.backend.block.transition.GenerateFile;
import hu.tilos.radio.backend.block.transition.ToInitial;
import hu.tilos.radio.backend.file.NewsElement;
import hu.tilos.radio.backend.file.NewsFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsBlockService {


    private static final Logger LOG = LoggerFactory.getLogger(NewsBlockService.class);

    @Value("${news.outputDir}")
    private String outputDir;

    @Inject
    NewsFileService newsFileService;

    @Inject
    private NewsBlockRepository newsBlockRepository;

    @Inject
    private Scheduler scheduler;

    @Inject
    DrawFiles fileDrawer;

    @Inject
    GenerateFile fileGenerator;

    @Inject
    ToInitial toInitial;

    public Path getOutputDirPath() {
        return Paths.get(outputDir);
    }

    public List<NewsBlock> getBlocks(LocalDate date) {

        List<NewsBlock> scheduled = scheduler.getScheduledBlocks(date);

        List<NewsBlock> persistent = getPersistentBlocks(date);

        persistMissing(scheduled, persistent);

        return getPersistentBlocks(date).stream().map(block -> block.findGeneratedFile(getOutputDirPath())).collect(Collectors.toList());
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

    @Scheduled(cron = "0 0 3 * * *")
    public void todayDrawer() {
        prepare();
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void todayGenerator() {
        prepare();
    }

    public void prepare() {
        LOG.debug("Checking new files to generate");
        LocalDateTime now = LocalDateTime.now();
        List<NewsBlock> scheduledBlocks = getBlocks(LocalDate.now());
        scheduledBlocks.forEach(block -> {
            long untilThat = now.until(block.getDate(), ChronoUnit.MINUTES);
            if (!block.isHandmade() && untilThat > 0 && block.getPath() == null) {
                if (block.getFiles().size() == 0) {
                    block = fileDrawer.process(block);
                }
                block = fileGenerator.process(block);
                newsBlockRepository.save(block);
                LOG.info("Mp3 file is generated for " + block.getDate() + "/" + block.getName());
            } else if (!block.isHandmade() && block.getFiles().size() == 0) {
                block = fileDrawer.process(block);
                newsBlockRepository.save(block);
                LOG.info("News files are selected for " + block.getDate() + "/" + block.getName());
            }
        });

    }


    public NewsBlock draw(LocalDate date, String name) {
        NewsBlock block = newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFile(getOutputDirPath());
        if (block != null) {
            block = toInitial.process(block);
            block = fileDrawer.process(block);
        }
        newsBlockRepository.save(block);
        return getBlock(date, name);
    }


    public NewsBlock getBlock(LocalDate date, String name) {
        return newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFile(getOutputDirPath());
    }

    public NewsBlock registerPlay(LocalDate date, String name) {
        NewsBlock block = newsBlockRepository.findOneByDateBetweenAndName(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), name).findGeneratedFile(getOutputDirPath());
        block.getLiveAt().add(LocalDateTime.now());
        newsBlockRepository.save(block);
        return getBlock(date, name);
    }


    public NewsBlock generate(LocalDate localDate, String name) {
        NewsBlock block = getBlock(localDate, name);
        if (block.getFiles().size() == 0) {
            block = fileDrawer.process(block);
        }
        fileGenerator.process(block);
        return getBlock(localDate, name);
    }

    public NewsBlock update(String id, NewsBlockToSave newsBlockToSave) {
        NewsBlock block = newsBlockRepository.findOne(id);
        if (!block.wasLive()) {
            block = toInitial.process(block);
            for (NewsElement file : newsBlockToSave.getFiles()) {
                if (file.getId() != null) {
                    block.addFile(NewsElement.from(newsFileService.get(file.getId())));
                } else {
                    block.addFile(file);
                }
            }
            newsBlockRepository.save(block);
        } else {
            throw new IllegalArgumentException("Nem lehet módosítani olyan blokkot, ami már lement");
        }
        return newsBlockRepository.findOne(id);
    }


    public String getWeeklyReport() {

        LocalDateTime start = LocalDateTime.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY.MONDAY)).minusDays(7);
        LocalDateTime end = start.plusDays(7);

        List<NewsBlock> byDateBetween = newsBlockRepository.findByDateBetween(start, end);
        StringBuilder b = new StringBuilder();
        for (NewsBlock block : byDateBetween) {
            if (block.wasLive()) {
                for (LocalDateTime time : block.getLiveAt()) {
                    b.append(block.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    b.append(";");
                    b.append(time.format(DateTimeFormatter.ISO_TIME));
                    b.append(";");
                    b.append(time.plusSeconds(block.getExpectedDuration()).format(DateTimeFormatter.ISO_TIME));
                    b.append(";");
                    b.append(block.getName());
                    b.append(";");
                    b.append("\n");
                }
            }
        }

        return b.toString();
    }

    public void deleteDay(LocalDate date) {
        LocalDate d = date;
        for (int i = 0; i < 30; i++) {
            getBlocks(date).stream().filter(block -> !block.wasLive()).forEach(block -> {
                if (block.getPath() != null) {
                    deleteGeneratedFile(block, getOutputDirPath().resolve(block.getPath()), "Can't delete file: " + block.getPath());
                }
                newsBlockRepository.delete(block);
            });
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


    public void upload(String id, MultipartFile file) {
        NewsBlock block = newsBlockRepository.findOne(id);
        if (block == null) {
            throw new IllegalArgumentException("No such news block" + id);
        }
        try {
            block = toInitial.process(block);
            Path destinationPath = getOutputDirPath().resolve(block.createDestinationPath());
            Files.createDirectories(destinationPath.getParent());
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            block.setHandmade(true);
            newsBlockRepository.save(block);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
