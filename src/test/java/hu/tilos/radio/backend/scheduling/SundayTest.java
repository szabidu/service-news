package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SundayTest {

    @Test
    public void testCreateBlocks() throws Exception {
        List<NewsBlock> blocks = new ArrayList<>();

        new Sunday().createBlocks(LocalDate.of(2016, 02, 10), blocks, false);

        int sum = blocks.stream().filter(block -> !block.getName().contains("havolt")).mapToInt(NewsBlock::getExpectedDuration).sum();
        Assert.assertEquals((21 + 18 * 3) * 60, sum);
    }
}