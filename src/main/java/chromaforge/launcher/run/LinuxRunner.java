// FIXME

package chromaforge.launcher.run;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LinuxRunner implements EngineRunner {
    @Override
    public void run(Path coreDir) {
        Path appImage = coreDir.resolve("ChromaForge.AppImage");
        if (!Files.isExecutable(appImage)) {
            throw new RuntimeException("Engine executable not found or not executable: " + appImage);
        }
        ProcessBuilder pb = new ProcessBuilder(appImage.toString())
            .directory(coreDir.toFile())
            .inheritIO();
        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            System.out.println("The engine has terminated with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run engine", e);
        }
    }
}
