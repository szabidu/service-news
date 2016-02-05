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
            result.add(new NewsBlock("orankenti1", date.atTime(5, 0), 3 * 60));
            result.add(new NewsBlock("reggel", date.atTime(6, 48), 9 * 60 + 2 * 3 * 60));
            result.add(new NewsBlock("orankenti2", date.atTime(8, 0), 3 * 60));
            result.add(new NewsBlock("orankenti3", date.atTime(9, 57), 6 * 60));
            result.add(new NewsBlock("orankenti3-potrovid", date.atTime(9, 57), 3 * 60));
            result.add(new NewsBlock("orankenti4", date.atTime(11, 57), 6 * 60));
            result.add(new NewsBlock("orankenti4-potrovid", date.atTime(11, 57), 3 * 60));
            result.add(new NewsBlock("orankenti5", date.atTime(13, 28, 30), 3 * 60));
            result.add(new NewsBlock("orankenti6", date.atTime(14, 57, 30), 6 * 60));
            result.add(new NewsBlock("orankenti6-potrovid", date.atTime(14, 57, 30), 3 * 60));
            result.add(new NewsBlock("orankenti7", date.atTime(16, 28, 30), 3 * 60));
            result.add(new NewsBlock("orankenti8", date.atTime(17, 30), 3 * 60));
            result.add(new NewsBlock("orankenti9", date.atTime(18, 30), 3 * 60));
            result.add(new NewsBlock("este", date.atTime(19, 48), 12 * 60 + +2 * 3 * 30));
            result.add(new NewsBlock("orankenti10", date.atTime(21, 00), 3 * 60));
            result.add(new NewsBlock("orankenti11", date.atTime(22, 00), 3 * 60));
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

    private void createTuesday(LocalDate date, List<NewsBlock> result) {
        result.add(new NewsBlock("orankenti1", date.atTime(5, 0), 3 * 60));
        result.add(new NewsBlock("reggel", date.atTime(6, 48), 9 * 60 + 2 * 3 * 60));
        result.add(new NewsBlock("orankenti2", date.atTime(8, 0), 3 * 60));
        result.add(new NewsBlock("orankenti3", date.atTime(9, 57), 6 * 60));
        result.add(new NewsBlock("orankenti3-potrovid", date.atTime(9, 57), 3 * 60));
        result.add(new NewsBlock("orankenti4", date.atTime(11, 57), 6 * 60));
        result.add(new NewsBlock("orankenti4-potrovid", date.atTime(11, 57), 3 * 60));
        result.add(new NewsBlock("orankenti5", date.atTime(13, 57, 0), 6 * 60));
        result.add(new NewsBlock("orankenti5-potrovid", date.atTime(13, 57, 0), 2 * 60));
        result.add(new NewsBlock("orankenti6", date.atTime(14, 57, 30), 6 * 60));
        result.add(new NewsBlock("orankenti6-potrovid", date.atTime(14, 57, 30), 3 * 60));
        result.add(new NewsBlock("orankenti7", date.atTime(16, 28, 30), 3 * 60));
        result.add(new NewsBlock("orankenti8", date.atTime(17, 57), 6 * 60));
        result.add(new NewsBlock("orankenti8-potrovid", date.atTime(17, 57), 3 * 60));
        result.add(new NewsBlock("este", date.atTime(19, 48), 12 * 60 + +2 * 3 * 30));
        result.add(new NewsBlock("orankenti10", date.atTime(21, 00), 3 * 60));
        result.add(new NewsBlock("orankenti11", date.atTime(22, 00), 3 * 60));
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

    private void addShort(LocalDate date, List<NewsBlock> result, String name, int hour, int minute, int second, int duration) {
        result.add(new NewsBlock(name, date.atTime(hour, minute, second), duration * 60)
                .setWithSeparatorSignal(false)
                .setBackgroundPath("bangkok.wav")
                .setSignalType("short"));
    }
}
