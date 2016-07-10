package hu.tilos.radio.backend.block.transition;

import hu.tilos.radio.backend.NewsSignal;
import hu.tilos.radio.backend.NewsSignalService;
import hu.tilos.radio.backend.block.NewsBlock;
import hu.tilos.radio.backend.file.NewsFile;
import hu.tilos.radio.backend.mongoconverters.ScriptExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        b.append("sox $SIGNALDIR/silence6.wav $TMPDIR/temp.wav\n");
        b.append("mv $TMPDIR/temp.wav $TMPDIR/hirekeddig.wav\n");
        String lastCategory = null;
        for (NewsFile file : block.getFiles()) {
            if ((lastCategory == null || !lastCategory.equals(file.getCategory())) && block.isWithSeparatorSignal()) {
                Path signalPath = Paths.get(workDir, "signal", file.getCategory() + ".wav");
                if (Files.exists(signalPath)) {
                    b.append("sox $TMPDIR/hirekeddig.wav \"" + signalPath + "\" $SIGNALDIR/silence3.wav $TMPDIR/temp.wav\n");
                    b.append("mv $TMPDIR/temp.wav $TMPDIR/hirekeddig.wav\n");
                }
            }
            b.append("sox $TMPDIR/hirekeddig.wav \"" + getInputDirPath().resolve(file.getPath()) + "\" $SIGNALDIR/silence3.wav $TMPDIR/temp.wav\n");
            b.append("mv $TMPDIR/temp.wav $TMPDIR/hirekeddig.wav\n");
            lastCategory = file.getCategory();
        }

        NewsSignal signal = signalService.getSignal(block.getSignalType());
        Path introPath = getSignalPath().resolve(signal.getIntroPath());
        Path outroPath = getSignalPath().resolve(signal.getOutroPath());

        String loopFile = block.getBackgroundPath() != null ? block.getBackgroundPath() : signal.getDefaultLoop();

        b.append("sox $TMPDIR/hirekeddig.wav $SIGNALDIR/silence3.wav $TMPDIR/hireketmondunk.wav\n" +
                "sox " + getSignalPath().resolve(loopFile) + " $TMPDIR/zenemost.wav trim 0 $(soxi -s $TMPDIR/hireketmondunk.wav)s \n" +
                "sox -m $TMPDIR/zenemost.wav $TMPDIR/hireketmondunk.wav $TMPDIR/zeneshirek.wav\n" +
                "sox " + introPath + " $TMPDIR/zeneshirek.wav $TMPDIR/hirek_eleje.wav splice -q $(soxi -D " + introPath + "),2\n" +
                "mkdir -p " + destinationFilePath.getParent() + "\n" +
                "sox $TMPDIR/hirek_eleje.wav " + outroPath + " " + destinationFilePath + " splice -q $(soxi -D $TMPDIR/hirek_eleje.wav),2\n");
        return b.toString();
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
}
