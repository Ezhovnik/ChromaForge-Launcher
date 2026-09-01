package chromaforge.launcher.run;

import java.io.IOException;
import java.nio.file.Path;

import chromaforge.launcher.debug.Logger;

public class WindowsRunner implements EngineRunner {
    private static Logger logger = Logger.getLogger("windows-runner");

    @Override
    public void run(Path coreDir) {
        ProcessBuilder pb = new ProcessBuilder(coreDir.resolve("ChromaForge.exe").toString())
            .directory(coreDir.toFile())
            .inheritIO();
        try {
            logger.info("Starting engine...");
            Process engineProcess = pb.start();
            int code = engineProcess.waitFor();
            logger.info("Engine has terminated with code " + code);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run engine", e);
        }
    }
}
