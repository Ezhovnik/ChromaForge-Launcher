package chromaforge.launcher.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import chromaforge.launcher.coders.sha256.Sha256;
import chromaforge.launcher.interfaces.Progress;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.CoreVersion;

public class CheckService {
    public static class CheckException extends RuntimeException {
        public CheckException(String message) {
            super(message);
        }
        public CheckException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static void writeChecksumsFile(Path file, Map<String, String> hashes) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : hashes.entrySet()) {
            sb.append(entry.getValue())
            .append("  ")
            .append(entry.getKey())
            .append("\n");
        }
        FileUtils.writeString(file, sb.toString());
    }

    private static Map<String, String> readChecksumsFile(Path file) {
        Map<String, String> hashes = new LinkedHashMap<>();
        List<String> lines = FileUtils.readLines(file);
        for (String line : lines) {
            String[] parts = line.split("  ", 2);
            hashes.put(parts[1], parts[0]);
        }
        return hashes;
    }

    private static final List<String> ignoredPatterns = List.of(
        "res/content/"
    );

    private static boolean isIgnored(Path file, Path versionDir) {
        String relative = versionDir.relativize(file).toString().replace('\\', '/');
        for (String pattern : ignoredPatterns) {
            if (pattern.endsWith("/")) {
                String dirName = pattern.substring(0, pattern.length() - 1);
                if (relative.startsWith(dirName + "/") || relative.equals(dirName)) {
                    return true;
                }
            } else if (pattern.startsWith("*.")) {
                String ext = pattern.substring(1);
                if (relative.endsWith(ext)) {
                    return true;
                }
            } else {
                if (relative.equals(pattern) || relative.startsWith(pattern + "/")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void createChecksumsFile(CoreVersion v, LauncherPaths paths) {
        Path versionDir = paths.getCoreDir(v);
        Map<String, String> hashes = new LinkedHashMap<>();

        try (Stream<Path> walk = Files.walk(versionDir)) {
            for (Path file : walk.filter(Files::isRegularFile).toList()) {
                if (isIgnored(file, versionDir)) continue;
                String relative = versionDir.relativize(file).toString();
                try (InputStream is = Files.newInputStream(file)) {
                    String hash = Sha256.hash(is);
                    hashes.put(relative, hash);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create checksums file for " + v + " : " + e.getMessage());
        }

        Path checksums = paths.getChecksumsFile(v);
        writeChecksumsFile(checksums, hashes);
    }

    public static void check(CoreVersion v, LauncherPaths paths, Progress progress) {
        Path checksums = paths.getChecksumsFile(v);
        Map<String, String> hashes = readChecksumsFile(checksums);

        Path versionDir = paths.getCoreDir(v);
        long total = hashes.size();
        long done = 0;
        for (Map.Entry<String, String> entry : hashes.entrySet()) {
            String relativePath = entry.getKey();
            String expectedHash = entry.getValue();

            Path file = versionDir.resolve(relativePath);
            if (isIgnored(file, versionDir)) continue;

            if (!Files.exists(file)) {
                throw new CheckException("File " + file + " was not found");
            } else {
                try (InputStream is = Files.newInputStream(file)) {
                    String actualHash = Sha256.hash(is);
                    if (!actualHash.equals(expectedHash)) {
                        throw new CheckException("File " + file + " is corrupted");
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to check version " + v + " files", e);
                }
            }

            done++;
            if (progress != null) {
                progress.onProgress(done, total);
            }
        }

        if (total == 0 && progress != null) {
            progress.onProgress(0, 0);
        }
    }
}
