package hu.tilos.radio.backend.file;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Scanner;

@Service
public class FileDuration {

    public int calculate(Path file) {
        ProcessBuilder pb = new ProcessBuilder("soxi", "-D", file.toString());
        try {
            Process start = pb.start();
            String result = new Scanner(start.getInputStream()).useDelimiter("//Z").next();
            start.waitFor();
            return Double.valueOf(result.trim()).intValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
