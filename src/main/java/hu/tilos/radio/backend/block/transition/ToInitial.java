package hu.tilos.radio.backend.block.transition;

import hu.tilos.radio.backend.block.NewsBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ToInitial implements StateTransition {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToInitial.class);

    @Value("${news.outputDir}")
    private String outputDir;

    @Override
    public NewsBlock process(NewsBlock original) {
        deleteGeneratedFile(original);
        original.setHandmade(false);
        original.clearFiles();
        original.setPath("");
        return original;
    }

    private void deleteGeneratedFile(NewsBlock block) {
        if (block.getPath() != null && block.getPath().length() > 0) {
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
