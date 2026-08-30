package chromaforge.launcher.util.commands;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LsCommand extends Command {
    public LsCommand() {
        this.keyword = "ls";
        this.args = "";
        this.help = "list installed engine versions";
    }

    @Override
    public void execute(String[] args) {
        Path coresDir = Path.of("cores");
        if (Files.exists(coresDir) && Files.isDirectory(coresDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(coresDir, 
                    path -> Files.isDirectory(path) && path.getFileName().toString().startsWith("chromaforge-"))) {
                List<String> versions = new ArrayList<>();
                for (Path p : stream) {
                    String name = p.getFileName().toString();
                    versions.add(name.substring("chromaforge-".length()));
                }
                Collections.sort(versions);
                if (versions.isEmpty()) {
                    System.out.println("No installed versions found.");
                } else {
                    System.out.println("Installed versions:");
                    for (String v : versions) {
                        System.out.println("  " + v);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to list installed versions", e);
            }
        } else {
            System.out.println("No installed versions found (cores directory does not exist).");
        }
    }
}
