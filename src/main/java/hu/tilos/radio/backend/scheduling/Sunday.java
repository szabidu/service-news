package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Sunday implements BaseScheduling {
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        return new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)
                .doubleBlock(7)
                .addShort("3perces-8-plus3", LocalTime.of(8, 54), 6)
                .doubleBlock(10)
                .addShort("3perces-11-pot", LocalTime.of(11, 30), 3)
                .addShort("3perces-12-pot", LocalTime.of(12, 30), 3)
                .addShort("3perces-13-pot", LocalTime.of(13, 27), 3)
                .doubleBlock(15)
                .singleBlockAt(16)
                .singleBlockAt(17)
                .addBlock("este", LocalTime.of(18, 24), 12)
                .doubleBlock(20)
                .singleBlockSymmetric(21, 57)
                .addShort("3perces-22-pot", LocalTime.of(22, 30), 3)
                .build();

    }
}
