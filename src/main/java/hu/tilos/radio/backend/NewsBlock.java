package hu.tilos.radio.backend;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NewsBlock {


    private static DateTimeFormatter dataBasedPath = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Id
    private String id;

    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    private LocalDateTime date;

    private List<NewsFile> files = new ArrayList<>();

    private int expectedDuration;

    private List<LocalDateTime> liveAt = new ArrayList<>();

    private String path;

    public NewsBlock() {
    }

    public NewsBlock(String name, LocalDateTime date, int expectedDuration) {
        this.name = name;
        this.date = date;
        this.expectedDuration = expectedDuration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocalDateTime> getLiveAt() {
        return liveAt;
    }

    public void setLiveAt(List<LocalDateTime> liveAt) {
        this.liveAt = liveAt;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<NewsFile> getFiles() {
        return files;
    }

    public void setFiles(List<NewsFile> files) {
        this.files = files;
    }

    public int getExpectedDuration() {
        return expectedDuration;
    }

    public void setExpectedDuration(int expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public int calculateLength() {
        return files.stream().mapToInt(NewsFile::getDuration).sum();
    }

    public void addFile(NewsFile one) {
        files.add(one);
    }

    public NewsBlock findGeneratedFiled(Path root) {
        Path destinationPath = createDestinationPath();
        if (Files.exists(root.resolve(destinationPath))) {
            setPath(destinationPath.toString());
        }
        return this;
    }

    public Path createDestinationPath() {
        return Paths.get(getDate().format(dataBasedPath), getName() + ".mp3");
    }
}
