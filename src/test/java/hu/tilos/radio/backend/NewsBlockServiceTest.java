package hu.tilos.radio.backend;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NewsBlockServiceTest {

    @Test
    public void testDrawFiles() throws Exception {
        //given
        LocalDateTime time = LocalDateTime.of(2011, 10, 10, 10, 10, 10);

        LocalDateTime start = time.withHour(0).withMinute(0);
        LocalDateTime end = start.plusDays(1);

        NewsBlockService s = new NewsBlockService();

        NewsSignalService signalService = Mockito.mock(NewsSignalService.class);
        NewsSignal signal = Mockito.mock(NewsSignal.class);
        when(signal.getSumLength()).thenReturn(4);
        when(signalService.getSignal(any())).thenReturn(signal);
        s.setSignalService(signalService);

        List<NewsFile> newsFiles = new ArrayList<>();
        NewsFile newsFile = new NewsFile();
        newsFile.setValidFrom(start);
        newsFile.setExpiration(end);
        newsFile.setDuration(20);
        newsFile.setCategory("test");
        newsFiles.add(newsFile);

        newsFile = new NewsFile();
        newsFile.setValidFrom(start);
        newsFile.setExpiration(end);
        newsFile.setDuration(20);
        newsFile.setCategory("sport");
        newsFiles.add(newsFile);

        newsFile = new NewsFile();
        newsFile.setValidFrom(start);
        newsFile.setExpiration(end);
        newsFile.setDuration(20);
        newsFile.setCategory("test");
        newsFiles.add(newsFile);


        NewsFileService fs = mock(NewsFileService.class);
        when(fs.getFiles()).thenReturn(newsFiles);
        s.setNewsFileService(fs);

        //when
        NewsBlock block = new NewsBlock("test", time, 60);
        s.drawFiles(block);

        //then
        Assert.assertEquals(2, block.getFiles().size());
        Assert.assertTrue(!block.getFiles().get(0).getCategory().equals("sport"));
        Assert.assertTrue(!block.getFiles().get(1).getCategory().equals("sport"));

    }
}