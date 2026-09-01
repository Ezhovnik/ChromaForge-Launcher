package chromaforge.launcher.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import chromaforge.launcher.debug.Logger;

public class WindowsRunner implements EngineRunner {
    private static Logger logger = Logger.getLogger("windows-runner");

    @Override
    public void run(Path coreDir) {
        ProcessBuilder pb = new ProcessBuilder(coreDir.resolve("ChromaForge.exe").toString())
            .directory(coreDir.toFile())
            .redirectErrorStream(true);
        try {
            logger.info("Starting engine...");
            Process engineProcess = pb.start();

            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("    " + line);
                    }
                } catch (IOException e) {
                    logger.error("Error reading engine output: " + e.getMessage());
                }
            });
            outputReader.setDaemon(true);
            outputReader.start();

            int code = engineProcess.waitFor();
            outputReader.join(1000);

            logger.info("Engine has terminated with code " + code);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run engine", e);
        }
    }
}
