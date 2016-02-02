package hu.tilos.radio.backend;

public class NewsSignal {

    private String introPath;

    private String outroPath;

    private int sumLength;

    public String getIntroPath() {
        return introPath;
    }

    public void setIntroPath(String introPath) {
        this.introPath = introPath;
    }

    public String getOutroPath() {
        return outroPath;
    }

    public void setOutroPath(String outroPath) {
        this.outroPath = outroPath;
    }

    public int getSumLength() {
        return sumLength;
    }

    public void setSumLength(int sumLength) {
        this.sumLength = sumLength;
    }
}
