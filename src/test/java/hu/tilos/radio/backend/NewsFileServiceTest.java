package hu.tilos.radio.backend;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewsFileServiceTest {

    @Test
    public void testDetectExpiration() throws Exception {

        NewsFileService service = new NewsFileService();


        Assert.assertEquals(2,service.detectExpiration("(2)valami.mp3"));
        Assert.assertEquals(3,service.detectExpiration("valami(3).mp3"));
        Assert.assertEquals(1,service.detectExpiration("valami .mp3"));


    }
}