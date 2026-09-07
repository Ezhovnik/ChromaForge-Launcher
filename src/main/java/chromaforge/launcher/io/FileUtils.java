package chromaforge.launcher.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import chromaforge.launcher.util.ConsoleUtils;

import java.io.IOException;

public class FileUtils {

    public static String readString(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + path, e);
        }
    }

    public static List<String> readLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file " + path, e);
        }
    }

    public static void writeString(Path path, String content) {
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file " + path, e);
        }
    }

    public static void deleteRecursive(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            Iterator<Path> it = walk.sorted(Comparator.reverseOrder()).iterator();
            while (it.hasNext()) {
                Path p = it.next();
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete " + p, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to traverse directory " + path, e);
        }
    }

    public static void mkdir(Path path) {
        if (Files.exists(path)) return;
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            ConsoleUtils.error("Failed to create directory " + path + ": " + e.getMessage());
        }
    }

    public static void mkdirs(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            ConsoleUtils.error("Failed to create directory " + path + ": " + e.getMessage());
        }
    }

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    public static boolean isDir(Path path) {
        return Files.isDirectory(path);
    }
} 
