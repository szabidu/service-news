package hu.tilos.radio.backend;

import hu.tilos.radio.backend.file.FileDuration;
import hu.tilos.radio.backend.file.NewsElement;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class NewsSignalService {

    protected Map<String, NewsSignal> signals = new HashMap<>();

    private Random random = new Random();

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(NewsSignalService.class);

    @Inject
    private FileDuration fileDuration;

    @Value("${news.inputDir}")
    private String inputDir;

    public NewsSignal getSignal(String id) {
        if (id == null) {
            return signals.get("zen");
        } else {
            return signals.get(id);
        }
    }

    @PostConstruct
    public void init() {
        try {

            Files.newDirectoryStream(getInputDir().resolve("signal"), Files::isDirectory).forEach(dir -> {
                Path intro = dir.resolve("intro.mp3");
                Path outro = dir.resolve("outro.mp3");
                Path loop = dir.resolve("loop.mp3");
                if (Files.exists(intro) && Files.exists(outro) && Files.exists(loop)) {
                    addSignal(dir.getFileName().toString(), intro, outro, loop);
                } else {
                    LOGGER.warn("One of the news signal elements are missing: " + dir);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Can't load signal", e);
        }
        LOGGER.info("Loaded {} signals: {}", signals.size(), signals.toString());
    }

    private void addSignal(String name, Path intro, Path outro, Path loop) {
        NewsSignal newsSignal = new NewsSignal();
        newsSignal.setIntro(NewsElement.from(getInputDir().relativize(intro), "news_intro", fileDuration.calculate(intro)));
        newsSignal.setOutro(NewsElement.from(getInputDir().relativize(outro), "news_outro", fileDuration.calculate(outro)));
        newsSignal.setLoop(NewsElement.from(getInputDir().relativize(loop), "news_loop", fileDuration.calculate(loop)));
        signals.put(name, newsSignal);
    }


    public NewsSignal getRandomSignal() {
        List<NewsSignal> signalList = new ArrayList(signals.values());
        return signalList.get(random.nextInt(signalList.size()));
    }

    public Path getInputDir() {
        return Paths.get(inputDir);
    }

    public Collection<NewsSignal> list() {
        return signals.values();
    }
}
