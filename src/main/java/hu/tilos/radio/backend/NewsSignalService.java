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
        NewsSignal newsSignal = new NewsSignal();
        newsSignal.setIntroPath("Hirek_intro.wav");
        newsSignal.setOutroPath("Hirek_outro.wav");
        newsSignal.setSumLength(20);
        signals.put("classic", newsSignal);

        NewsSignal shortSignal = new NewsSignal();
        shortSignal.setIntroPath("short_intro.wav");
        shortSignal.setOutroPath("short_outro.wav");
        shortSignal.setSumLength(21);
        signals.put("short", shortSignal);
    }
}
