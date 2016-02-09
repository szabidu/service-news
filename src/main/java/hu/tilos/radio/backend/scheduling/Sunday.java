package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.util.List;

public class Sunday extends BaseScheduling {
    public void createBlocks(LocalDate date, List<NewsBlock> result, boolean odd) {
        addShort(date, result, "3perces-5", 5, 0, 6);
        addShort(date, result, "3perces-6-7", 6, 57, 6);
        addShort(date, result, "3perces-6-7-havolt", 6, 57, 3);
        result.add(new NewsBlock("reggel", date.atTime(8, 48), 9 * 60 + 3 * 60));
        addShort(date, result, "3perces-9-10", 9, 57, 6);
        addShort(date, result, "3perces-9-10-havolt", 9, 57, 3);
        addShort(date, result, "3perces-11", 10, 28, 30, 3);
        addShort(date, result, "3perces-13", 13, 28, 30, 3);
        addShort(date, result, "3perces-14-15", 14, 57, 0, 6);
        addShort(date, result, "3perces-14-15-havolt", 14, 57, 0, 3);
        addShort(date, result, "3perces-16", 16, 0, 3);
        addShort(date, result, "3perces-17", 17, 0, 3);
        result.add(new NewsBlock("este", date.atTime(18, 24), 9 * 60 + 3 * 60));
        addShort(date, result, "3perces-19-20", 19, 57, 6);
        addShort(date, result, "3perces-19-20-havolt", 19, 57, 3);
        addShort(date, result, "3perces-21-22", 21, 57, 3);
        addShort(date, result, "3perces-21-22-hakell", 21, 57, 6);

    }
}
