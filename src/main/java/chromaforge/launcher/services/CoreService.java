package chromaforge.launcher.services;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;

public class CoreService {
    static public List<String> listInstalled(LauncherPaths paths) {
        Path coresDir = paths.getCoresDir();
        if (Files.exists(coresDir) && Files.isDirectory(coresDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(coresDir, 
                    path -> Files.isDirectory(path) && path.getFileName().toString().startsWith("chromaforge-v"))) {
                List<String> versions = new ArrayList<>();
                for (Path p : stream) {
                    String name = p.getFileName().toString();
                    versions.add(name.substring("chromaforge-v".length()));
                }
                Collections.sort(versions);
                return versions;
            } catch (IOException e) {
                return null;
            } 
        }
        return null;
    }

    static public void removeVersion(String version, LauncherPaths paths) {
        Path versionDir = paths.getCoreDir(version);
        if (!Files.exists(versionDir)) {
            throw new RuntimeException("Version " + version + " is not installed");
        }
        FileUtils.deleteRecursive(versionDir);
        FileUtils.deleteRecursive(paths.getChecksumsFile(version));
    }
}
