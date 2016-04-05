package hu.tilos.radio.backend.block;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.tilos.radio.backend.file.NewsFile;
import hu.tilos.radio.backend.json.LocalDateTimeJsonSerializer;
import hu.tilos.radio.backend.json.LocalDateTimeListJsonSerializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "newsblock")
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

    @JsonSerialize(using = LocalDateTimeListJsonSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<LocalDateTime> liveAt = new ArrayList<>();

    private String path;

    private String backgroundPath;

    private boolean withSeparatorSignal = true;

    private String signalType;

    private String description;

    private String selection = "default";

    public NewsBlock() {
    }

    public NewsBlock(String name, LocalDateTime date, int expectedDuration) {
        this.name = name;
        this.date = date;
        this.expectedDuration = expectedDuration;
    }

    public NewsBlock(String name, LocalDateTime date, int expectedDuration, String description) {
        this.name = name;
        this.date = date;
        this.expectedDuration = expectedDuration;
        this.description = description;
    }

    public boolean isWithSeparatorSignal() {
        return withSeparatorSignal;
    }

    public NewsBlock setWithSeparatorSignal(boolean withSeparatorSignal) {
        this.withSeparatorSignal = withSeparatorSignal;
        return this;
    }

    public static DateTimeFormatter getDataBasedPath() {
        return dataBasedPath;
    }

    public static void setDataBasedPath(DateTimeFormatter dataBasedPath) {
        NewsBlock.dataBasedPath = dataBasedPath;
    }

    public String getSignalType() {
        return signalType;
    }

    public NewsBlock setSignalType(String signalType) {
        this.signalType = signalType;
        return this;
    }

    public String getBackgroundPath() {
        return backgroundPath;

    }

    public NewsBlock setBackgroundPath(String backgroundPath) {
        this.backgroundPath = backgroundPath;
        return this;
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

    public boolean wasLive() {
        return liveAt != null && liveAt.size() > 0;
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

    public String getDescription() {
        return description;
    }

    public NewsBlock setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getSelection() {
        return selection;
    }

    public NewsBlock setSelection(String selection) {
        this.selection = selection;
        return this;
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
        } else {
            setPath(null);
        }
        return this;
    }

    public Path createDestinationPath() {
        return Paths.get(getDate().format(dataBasedPath), getName() + ".mp3");
    }
}
