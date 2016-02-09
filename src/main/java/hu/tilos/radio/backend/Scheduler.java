package hu.tilos.radio.backend;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class Scheduler {

    public List<NewsBlock> getScheduledBlocks(LocalDate date) {
        List<NewsBlock> result = new ArrayList<>();

        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            createMonday(date, result, true);
        } else if (date.getDayOfWeek() == DayOfWeek.TUESDAY) {
            createTuesday(date, result);
        } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            createSaturday(date, result);
        } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            createSunday(date, result);
        } else {
            result.add(new NewsBlock("reggel", date.atTime(6, 48), 11 * 60 + 30));
            result.add(new NewsBlock("este", date.atTime(19, 48), 11 * 60 + 30));

            for (int i = 5; i < 23; i++) {
                if (i != 7 && i != 19) {
                    result.add(new NewsBlock("orankenti" + i + "-3perces", date.atTime(i, 0), 3 * 60)
                            .setWithSeparatorSignal(false)
                            .setBackgroundPath("bangkok.wav")
                            .setSignalType("short"));
                    result.add(new NewsBlock("orankenti" + i + "-6perces", date.atTime(i, 0), 6 * 60)
                            .setWithSeparatorSignal(false)
                            .setBackgroundPath("bangkok.wav")
                            .setSignalType("short"));
                }
            }


        }
        return result;

    }

    private void createMonday(LocalDate date, List<NewsBlock> result, boolean odd) {
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

    private void createTuesday(LocalDate date, List<NewsBlock> result) {
        addShort(date, result, "3perces-5", 5, 0, 6);
        addShort(date, result, "3perces-6", 6, 0, 3);
        result.add(new NewsBlock("reggel", date.atTime(7, 27), 9 * 60 + 3 * 60));

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

        result.add(new NewsBlock("este", date.atTime(18, 48), 9 * 60 + 3 * 60));

        addShort(date, result, "3perces-20-21", 20, 57, 6);
        addShort(date, result, "3perces-20-21-havolt", 20, 57, 3);

        addShort(date, result, "3perces-22", 22, 0, 3);

    }

    private void createSaturday(LocalDate date, List<NewsBlock> result) {
        addShort(date, result, "3perces-5", 5, 0, 6);
        addShort(date, result, "3perces-6", 6, 0, 3);
        result.add(new NewsBlock("reggel", date.atTime(7, 27), 9 * 60 + 3 * 60));
        addShort(date, result, "3perces-8-9", 8, 57, 6);
        addShort(date, result, "3perces-8-9-havolt", 8, 57, 3);
        addShort(date, result, "3perces-10", 10, 28, 30, 3);
        addShort(date, result, "3perces-11", 11, 28, 30, 3);
        addShort(date, result, "3perces-12", 12, 28, 30, 3);
        addShort(date, result, "3perces-13", 13, 28, 30, 3);
        addShort(date, result, "3perces-14-15", 14, 57, 6);
        addShort(date, result, "3perces-14-15-havolt", 14, 57, 3);
        addShort(date, result, "3perces-16", 16, 28, 30, 3);
        addShort(date, result, "3perces-17", 17, 28, 30, 3);
        result.add(new NewsBlock("este", date.atTime(18, 48), 9 * 60 + 3 * 60));
        addShort(date, result, "3perces-19-20", 19, 57, 6);
        addShort(date, result, "3perces-19-20-havolt", 19, 57, 3);
        addShort(date, result, "3perces-21-22", 21, 57, 6);
        addShort(date, result, "3perces-21-22-havolt", 21, 57, 3);
    }


    private void createSunday(LocalDate date, List<NewsBlock> result) {
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
        addShort(date, result, "3perces-21-22", 21, 57, 6);
        addShort(date, result, "3perces-21-22-havolt", 21, 57, 3);

    }

    private void addShort(LocalDate date, List<NewsBlock> result, String name, int hour, int minute, int duration) {
        addShort(date, result, name, hour, minute, 0, duration);
    }

    private void addShort(LocalDate date, List<NewsBlock> result, String name, int hour, int minute, int duration, String description) {
        addShort(date, result, name, hour, minute, 0, duration);
    }

    private void addShort(LocalDate date, List<NewsBlock> result, String name, int hour, int minute, int second, int duration) {
        result.add(new NewsBlock(name, date.atTime(hour, minute, second), duration * 60)
                .setWithSeparatorSignal(false)
                .setBackgroundPath("bangkok.wav")
                .setSignalType("short"));
    }
}
