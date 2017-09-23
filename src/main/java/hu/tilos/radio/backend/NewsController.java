package hu.tilos.radio.backend;

import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.block.NewsBlockService;
import hu.tilos.radio.backend.block.NewsBlockToSave;
import hu.tilos.radio.backend.file.FileDuration;
import hu.tilos.radio.backend.file.NewsElement;
import hu.tilos.radio.backend.file.NewsFile;
import hu.tilos.radio.backend.file.NewsFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class NewsController {


    @Inject
    FileDuration fileDuration;

    @Inject
    private NewsBlockService blockService;

    @Inject
    private NewsFileService fileService;

    @Inject
    private NewsSignalService signalService;

    @Inject
    UploadService uploadService;

    @Value("${news.inputDir}")
    private String inputDir;


    @RequestMapping(value = "/api/v1/news/file")
    public List<NewsFile> fileList() {
        return fileService.getFiles();
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/file/{id}")
    public NewsFile get(@PathVariable String id) {
        return fileService.get(id);
    }

    @RequestMapping(value = "/api/v1/news/signal")
    public Collection<NewsSignal> getSignals() {
        return signalService.list();
    }

    /**
     * Additional files to add to the news feed.
     */
    @RequestMapping(value = "/api/v1/news/plus")
    public Collection<NewsElement> getPlus() {
        try {
            List<NewsElement> collect = Stream.concat(getExtraFiles("after"), getExtraFiles("before")).collect(Collectors.toList());
            return collect;
        } catch (IOException e) {
            throw new AssertionError(e);
        }


    }

    private Stream<NewsElement> getExtraFiles(String type) throws IOException {
        try (Stream<Path> files = Files.list(Paths.get(inputDir).resolve(type))) {
            return files.map(path ->
                NewsElement.from(Paths.get(inputDir).relativize(path), "news_" + type, fileDuration.calculate(path)));
        }
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/file/{id}", method = RequestMethod.PUT)
    public NewsFile get(@RequestBody NewsFile file) {
        return fileService.update(file);
    }


    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/file/{id}", method = RequestMethod.DELETE)
    public void deleteFile(@PathVariable String id) {
        fileService.delete(id);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/upload", method = RequestMethod.POST)
    @ResponseBody
    public void handleFileUpload(
            @RequestParam(value = "newsfile", required = true) MultipartFile file,
            @RequestParam(value = "category", required = true) String category) {


        uploadService.upload(category, file);

    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/block/{id}/upload", method = RequestMethod.POST)
    @ResponseBody
    public void handleBlockFileUpload(
            @RequestParam(value = "newsfile", required = true) MultipartFile file,
            @PathVariable String id) {
        blockService.upload(id, file);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/import", method = RequestMethod.POST)
    public void importFiles() {
        fileService.importNewFiles();
    }

    @RequestMapping(value = "/api/v1/news/todo")
    public List<NewsBlock> blockList() {
        return blockService.getTodo();
    }

    @RequestMapping(value = "/api/v1/news/block/report", method = RequestMethod.GET)
    @ResponseBody
    public String weeklyReport() {
        return blockService.getWeeklyReport();
    }

    @RequestMapping(value = "/api/v1/news/block/{date}", method = RequestMethod.GET)
    public List<NewsBlock> blockList(@PathVariable String date) {
        return blockService.getBlocks(LocalDate.parse(date));
    }

    @RequestMapping(value = "/api/v1/news/block/{id}", method = RequestMethod.PUT)
    public NewsBlock update(@PathVariable String id, @RequestBody NewsBlockToSave save) {
        return blockService.update(id, save);
    }

    @RequestMapping(value = "/api/v1/news/block/{date}/{name}", method = RequestMethod.GET, produces = "application/json")
    public NewsBlock getBlock(@PathVariable String date, @PathVariable String name) {
        return blockService.getBlock(LocalDate.parse(date), name);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/block/{date}", method = RequestMethod.DELETE)
    public String delete(@PathVariable String date) {
        blockService.deleteDay(LocalDate.parse(date));
        return "OK";
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/block/{date}/{name}/generate", method = RequestMethod.POST)
    public NewsBlock generate(@PathVariable String date, @PathVariable String name) {
        LocalDate localDate = LocalDate.parse(date);
        return blockService.generate(localDate, name);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/block/{date}/{name}/draw", method = RequestMethod.POST)
    public NewsBlock draw(@PathVariable String date, @PathVariable String name) {
        LocalDate localDate = LocalDate.parse(date);
        return blockService.draw(localDate, name);

    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @RequestMapping(value = "/api/v1/news/block/{date}/{name}/play", method = RequestMethod.POST)
    public NewsBlock registerPlay(@PathVariable String date, @PathVariable String name, @RequestParam(defaultValue = "false") boolean generate) {
        return blockService.registerPlay(LocalDate.parse(date), name);
    }

}
