package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Tuesday implements BaseScheduling {

    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {

        BlockBuilder builder = new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)
                .doubleBlock(10)
                .doubleBlock(12)
                .doubleBlock(14)
                .singleBlockAt(15, 0)
                .singleBlockSymmetric(16, 30)
                .doubleBlock(18)
                .singleBlockAt(19, 30).withSelection("children")
                .addBlock("este", LocalTime.of(19, 48), 9 + 3)
                .singleBlockAt(20, 57)
                .addShort("3perces-21-pot", LocalTime.of(21, 30), 3)
                .singleBlockAt(22, 0);

        if (odd) {
            builder.singleBlockAt(6, 57)
                    .doubleBlock(8);
        } else {
            builder.doubleBlock(7)
                    .singleBlockSymmetric(8, 30);
        }

        return builder.build();
    }

}