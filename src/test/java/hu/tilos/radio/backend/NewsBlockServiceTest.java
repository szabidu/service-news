package hu.tilos.radio.backend;

import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.block.NewsBlockService;
import hu.tilos.radio.backend.file.NewsFile;
import hu.tilos.radio.backend.file.NewsFileService;
import hu.tilos.radio.backend.mongoconverters.ScriptExecutor;
import hu.tilos.radio.backend.selection.ChildrenSelector;
import hu.tilos.radio.backend.selection.DefaultSelector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {
        DefaultSelector.class, ChildrenSelector.class,
        NewsSignalService.class,
        NewsBlockService.class,
        MockNewsBlockRepositoryFactory.class,
        MockNewsFileRepositoryFactory.class,
        NewsBlockServiceTest.class,
        Scheduler.class})
@Component
public class NewsBlockServiceTest {

    @Autowired
    NewsBlockService newsBlockService;


    @Bean
    public ScriptExecutor createScriptExecutor() {
        return null;
    }

    @Bean
    public NewsFileService createNewsFileService() {
        return Mockito.mock(NewsFileService.class);
    }


    @Test
    public void testSelectFiles() {
        NewsFileService newsFileService = newsBlockService.getNewsFileService();
        Mockito.reset(newsFileService);
        //given
        List<NewsFile> files = new ArrayList<>();
        files.add(new NewsFile("test1.wav", "idojaras", 30));
        files.add(new NewsFile("test2.wav", "valami", 30));
        files.add(new NewsFile("test3.wav", "valami", 30));
        files.add(new NewsFile("test4.wav", "valami", 30));

        Mockito.when(newsFileService.getFiles()).thenReturn(files);

        NewsBlock valami = new NewsBlock("valami", LocalDateTime.now(), 120);
        valami.setSelection("default");

        //when
        newsBlockService.drawFiles(valami);

        //then
        Assert.assertEquals(3, valami.getFiles().size());
    }


    @Test
    public void testChildren() {
        NewsFileService newsFileService = newsBlockService.getNewsFileService();
        Mockito.reset(newsFileService);
        //given
        List<NewsFile> files = new ArrayList<>();
        files.add(new NewsFile("test1.wav", "idojaras", 30));
        files.add(new NewsFile("test2.wav", "gyerek", 30));
        files.add(new NewsFile("test3.wav", "gyerek", 30));
        files.add(new NewsFile("test4.wav", "gyerek", 30));

        Mockito.when(newsFileService.getFiles()).thenReturn(files);

        for (int i = 0; i < 100; i++) {
            NewsBlock valami = new NewsBlock("valami", LocalDateTime.now(), 90);
            valami.setSelection("children");

            //when
            newsBlockService.drawFiles(valami);

            //then
            Assert.assertEquals(2, valami.getFiles().size());
            Assert.assertEquals("gyerek", valami.getFiles().get(0).getCategory());
            Assert.assertEquals("gyerek", valami.getFiles().get(1).getCategory());
            Assert.assertNotEquals(valami.getFiles().get(0), valami.getFiles().get(1));
        }
    }


}