package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.util.List;

public class Thursday extends BaseScheduling {

    public void createBlocks(LocalDate date, List<NewsBlock> result, boolean odd) {
        result.add(new NewsBlock("reggel", date.atTime(5, 0), 12 * 60));

        addShort(date, result, "3perces-6-7", 6, 54, 9, "2*3 perc + 3 a 12-ből");

        addShort(date, result, "3perces-8", 8, 28, 30, 3);

        addShort(date, result, "3perces-9-10", 9, 57, 6);
        addShort(date, result, "3perces-9-10-havolt", 9, 57, 3);

        addShort(date, result, "3perces-11-12", 11, 57, 6);
        addShort(date, result, "3perces-11-12-havolt", 11, 57, 3);

        addShort(date, result, "3perces-13-14", 13, 57, 6);
        addShort(date, result, "3perces-13-14-havolt", 13, 57, 3);

        addShort(date, result, "3perces-15", 15, 0, 3);

        addShort(date, result, "3perces-16", 16, 28, 30, 3);

        addShort(date, result, "3perces-17-18", 17, 57, 6);
        addShort(date, result, "3perces-17-18-havolt", 17, 57, 3);

        result.add(new NewsBlock("este", date.atTime(19, 48), 9 * 60 + 3 * 60));
        
        addShort(date, result, "3perces-20-21", 20, 57, 6);
        addShort(date, result, "3perces-20-21-havolt", 20, 57, 3);

        addShort(date, result, "3perces-22", 22, 57, 3);
    }
}
