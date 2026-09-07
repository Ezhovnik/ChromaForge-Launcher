package chromaforge.launcher.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;

public class LinuxRunner implements EngineRunner {

    @Override
    public void run(Path coreDir, List<String> args) {
        Path appImage = coreDir.resolve("ChromaForge.AppImage").toAbsolutePath();

        if (!Files.isExecutable(appImage)) {
            throw new RuntimeException("Engine executable not found or not executable: " + appImage);
        }

        List<String> command = new ArrayList<>();
        command.add(appImage.toString());
        command.addAll(args);
        ProcessBuilder pb = new ProcessBuilder(command)
                .directory(coreDir.toFile())
                .redirectErrorStream(true);

        try {
            System.out.println("Starting engine...");
            Process engineProcess = pb.start();

            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(ConsoleColor.DIM + " [Engine] " + ConsoleColor.RESET + line);
                    }
                } catch (IOException e) {
                    ConsoleUtils.error("Error reading engine output: " + e.getMessage());
                }
            });
            outputReader.setDaemon(true);
            outputReader.start();

            int code = engineProcess.waitFor();
            outputReader.join(1000);

            if (code == 0) {
                ConsoleUtils.success("Engine has terminated with code " + code);
            } else {
                ConsoleUtils.error("Engine has terminated with code " + code);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run engine", e);
        }
    }
}
