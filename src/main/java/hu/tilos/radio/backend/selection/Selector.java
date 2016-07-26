package hu.tilos.radio.backend.selection;

import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.file.NewsElement;
import hu.tilos.radio.backend.file.NewsFile;

import java.util.List;

public interface Selector {

    List<NewsElement> selectFor(NewsBlock block, List<NewsFile> file);

}
