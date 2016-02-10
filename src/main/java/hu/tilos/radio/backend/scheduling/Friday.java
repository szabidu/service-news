package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.util.List;

public class Friday extends BaseScheduling {
    @Override
    public void createBlocks(LocalDate date, List<NewsBlock> result, boolean odd) {
        result.add(new NewsBlock("reggel", date.atTime(5, 0), 12 * 60));
        result.add(new NewsBlock("este", date.atTime(19, 48), 12 * 60));

        singleBlockPre(date, result, 7);
        doubleBlock(date, result, 8);
        doubleBlock(date, result, 10);
        singleBlock(date, result, 11, 30);
        singleBlock(date, result, 12, 30);
        singleBlock(date, result, 13, 30);
        singleBlock(date, result, 14, 30);
        doubleBlock(date, result, 16);
        doubleBlock(date, result, 18);
        addShort(date, result, "3perces-19", 19, 30, 3);
        doubleBlock(date, result, 21);
        singleBlock(date, result, 22);

    }

}
