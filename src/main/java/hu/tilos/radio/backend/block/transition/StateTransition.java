package hu.tilos.radio.backend.block.transition;

import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.file.NewsFile;

public interface StateTransition {

    public NewsBlock process(NewsBlock original);
}
