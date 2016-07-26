package hu.tilos.radio.backend.block;

import hu.tilos.radio.backend.file.NewsElement;

import java.util.ArrayList;
import java.util.List;

public class NewsBlockToSave {

    private List<NewsElement> files = new ArrayList<>();

    public List<NewsElement> getFiles() {
        return files;
    }

    public void setFiles(List<NewsElement> files) {
        this.files = files;
    }
}
