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
        Path destinationFilePath = getOutputDirPath().resolve(block.createDestinationPath());

        StringBuilder b = new StringBuilder();
        b.append("#!/bin/bash\n" +
                "set -e\n" +
                "export TMPDIR=./tmp\n" +
                "rm -rf $TMPDIR\n" +
                "mkdir -p $TMPDIR\n" +
                "export SIGNALDIR=./signal\n");
        b.append("sox $SIGNALDIR/silence6.wav $TMPDIR/eddig.wav\n");
        ;

        String joining = block.getFiles().stream().filter(ne -> !ne.getCategory().startsWith("news_")).map(ne -> {
            return addFile(ne.getPath()) + addSilence3();
        }).collect(Collectors.joining());

        b.append(joining);

        b.append(addSignal(block.getFiles()));

//        NewsSignal signal = signalService.getSignal(block.getSignalType());
//        Path introPath = getSignalPath().resolve(signal.getIntroPath());
//        Path outroPath = getSignalPath().resolve(signal.getOutroPath());
//
//        String loopFile = block.getBackgroundPath() != null ? block.getBackgroundPath() : signal.getDefaultLoop();
//
//        b.append("sox $TMPDIR/hirekeddig.wav $SIGNALDIR/silence3.wav $TMPDIR/hireketmondunk.wav\n" +
//                "sox " + getSignalPath().resolve(loopFile) + " $TMPDIR/zenemost.wav trim 0 $(soxi -s $TMPDIR/hireketmondunk.wav)s \n" +
//                "sox -m $TMPDIR/zenemost.wav $TMPDIR/hireketmondunk.wav $TMPDIR/zeneshirek.wav\n" +
//                "sox " + introPath + " $TMPDIR/zeneshirek.wav $TMPDIR/hirek_eleje.wav splice -q $(soxi -D " + introPath + "),2\n" +
//                "mkdir -p " + destinationFilePath.getParent() + "\n" +
//                "sox $TMPDIR/hirek_eleje.wav " + outroPath + " " + destinationFilePath + " splice -q $(soxi -D $TMPDIR/hirek_eleje.wav),2\n");
        return b.toString();
    }

    private String addSignal(List<NewsElement> files) {
        String result = "";
        result += addSilence3();

        //trim loop
        files.stream()
                .filter(ne -> ne.getCategory().equals("news_loop"))
                .findFirst()
                .map(NewsElement::getPath)
                .map(path -> );

        result += String.format("sox %s $TMPDIR/sized_loop.wav trim 0 $(soxi -s $TMPDIR/eddig.wav)s \n",getInputDirPath().resolve())
        //loop + eddig

        //add intro

        //add outro


                "sox -m $TMPDIR/zenemost.wav $TMPDIR/hireketmondunk.wav $TMPDIR/zeneshirek.wav\n" +
                "sox " + introPath + " $TMPDIR/zeneshirek.wav $TMPDIR/hirek_eleje.wav splice -q $(soxi -D " + introPath + "),2\n" +
                "mkdir -p " + destinationFilePath.getParent() + "\n" +
                "sox $TMPDIR/hirek_eleje.wav " + outroPath + " " + destinationFilePath + " splice -q $(soxi -D $TMPDIR/hirek_eleje.wav),2\n");

    }

    private String addFile(String path) {
        return String.format("sox $TMPDIR/eddig.wav %s $TMPDIR/tmp.wav\n" +
                "mv $TMPDIR/tmp.wav $TMPDIR/eddig.wav\n", path);
    }

    private String addSilence3() {
        addFile("silence/silence3.wav");
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
