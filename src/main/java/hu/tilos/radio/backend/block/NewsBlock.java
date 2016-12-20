package hu.tilos.radio.backend.block;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hu.tilos.radio.backend.file.NewsElement;
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
import java.util.Collections;
import java.util.Comparator;
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

    private List<NewsElement> files = new ArrayList<>();

    private int expectedDuration;

    @JsonSerialize(using = LocalDateTimeListJsonSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<LocalDateTime> liveAt = new ArrayList<>();

    private String path;

    private String description;

    private String selection = "default";

    private boolean handmade;

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


    public static DateTimeFormatter getDataBasedPath() {
        return dataBasedPath;
    }

    public static void setDataBasedPath(DateTimeFormatter dataBasedPath) {
        NewsBlock.dataBasedPath = dataBasedPath;
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

    public List<NewsElement> getFiles() {
        return Collections.unmodifiableList(new ArrayList<>(files));
    }


    public void clearFiles() {
        files.clear();
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
        return files.stream().mapToInt(NewsElement::getDuration).sum();
    }

    public void addFile(NewsElement one) {
        files.add(one);
        files = sort(files);
    }

    public NewsBlock findGeneratedFile(Path root) {
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

    public boolean isHandmade() {
        return handmade;
    }

    public void setHandmade(boolean handmade) {
        this.handmade = handmade;
    }


    public List<NewsElement> sort(List<NewsElement> files) {
        List<NewsElement> result = new ArrayList<>();
        result.addAll(files);
        Collections.sort(result, new Comparator<NewsElement>() {
            @Override
            public int compare(NewsElement o1, NewsElement o2) {
                return getWeight(o1).compareTo(getWeight(o2));
            }
        });
        return result;
    }

    public Integer getWeight(NewsElement element) {
        String category = element.getCategory();
        if (category.equals("cim")) {
            return categoryOrder(Paths.get(element.getPath()).getFileName().toString().replace(".wav", ""));
        } else {
            return categoryOrder(category) + 1;
        }

    }

    protected Integer categoryOrder(String category) {
        if (category.equals("news_intro")) {
            return 0;
        } else if (category.equals("news_outro")) {
            return 1000;
        } else if (category.equals("news_before")) {
            return -1;
        } else if (category.equals("news_after")) {
            return 1010;
        } else if (category.equals("news_loop")) {
            return 1001;
        } else if (category.equals("fontos")) {
            return 2;
        } else if (category.equals("kozerdeku")) {
            return 2;
        } else if (category.equals("szines")) {
            return 300;
        } else if (category.equals("idojaras")) {
            return 305;
        } else {
            return Integer.valueOf((int) category.charAt(0));
        }
    }

    public void addFiles(List<NewsElement> newsFiles) {
        files.addAll(newsFiles);
        files = sort(files);
    }
}
