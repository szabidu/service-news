package hu.tilos.radio.backend;

import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.scheduling.*;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Scheduler {

    Map<DayOfWeek, BaseScheduling> schedulings = new HashMap<>();

    public Scheduler() {
        this.schedulings.put(DayOfWeek.MONDAY, new Monday());
        this.schedulings.put(DayOfWeek.TUESDAY, new Tuesday());
        this.schedulings.put(DayOfWeek.WEDNESDAY, new Wednesday());
        this.schedulings.put(DayOfWeek.THURSDAY, new Thursday());
        this.schedulings.put(DayOfWeek.FRIDAY, new Friday());
        this.schedulings.put(DayOfWeek.SATURDAY, new Saturday());
        this.schedulings.put(DayOfWeek.SUNDAY, new Sunday());

    }

    public List<NewsBlock> getScheduledBlocks(LocalDate date) {
        return this.schedulings.get(date.getDayOfWeek()).createBlocks(date, true);

    }


}
