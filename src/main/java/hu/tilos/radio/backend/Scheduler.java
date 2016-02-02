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

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            result.add(new NewsBlock("reggel", date.atTime(8, 55), 11 * 60 + 30));
            result.add(new NewsBlock("este", date.atTime(20, 0), 11 * 60 + 30));
        } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            result.add(new NewsBlock("reggel", date.atTime(8, 50), 11 * 60 + 30));
            result.add(new NewsBlock("este", date.atTime(20, 0), 11 * 60 + 30));
        } else if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
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

}
