package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Thursday implements BaseScheduling {

    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        BlockBuilder builder = new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)
                .doubleBlock(7)
                .singleBlockSymmetric(8, 30)
                .doubleBlock(10)
                .doubleBlock(12)
                .doubleBlock(14)
                .singleBlockAt(15)
                .singleBlockSymmetric(16, 30)
                .doubleBlock(18)
                .singleBlockAt(19, 30).withSelection("children")
                .addBlock("este", LocalTime.of(19, 48), 9 + 3)
                .doubleBlock(21)
                .singleBlockAt(22);
        return builder.build();

    }


}
