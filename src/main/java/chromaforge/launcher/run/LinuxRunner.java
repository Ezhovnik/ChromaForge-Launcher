package chromaforge.launcher.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ConsoleUtils.ConsoleSymbols;

public class LinuxRunner implements EngineRunner {

    @Override
    public void run(Path coreDir) {
        Path appImage = coreDir.resolve("ChromaForge.AppImage").toAbsolutePath();

        if (!Files.isExecutable(appImage)) {
            throw new RuntimeException("Engine executable not found or not executable: " + appImage);
        }
        ProcessBuilder pb = new ProcessBuilder(appImage.toString())
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
                    System.err.println(ConsoleSymbols.CROSS + " Error reading engine output: " + e.getMessage());
                }
            });
            outputReader.setDaemon(true);
            outputReader.start();

            int code = engineProcess.waitFor();
            outputReader.join(1000);

            if (code == 0) {
                System.out.println(ConsoleColor.GREEN + ConsoleSymbols.CHECK + " Engine has terminated with code " + code + ConsoleColor.RESET);
            } else {
                System.out.println(ConsoleColor.RED + ConsoleSymbols.CROSS + " Engine has terminated with code " + code + ConsoleColor.RESET);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to run engine", e);
        }
    }
}
