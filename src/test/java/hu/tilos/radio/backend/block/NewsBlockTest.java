package hu.tilos.radio.backend.block;

import hu.tilos.radio.backend.file.NewsElement;
import org.junit.Assert;
import org.junit.Test;

public class NewsBlockTest {

    @Test
    public void sortOrder() {
        NewsBlock nb = new NewsBlock();

        Integer i1 = nb.getWeight(new NewsElement("", "cim/szines.wav", "cim", 12));
        Integer i2 = nb.getWeight(new NewsElement("", "szines/asd.mp3", "szines", 12));
        Integer i3 = nb.getWeight(new NewsElement("", "cim/idojaras.wav", "cim", 12));
        Integer i4 = nb.getWeight(new NewsElement("", "idojaras/asd.wav", "idojaras", 12));


        Assert.assertTrue(i1 < i2);
        Assert.assertTrue(i2 < i3);
        Assert.assertTrue(i3 < i4);


    }

}