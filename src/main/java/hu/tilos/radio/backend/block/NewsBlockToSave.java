package hu.tilos.radio.backend.block;

import java.util.ArrayList;
import java.util.List;

public class NewsBlockToSave {

    private List<NewsFileReference> files = new ArrayList<>();

    public List<NewsFileReference> getFiles() {
        return files;
    }

    public void setFiles(List<NewsFileReference> files) {
        this.files = files;
    }
}
