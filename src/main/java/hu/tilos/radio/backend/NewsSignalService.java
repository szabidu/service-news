package hu.tilos.radio.backend;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class NewsSignalService {

    private Map<String, NewsSignal> signals = new HashMap<>();

    public NewsSignal getSignal(String id) {
        if (id == null) {
            return signals.get("classic");
        } else {
            return signals.get(id);
        }
    }

    @PostConstruct
    public void init() {
        addSignal("short", "short_intro.wav", "short_outro.wav", 21, "bangkok.wav");
        addSignal("classic", "Hirek_intro.wav", "Hirek_outro.wav", 20, "hirekzene.wav");
        addSignal("czaban", "Czaban-signal-1.mp3", "Czaban-signal-2.mp3", 34, "Czaban-loop.mp3");
        addSignal("zep", "zen-eleje.mp3", "zen-vege.mp3", 22, "zen-gumi.mp3");
        addSignal("rooster", "rooster-eleje.mp3", "rooster-vege.mp3", 17, "rooster-gumi.mp3");

        addSignal("busa", "busa-intro.wav", "busa-outro.wav", 16, "tf1128.wav");
        addSignal("vektor", "vektor-intro.wav ", "vektor-outro.wav", 22, "tf1128.wav");
        addSignal("csillla", "csilla-intro.wav", "csilla-outro.wav", 22, "vgyl-1113.wav");
        addSignal("zsu", "zsu-intro.wav", "zsu-outro.wav", 27, "s4-s.wav");


    }

    private void addSignal(String name, String intro, String outro, int length, String defaultLoop) {
        NewsSignal newsSignal = new NewsSignal();
        newsSignal.setIntroPath(intro);
        newsSignal.setOutroPath(outro);
        newsSignal.setSumLength(length);
        newsSignal.setDefaultLoop(defaultLoop);
        signals.put(name, newsSignal);
    }

}
