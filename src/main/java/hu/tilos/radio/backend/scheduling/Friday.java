package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Friday implements BaseScheduling {
    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        return new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)
                .addBlock("este", LocalTime.of(19, 48), 12)
                .doubleBlock(7)
                .doubleBlockAt(8, 30)
                .doubleBlock(10, 30)
                .singleBlockSymmetric(12)
                .singleBlockSymmetric(13)
                .doubleBlockSymmetric(15)
                .doubleBlock(16, 30)
                .doubleBlock(18)
                .addShort("3perces-19", 19, 30, 3).withSelection("children")
                .singleBlockPre(21)
                .addBlock("3perces-20-21-havolt", LocalTime.of(20, 57), 6)
                .addBlock("3perces-20-21-pot", LocalTime.of(21, 28, 30), 3)
                .singleBlockAt(22)
                .build();

    }


}
