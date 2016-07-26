package hu.tilos.radio.backend.block.transition;

import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.file.NewsElement;
import hu.tilos.radio.backend.file.NewsFile;
import hu.tilos.radio.backend.file.NewsFileService;
import hu.tilos.radio.backend.selection.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DrawFiles implements StateTransition {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrawFiles.class);

    @Value("${news.outputDir}")
    private String outputDir;

    @Inject
    NewsFileService newsFileService;

    @Inject
    private List<Selector> selectors;

    @Override
    public NewsBlock process(NewsBlock block) {
        List<NewsFile> files = newsFileService.getFiles();
        final String selection = block.getSelection() == null ? "default" : block.getSelection();
        Selector selector1 = selectors.stream().filter(selector -> selector.getClass().getSimpleName().toLowerCase().replace("selector", "").equals(selection)).findFirst().get();

        List<NewsElement> newsFiles = null;
        int minDuration = Integer.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            List<NewsElement> onePossibleSelection = selector1.selectFor(block, files);
            int duration = GenerateFile.estimateDuration(onePossibleSelection);
            if (duration < minDuration) {
                newsFiles = onePossibleSelection;
                minDuration = duration;
            }
        }

        block.addFiles(newsFiles);
        block.setHandmade(false);
        deleteGeneratedFile(block);
        return block;
    }

    private void deleteGeneratedFile(NewsBlock block) {
        if (block.getPath() != null && block.getPath().toString().length() > 0) {
            try {
                Files.delete(getOutputDirPath().resolve(block.getPath()));
            } catch (IOException e) {
                LOGGER.error("Can't delete file", e);
            }
        }
    }

    public Path getOutputDirPath() {
        return Paths.get(outputDir);
    }

}
