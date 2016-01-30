package hu.tilos.radio.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@RestController
public class NewsController {


    @Inject
    private NewsBlockService blockService;

    @Inject
    private NewsFileService fileService;

    @Inject
    UploadService uploadService;


    @RequestMapping(value = "/api/v1/news/file")
    public List<NewsFile> fileList() {
        return fileService.getFiles();
    }

    @RequestMapping(value = "/api/v1/news/upload", method = RequestMethod.POST)
    @ResponseBody
    public void handleFileUpload(
            @RequestParam(value = "newsfile", required = true) MultipartFile file,
            @RequestParam(value = "category", required = true) String category) {


        uploadService.upload(category, file);

    }

    @RequestMapping(value = "/api/v1/news/import")
    public void importFiles() {
        fileService.importNewFiles();
    }

    @RequestMapping(value = "/api/v1/news/todo")
    public List<NewsBlock> blockList() {
        return blockService.getTodo();
    }

    @RequestMapping(value = "/api/v1/news/block/{date}")
    public List<NewsBlock> blockList(@PathVariable String date) {
        return blockService.getBlocks(LocalDate.parse(date));
    }

    @RequestMapping(value = "/api/v1/news/block/{date}/{name}", method = RequestMethod.GET, produces = "application/json")
    public NewsBlock getBlock(@PathVariable String date, @PathVariable String name) {
        return blockService.getBlock(LocalDate.parse(date), name);
    }

    @RequestMapping(value = "/api/v1/news/block/{date}/{name}", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getBlockList(@PathVariable String date, @PathVariable String name) {
        return blockService.getGenerateScript(LocalDate.parse(date), name);
    }


    @RequestMapping(value = "/api/v1/news/block/{date}/{name}", method = RequestMethod.POST)
    public NewsBlock save(@PathVariable String date, @PathVariable String name, @RequestParam(defaultValue = "false") boolean generate) {
        LocalDate localDate = LocalDate.parse(date);
        if (generate) {
            return blockService.generate(localDate, name);
        } else {

            return blockService.draw(localDate, name);
        }
    }

}
