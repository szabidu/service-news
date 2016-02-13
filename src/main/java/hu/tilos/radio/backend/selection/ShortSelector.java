package hu.tilos.radio.backend.selection;

import hu.tilos.radio.backend.NewsFile;
import org.springframework.stereotype.Service;

@Service
public class ShortSelector extends DefaultSelector {
    @Override
    public boolean filter(NewsFile file) {
        return !file.getCategory().equals("idojaras") && !file.getCategory().equals("sport");
    }
}
