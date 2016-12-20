package hu.tilos.radio.backend.block.transition;

import hu.tilos.radio.backend.NewsSignalService;
import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.file.NewsElement;
import hu.tilos.radio.backend.mongoconverters.ScriptExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenerateFile implements StateTransition {

    @Value("${news.workDir}")
    private String workDir;

    @Value("${news.outputDir}")
    private String outputDir;

    @Value("${news.inputDir}")
    private String inputDir;

    @Inject
    private ScriptExecutor scriptExecutor;

    @Inject
    private NewsSignalService signalService;

    @Override
    public NewsBlock process(NewsBlock block) {
        String generateScript = getGenerateScript(block);
        scriptExecutor.executeScript(generateScript, workDir, "combine");
        return block;
    }


    public String getGenerateScript(NewsBlock block) {
        Path destinationPath = getOutputDirPath().resolve(block.createDestinationPath());


        StringBuilder b = new StringBuilder();
        b.append("#!/bin/bash\n" +
                "set -e\n" +
                "export TMPDIR=/tmp/newscut\n" +
                "rm -rf $TMPDIR\n" +
                "mkdir -p $TMPDIR\n" +
                "export SIGNALDIR=./signal\n");
        b.append("sox " + getInputDirPath().resolve("silence").resolve("silence6.wav") + " $TMPDIR/eddig.wav\n");
        ;

        String joining = block.getFiles().stream().filter(ne -> !ne.getCategory().startsWith("news_")).map(ne -> {
            return addFile(ne.getPath()) + addSilence3();
        }).collect(Collectors.joining());

        b.append(joining);

        b.append(addSignal(block.getFiles()));

        b.append("mkdir -p " + destinationPath.getParent() + "\n");


        b.append(block.getFiles().stream()
                .filter(ne -> ne.getCategory().equals("news_after"))
                .map(NewsElement::getPath)
                .map(path -> join("$TMPDIR/eddig.wav", "" + getInputDirPath().resolve(path)))
                .collect(Collectors.joining("\n")));


        b.append(block.getFiles().stream()
                .filter(ne -> ne.getCategory().equals("news_before"))
                .map(NewsElement::getPath)
                .map(path -> join("" + getInputDirPath().resolve(path), "$TMPDIR/eddig.wav"))
                .collect(Collectors.joining("\n")));

        b.append("mv $TMPDIR/eddig.wav " + destinationPath + "\n");

        return b.toString();
    }

    private String addSignal(List<NewsElement> files) {
        String result = "";
        result += addSilence3();

        //loop
        result += files.stream()
                .filter(ne -> ne.getCategory().equals("news_loop"))
                .findFirst()
                .map(NewsElement::getPath)
                .map(loopPath -> {
                    return "sox " + getInputDirPath().resolve(loopPath) + " $TMPDIR/sized_loop.wav trim 0 $(soxi -s $TMPDIR/eddig.wav)s \n" +
                            "sox -m $TMPDIR/eddig.wav $TMPDIR/sized_loop.wav $TMPDIR/tmp.wav\n" +
                            "mv $TMPDIR/tmp.wav $TMPDIR/eddig.wav\n";

                }).orElse("");

        //add intro
        result += files.stream()
                .filter(ne -> ne.getCategory().equals("news_intro"))
                .findFirst()
                .map(NewsElement::getPath)
                .map(path -> {
                    return join("" + getInputDirPath().resolve(path), "$TMPDIR/eddig.wav");
                }).orElse("");


        //add outro
        result += files.stream()
                .filter(ne -> ne.getCategory().equals("news_outro"))
                .findFirst()
                .map(NewsElement::getPath)
                .map(path -> {
                    return join("$TMPDIR/eddig.wav", "" + getInputDirPath().resolve(path));
                }).orElse("");

        return result;

    }

    private String join(String first, String second) {
        return "sox " + first + " " + second + " $TMPDIR/tmp.wav splice -q $(soxi -D " + first + "),2\n" +
                "mv $TMPDIR/tmp.wav $TMPDIR/eddig.wav\n";
    }

    private String addFile(String path) {
        return String.format("sox $TMPDIR/eddig.wav \"%s\" $TMPDIR/tmp.wav\n" +
                "mv $TMPDIR/tmp.wav $TMPDIR/eddig.wav\n", getInputDirPath().resolve(path));
    }

    private String addSilence3() {
        return addFile("silence/silence3.wav");
    }

    public Path getOutputDirPath() {
        return Paths.get(outputDir);
    }

    public Path getInputDirPath() {
        return Paths.get(inputDir);
    }

    public Path getSignalPath() {
        return Paths.get(workDir).resolve("signal");
    }

    public static int estimateDuration(List<NewsElement> selectedFiles) {
        return selectedFiles.stream().filter(ne -> !ne.getCategory().equals("news_loop")).mapToInt(ne -> ne.getDuration()).sum() + selectedFiles.size() * 3;
    }
}
