package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.block.NewsBlock;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class WeekdayTest {

    @Test
    public void testMonday() throws Exception {
        test(new Monday(), LocalDate.of(2016, 10, 8));
    }

    @Test
    public void testTuesday() throws Exception {
        test(new Tuesday(), LocalDate.of(2016, 10, 9));
    }

    @Test
    public void testWednesday() throws Exception {
        test(new Wednesday(), LocalDate.of(2016, 10, 10));
    }

    @Test
    public void testThursday() throws Exception {
        test(new Thursday(), LocalDate.of(2016, 10, 11));
    }

    @Test
    public void testFriday() throws Exception {
        test(new Friday(), LocalDate.of(2016, 10, 12));
    }

    @Test
    public void testSaturday() throws Exception {
        test(new Saturday(), LocalDate.of(2016, 10, 13));
    }

    @Test
    public void testSunday() throws Exception {
        test(new Sunday(), LocalDate.of(2016, 10, 14));
    }

    public void test(BaseScheduling scheduling, LocalDate localDate) {

        List<NewsBlock> blocks = scheduling.createBlocks(localDate, false);

        int sum = blocks.stream().filter(block -> !block.getName().contains("havolt")).mapToInt(NewsBlock::getExpectedDuration).sum();
        blocks.forEach(block -> {
            System.out.println(block.getDate() + " " + block.getName() + " " + (block.getExpectedDuration() / 60));
        });
        Assert.assertEquals((21 + 18 * 3), sum / 60);

        blocks = scheduling.createBlocks(localDate, true);

        sum = blocks.stream().filter(block -> !block.getName().contains("havolt")).mapToInt(NewsBlock::getExpectedDuration).sum();

        Assert.assertEquals((21 + 18 * 3), sum / 60);


    }
}