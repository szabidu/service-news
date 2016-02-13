package hu.tilos.radio.backend.selection;

import hu.tilos.radio.backend.NewsBlock;
import hu.tilos.radio.backend.NewsFile;

import java.util.List;

public interface Selector {
    public List<NewsFile> selectFor(NewsBlock block, List<NewsFile> file);


}
