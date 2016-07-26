package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsSignalService;
import hu.tilos.radio.backend.block.NewsBlock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockBuilder {

    @Autowired
    NewsSignalService signalService;

    private LocalDate date;

    private List<NewsBlock> result;

    private String[] shortSignals = new String[]{"short", "czaban", "zep", "rooster"};

    private Random random = new Random();

    public BlockBuilder(LocalDate date) {
        this.date = date;
        this.result = new ArrayList<>();
    }

    public List<NewsBlock> build() {
        return result;
    }

    public BlockBuilder addShort(String name, int hour, int minute, int duration) {
        addShort(name, hour, minute, 0, duration);
        return this;
    }

    public BlockBuilder addShort(String name, int hour, int minute, int duration, String description) {
        addShort(name, hour, minute, 0, duration);
        return this;
    }

    public BlockBuilder addShort(String name, int hour, int minute, int second, int duration) {
        NewsBlock newsBlock = new NewsBlock(name, date.atTime(hour, minute, second), duration * 60)
                .setSelection("short");
        result.add(newsBlock);
        return this;
    }

    public BlockBuilder withSelection(String selection) {
        result.get(result.size() - 1).setSelection(selection);
        return this;
    }

    protected BlockBuilder singleBlockSymmetric(int hour, int min) {
        LocalTime time = LocalTime.of(hour, min).minusSeconds(3 * 60 / 2);
        addShort("3perces-" + hour, time.getHour(), time.getMinute(), time.getSecond(), 3);
        return this;
    }

    protected BlockBuilder singleBlockAt(int hour, int min) {
        addShort("3perces-" + hour, hour, min, 0, 3);
        return this;
    }

    public BlockBuilder singleBlockAt(int hour) {
        addShort("3perces-" + hour, hour, 0, 3);
        return this;
    }

    protected BlockBuilder doubleBlock(int hour) {
        addShort("3perces-" + (hour - 1) + "-" + hour, hour - 1, 57, 6);
        addShort("3perces-" + (hour - 1) + "-" + hour + "-havolt", hour, 0, 3);
        return this;
    }

    protected BlockBuilder singleBlockPre(int hour) {
        addShort("3perces-" + (hour - 1), hour - 1, 57, 3);
        return this;

    }

    public BlockBuilder addShort(String name, LocalTime time, int duration) {
        addShort(name, time.getHour(), time.getMinute(), duration);
        return this;
    }

    public BlockBuilder addBlock(String name, LocalTime time, int durationMinute) {
        result.add(new NewsBlock(name, date.atTime(time), durationMinute * 60));
        return this;
    }

}
