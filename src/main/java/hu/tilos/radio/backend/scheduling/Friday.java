package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Friday implements BaseScheduling {
    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        return new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)
                .addBlock("este", LocalTime.of(19, 48), 12)

                .singleBlockPre(7)
                .doubleBlock(8)
                .doubleBlock(10)
                .singleBlock(11, 30)
                .singleBlock(12, 30)
                .singleBlock(13, 30)
                .singleBlock(14, 30)
                .doubleBlock(16)
                .doubleBlock(18)
                .addShort("3perces-19", 19, 30, 3)
                .doubleBlock(21)
                .singleBlock(22)
                .build();

    }


}
