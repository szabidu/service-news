package hu.tilos.radio.backend;

import hu.tilos.radio.backend.file.NewsElement;

public class NewsSignal {

    private NewsElement intro;

    private NewsElement outro;

    private NewsElement loop;

    public NewsElement getIntro() {
        return intro;
    }

    public void setIntro(NewsElement intro) {
        this.intro = intro;
    }

    public NewsElement getOutro() {
        return outro;
    }

    public void setOutro(NewsElement outro) {
        this.outro = outro;
    }

    public NewsElement getLoop() {
        return loop;
    }

    public void setLoop(NewsElement loop) {
        this.loop = loop;
    }

}
