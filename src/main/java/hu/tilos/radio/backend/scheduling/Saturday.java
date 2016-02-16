package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Saturday implements BaseScheduling {

    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        return new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 51), 12)
                .addBlock("este", LocalTime.of(18, 48), 12)
                .addShort("3perces-7-plus3", 7, 27, 6)
                .doubleBlock(9)
                .singleBlockSymmetric(10, 30)
                .singleBlockSymmetric(11, 30)
                .singleBlockSymmetric(12, 30)
                .singleBlockSymmetric(13, 30)
                .addShort("3perces-14-plus3", 14, 56, 30, 9)
                .singleBlockSymmetric(16, 30)
                .singleBlockSymmetric(17, 30)
                .doubleBlock(20)
                .doubleBlock(22)
                .build();
    }

}
