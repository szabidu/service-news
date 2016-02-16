package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;

import java.time.LocalDate;
import java.util.List;

public interface BaseScheduling {

    public List<NewsBlock> createBlocks(LocalDate date, boolean odd);

}
