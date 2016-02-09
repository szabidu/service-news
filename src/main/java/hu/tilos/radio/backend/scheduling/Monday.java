package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.util.List;

public class Monday extends BaseScheduling {

    @Override
    public void createBlocks(LocalDate date, List<NewsBlock> result, boolean odd) {
        addShort(date, result, "3perces-5", 5, 0, 6, "6 órás 3perces + 3 a 21-ből");
        addShort(date, result, "3perces-6-7", 6, 57, 6);
        addShort(date, result, "3perces-6-7-havolt", 6, 57, 3);
        result.add(new NewsBlock("reggel", date.atTime(8, 0), 9 * 60 + 3 * 60, "3 perces + 9 a 21-ból"));
        addShort(date, result, "3perces-9-10", 9, 57, 6);
        addShort(date, result, "3perces-9-10-havolt", 9, 57, 3, "Ha volt az előző órában saját");
        addShort(date, result, "3perces-11", 11, 0, 3);
        addShort(date, result, "3perces-12", 12, 0, 3);
        if (odd) {
            addShort(date, result, "3perces-13", 13, 0, 3);
        } else {
            addShort(date, result, "3perces-13", 13, 28, 30, 3);
        }
        addShort(date, result, "3perces-14-15", 14, 57, 0, 6);
        addShort(date, result, "3perces-14-15-havolt", 14, 57, 3, "Ha volt az előzőben saját");

        addShort(date, result, "3perces-16-17", 16, 57, 0, 6);
        addShort(date, result, "3perces-16-17-havolt", 16, 57, 3, "Ha volt az előzőben saját");

        addShort(date, result, "3perces-18", 18, 28, 30, 3);

        result.add(new NewsBlock("este", date.atTime(19, 48), 9 * 60 + 3 * 60, "3 perces + 9 a 21-ból"));

        addShort(date, result, "3perces-20-21", 20, 57, 6);
        addShort(date, result, "3perces-22", 22, 0, 3);
    }
}
