package hu.tilos.radio.backend.block.transition;

import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.file.FileDuration;
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
        DefaultSelector.class,
        DrawFiles.class,
        MockNewsFileRepositoryFactory.class,
        MockNewsBlockRepositoryFactory.class,
        MockNewsFileServiceFactory.class,
        NewsSignalServiceStub.class,
        FileDuration.class,
        ChildrenSelector.class,
        DrawFilesTest.class})
@Component
public class DrawFilesTest {


    @Autowired
    private NewsFileService newsFileService;

    @Autowired
    DrawFiles drawFiles;

    @Bean
    public ScriptExecutor createScriptExecutor() {
        return null;
    }


    @Test
    public void testSelectFiles() {
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
        valami = drawFiles.process(valami);

        //then
        Assert.assertEquals(6, valami.getFiles().size());
    }


    @Test
    public void testChildren() {
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
            valami = drawFiles.process(valami);

            //then
            Assert.assertEquals(5, valami.getFiles().size());
            Assert.assertEquals("gyerek", valami.getFiles().get(0).getCategory());
            Assert.assertEquals("gyerek", valami.getFiles().get(1).getCategory());
            Assert.assertNotEquals(valami.getFiles().get(0), valami.getFiles().get(1));
        }
    }

}