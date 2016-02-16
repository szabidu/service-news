package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Monday implements BaseScheduling {


    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        BlockBuilder builder = new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)
                .singleBlockAt(6,57)
                .doubleBlock(8)
                .doubleBlock(10)
                .addShort("3perces-11-pot", 11, 0, 3)
                .singleBlockAt(12);
        if (odd) {
            builder.singleBlockSymmetric(13, 30);
        } else {
            builder.singleBlockAt(13);
        }
        builder.doubleBlock(15)
                .doubleBlock(17)
                .singleBlockSymmetric(18, 30)
                .singleBlockAt(19, 30).withSelection("children")
                .addBlock("este", LocalTime.of(19, 48), 9 + 3)
                .doubleBlock(21)
                .addShort("3perces-22-pot", 22, 0, 3);
        return builder.build();
    }

}
