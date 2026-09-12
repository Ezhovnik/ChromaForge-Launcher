package chromaforge.launcher.services;

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
import chromaforge.launcher.io.RegistryFormat;

public class CoreService {
    private static final long REGISTRY_FORMAT_VERSION = 1;

    static public List<CoreVersion> listInstalled(LauncherPaths paths) {
        List<CoreInfo> cores = readRegistry(paths);
        if (cores == null) {
            throw new RuntimeException("Could not find the file with version information (cores/lock.toml)");
        }
        List<CoreVersion> versions = new ArrayList<>();
        for (CoreInfo core : cores) {
            versions.add(core.version());
        }
        Collections.sort(versions);
        return versions;
    }

    static public boolean isInstalled(CoreVersion version, LauncherPaths paths) {
        List<CoreVersion> versions = listInstalled(paths);
        if (versions == null) {
            return false;
        }
        return versions.contains(version);
    }

    static public List<CoreInfo> readRegistry(LauncherPaths paths) {
        Path file = paths.getCoresLockFile();
        if (!FileUtils.exists(file)) {
            return null;
        }
        List<CoreInfo> cores = new ArrayList<>();
        dvValue root = TomlParser.parse(FileUtils.readString(file));
        if (root instanceof dvObject obj) {
            RegistryFormat.check(obj, REGISTRY_FORMAT_VERSION);
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
        RegistryFormat.write(root, REGISTRY_FORMAT_VERSION);
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
