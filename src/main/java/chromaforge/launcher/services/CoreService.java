package chromaforge.launcher.services;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import chromaforge.launcher.coders.toml.TomlParser;
import chromaforge.launcher.coders.toml.TomlWriter;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.data.dv.dvValue;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.CoreInfo;
import chromaforge.launcher.util.CoreVersion;
import chromaforge.launcher.util.InstanceInfo;

public class CoreService {
    static public List<CoreVersion> listInstalled(LauncherPaths paths) {
        List<CoreInfo> cores = readRegistry(paths);
        if (cores != null) {
            List<CoreVersion> versions = new ArrayList<>();
            for (CoreInfo core : cores) {
                versions.add(core.version());
            }
            Collections.sort(versions);
            return versions;
        }
        return scanInstalled(paths);
    }

    static public boolean isInstalled(CoreVersion version, LauncherPaths paths) {
        List<CoreVersion> versions = listInstalled(paths);
        if (versions == null) {
            return false;
        }
        return versions.contains(version);
    }

    static private List<CoreVersion> scanInstalled(LauncherPaths paths) {
        Path coresDir = paths.getCoresDir();
        if (Files.exists(coresDir) && Files.isDirectory(coresDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(coresDir, 
                    path -> Files.isDirectory(path) && path.getFileName().toString().startsWith("chromaforge-v"))) {
                List<CoreVersion> versions = new ArrayList<>();
                for (Path p : stream) {
                    String name = p.getFileName().toString();
                    versions.add(
                        CoreVersion.parse(name.substring("chromaforge-v".length()))
                    );
                }
                Collections.sort(versions);
                return versions;
            } catch (IOException e) {
                return null;
            } 
        }
        return null;
    }

    static public List<CoreInfo> readRegistry(LauncherPaths paths) {
        Path file = paths.getCoresLockFile();
        if (!FileUtils.exists(file)) {
            return null;
        }
        List<CoreInfo> cores = new ArrayList<>();
        dvValue root = TomlParser.parse(FileUtils.readString(file));
        if (root instanceof dvObject obj) {
            for (Map.Entry<String, dvValue> e : obj.entries().entrySet()) {
                if (e.getValue() instanceof dvObject object) {
                    cores.add(CoreInfo.fromObject(e.getKey(), object));
                }
            }
        }
        return cores;
    }

    static public void writeRegistry(LauncherPaths paths, List<CoreInfo> cores) {
        dvObject root = new dvObject(new LinkedHashMap<>());
        for (CoreInfo core : cores) {
            dvObject entry = new dvObject(new LinkedHashMap<>());
            entry.entries().put("installed_at", new dvString(core.installedAt()));
            root.entries().put(core.version().toString(), entry);
        }
        FileUtils.writeString(paths.getCoresLockFile(), TomlWriter.stringify(root, ""));
    }

    static public void registerVersion(CoreVersion version, LauncherPaths paths) {
        List<CoreInfo> cores = readRegistry(paths);
        if (cores == null) {
            cores = new ArrayList<>();
        }
        cores.removeIf(c -> c.version().equals(version));
        cores.add(new CoreInfo(
            version,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        ));
        writeRegistry(paths, cores);
    }

    static public void removeVersion(CoreVersion version, LauncherPaths paths) {
        Path versionDir = paths.getCoreDir(version);
        if (!FileUtils.isDir(versionDir)) {
            throw new RuntimeException("Version " + version + " is not installed");
        }

        List<String> users = usedByInstances(version, paths);
        if (!users.isEmpty()) {
            throw new RuntimeException(
                "Cannot remove version " + version +
                ": it is used by instance(s): " + String.join(", ", users)
            );
        }

        FileUtils.deleteRecursive(versionDir);
        FileUtils.deleteRecursive(paths.getChecksumsFile(version));

        List<CoreInfo> cores = readRegistry(paths);
        if (cores != null) {
            cores.removeIf(c -> c.version().equals(version));
            writeRegistry(paths, cores);
        }
    }

    static public List<String> usedByInstances(CoreVersion version, LauncherPaths paths) {
        List<String> users = new ArrayList<>();
        for (InstanceInfo inst : InstanceService.list(paths)) {
            if (version.equals(inst.coreVersion())) {
                users.add(inst.name());
            }
        }
        return users;
    }
}
