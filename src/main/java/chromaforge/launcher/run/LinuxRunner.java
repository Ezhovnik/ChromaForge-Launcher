package chromaforge.launcher.run;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import chromaforge.launcher.debug.Logger;

public class LinuxRunner implements EngineRunner {
    private static Logger logger = Logger.getLogger("linux-runner");

    @Override
    public void run(Path coreDir) {
        Path appImage = coreDir.resolve("ChromaForge.AppImage").toAbsolutePath();

        if (!Files.isExecutable(appImage)) {
            throw new RuntimeException("Engine executable not found or not executable: " + appImage);
        }
        ProcessBuilder pb = new ProcessBuilder(appImage.toString())
                .directory(coreDir.toFile())
                .inheritIO();

        try {
            logger.info("Starting engine...");
            Process process = pb.start();
            int code = process.waitFor();
            logger.info("Engine has terminated with code " + code);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run engine", e);
        }
    }
}
