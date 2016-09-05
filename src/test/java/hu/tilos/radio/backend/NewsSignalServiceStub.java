package hu.tilos.radio.backend;

import hu.tilos.radio.backend.file.NewsElement;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class NewsSignalServiceStub extends NewsSignalService {
    @Override
    public void init() {
        NewsSignal ns = new NewsSignal();
        ns.setLoop(new NewsElement("x", Paths.get("/tmp/tests.mp3").toString(), "signal", 10));
        ns.setIntro(new NewsElement("x", Paths.get("/tmp/tests.mp3").toString(), "signal", 10));
        ns.setOutro(new NewsElement("x", Paths.get("/tmp/tests.mp3").toString(), "signal", 10));
        signals.put("default",ns);
    }
}

