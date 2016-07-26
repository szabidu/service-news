package hu.tilos.radio.backend.file;

import java.nio.file.Path;

public class NewsElement {

    private String id;

    private String path;

    private String category;

    private int duration;

    public NewsElement(String id, String path, String category, int duration) {
        this.id = id;
        this.path = path;
        this.category = category;
        this.duration = duration;
    }

    public NewsElement() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static NewsElement from(NewsFile newsFile) {
        return new NewsElement(newsFile.getId(), newsFile.getPath(), newsFile.getCategory(), newsFile.getDuration());
    }

    public static NewsElement from(Path path, String category, int duration) {
        return new NewsElement(null, path.toString(), category, duration);
    }


    public static NewsElement from(String path, String category, int duration) {
        return new NewsElement(null, path, category, duration);
    }
}
