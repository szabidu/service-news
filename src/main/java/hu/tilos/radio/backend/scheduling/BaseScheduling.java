package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.util.List;

public abstract class BaseScheduling {

    public abstract void createBlocks(LocalDate date, List<NewsBlock> result, boolean odd);

    public void addShort(LocalDate date, List<NewsBlock> result, String name, int hour, int minute, int duration) {
        addShort(date, result, name, hour, minute, 0, duration);
    }

    public void addShort(LocalDate date, List<NewsBlock> result, String name, int hour, int minute, int duration, String description) {
        addShort(date, result, name, hour, minute, 0, duration);
    }

    public void addShort(LocalDate date, List<NewsBlock> result, String name, int hour, int minute, int second, int duration) {
        result.add(new NewsBlock(name, date.atTime(hour, minute, second), duration * 60)
                .setWithSeparatorSignal(false)
                .setBackgroundPath("bangkok.wav")
                .setSignalType("short"));
    }
}
