package chromaforge.launcher.run;

import java.io.IOException;
import java.nio.file.Path;

public class WindowsRunner implements EngineRunner {
    @Override
    public void run(Path coreDir) {
        ProcessBuilder pb = new ProcessBuilder(coreDir.resolve("ChromaForge.exe").toString())
            .directory(coreDir.toFile())
            .inheritIO();
        try {
            Process engineProcess = pb.start();
            int code = engineProcess.waitFor();
            System.out.println("The engine has terminated with code " + code);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run engine", e);
        }
    }
}
