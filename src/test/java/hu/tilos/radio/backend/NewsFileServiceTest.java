package hu.tilos.radio.backend;

import hu.tilos.radio.backend.file.NewsFileService;
import org.junit.Assert;
import org.junit.Test;

public class NewsFileServiceTest {

    @Test
    public void testDetectExpiration() throws Exception {

        NewsFileService service = new NewsFileService();


        Assert.assertEquals(2,service.detectExpiration("(2)valami.mp3"));
        Assert.assertEquals(3,service.detectExpiration("valami(3).mp3"));
        Assert.assertEquals(2,service.detectExpiration("valami .mp3"));


    }
}