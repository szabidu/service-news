package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.util.List;

public class Saturday extends BaseScheduling {
    @Override
    public void createBlocks(LocalDate date, List<NewsBlock> result, boolean odd) {

        result.add(new NewsBlock("reggel", date.atTime(5, 51), 12 * 60));
        result.add(new NewsBlock("este", date.atTime(18, 47), 12 * 60));
        addShort(date, result, "3perces-7-plus3", 7, 27, 6);

        doubleBlock(date, result, 9);
        singleBlock(date, result, 10, 30);
        singleBlock(date, result, 11, 30);
        singleBlock(date, result, 12, 30);
        singleBlock(date, result, 13, 30);
        addShort(date, result, "3perces-14-plus3", 7, 27, 6);
        singleBlock(date, result, 16, 30);
        singleBlock(date, result, 17, 30);
        doubleBlock(date, result, 20);
        doubleBlock(date, result, 22);
    }
}
