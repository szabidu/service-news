package hu.tilos.radio.backend;

import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@RestController
public class NewsController {


    @Inject
    private NewsBlockService blockService;

    @Inject
    private NewsFileService fileService;


    @RequestMapping(value = "/api/v1/news/file")
    public List<NewsFile> fileList() {
        return fileService.getRecentFiles();
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


    @RequestMapping(value = "/api/v1/news/import")
    public List<NewsFile> importFiles() {
        return fileService.getFiles();
    }
}
