package hu.tilos.radio.backend.selection;

import hu.tilos.radio.backend.file.NewsFile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ChildrenSelector extends ShortSelector {

    private Random random = new Random();

    @Override
    public NewsFile pickOne(List<NewsFile> files) {
        List<NewsFile> children = files.stream().filter(file -> file.getCategory().equals("gyerek")).collect(Collectors.toList());
        if (!children.isEmpty()) {
            return children.get(random.nextInt(children.size()));
        } else {
            return super.pickOne(files);
        }
    }
}

