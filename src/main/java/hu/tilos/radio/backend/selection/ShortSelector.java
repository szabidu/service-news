package hu.tilos.radio.backend.selection;

import hu.tilos.radio.backend.file.NewsElement;
import hu.tilos.radio.backend.file.NewsFile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShortSelector extends DefaultSelector {

    @Override
    public boolean filter(NewsFile file) {
        return !file.getCategory().equals("idojaras") && !file.getCategory().equals("sport");
    }

    @Override
    protected void addCategoryTitle(NewsFile one, List<NewsElement> selectedFiles) {
        //noop
    }

    public NewsFile pickOne(List<NewsFile> files) {
        if (files.size() < 1) {
            return null;
        } else {
            List<NewsFile> importantList = files.stream().filter(file -> file.getCategory().equals("fontos")).collect(Collectors.toList());
            if (!importantList.isEmpty()) {
                return importantList.get(random.nextInt(importantList.size()));
            } else {
                return files.get(random.nextInt(files.size()));
            }
        }
    }

}
