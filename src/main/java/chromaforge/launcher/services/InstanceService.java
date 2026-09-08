package chromaforge.launcher.services;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
import chromaforge.launcher.run.Runners;
import chromaforge.launcher.util.CoreVersion;
import chromaforge.launcher.util.InstanceInfo;
import chromaforge.launcher.util.Platform;

public class InstanceService {
    static public List<InstanceInfo> list(LauncherPaths paths) {
        Path file = paths.getInstancesLockFile();
        if (!FileUtils.exists(file)) {
            return new ArrayList<>();
        }
        List<InstanceInfo> instances = new ArrayList<>();
        dvValue root = TomlParser.parse(FileUtils.readString(file));
        if (root instanceof dvObject obj) {
            for (Map.Entry<String, dvValue> e : obj.entries().entrySet()) {
                if (e.getValue() instanceof dvObject entry) {
                    CoreVersion coreVersion = new CoreVersion(0, 0, 0);
                    String createdAt = "";
                    dvValue v = entry.entries().get("core_version");
                    if (v instanceof dvString s) coreVersion = CoreVersion.parse(s.value());
                    v = entry.entries().get("created_at");
                    if (v instanceof dvString s) createdAt = s.value();
                    instances.add(new InstanceInfo(e.getKey(), coreVersion, createdAt));
                }
            }
        }
        return instances;
    }

    static public InstanceInfo get(String name, LauncherPaths paths) {
        for (InstanceInfo inst : list(paths)) {
            if (inst.name().equals(name)) {
                return inst;
            }
        }
        return null;
    }

    static public boolean exists(String name, LauncherPaths paths) {
        return get(name, paths) != null;
    }

    static public void writeRegistry(LauncherPaths paths, List<InstanceInfo> instances) {
        dvObject root = new dvObject(new LinkedHashMap<>());
        for (InstanceInfo inst : instances) {
            dvObject entry = new dvObject(new LinkedHashMap<>());
            entry.entries().put("core_version", new dvString(inst.coreVersion().toString()));
            entry.entries().put("created_at", new dvString(inst.createdAt()));
            root.entries().put(inst.name(), entry);
        }
        FileUtils.writeString(paths.getInstancesLockFile(), TomlWriter.stringify(root, ""));
    }

    static public void create(InstanceInfo info, LauncherPaths paths) {
        if (!info.name().matches("^[\\w\\-. ]+$")) {
            throw new RuntimeException("Invalid name for instance");
        }

        Path instancePath = paths.getInstanceDir(info.name());
        if (exists(info.name(), paths)) {
            throw new RuntimeException("Instance with this name already exists");
        }

        if (!CoreService.isInstalled(info.coreVersion(), paths)) {
            throw new RuntimeException("Core version " + info.coreVersion() + " is not installed");
        }

        FileUtils.mkdir(instancePath);

        dvObject instance = new dvObject(new LinkedHashMap<>());
        instance.entries().put("core_version", new dvString(info.coreVersion().toString()));

        FileUtils.writeString(
            paths.getInstanceDir(info.name()).resolve("instance.toml"),
            TomlWriter.stringify(instance, "") + "\n"
        );

        List<InstanceInfo> instances = list(paths);
        instances.removeIf(i -> i.name().equals(info.name()));
        instances.add(info);
        writeRegistry(paths, instances);
    }

    static public void launch(InstanceInfo info, LauncherPaths paths) {
        if (!CoreService.isInstalled(info.coreVersion(), paths)) {
            throw new RuntimeException("Core version " + info.coreVersion() + " is not installed");
        }

        if (!exists(info.name(), paths)) {
            throw new RuntimeException("Instance '" + info.name() +"' is not exists");
        }

        Path coreDir = paths.getCoreDir(info.coreVersion());
        Path instanceDir = paths.getInstanceDir(info.name());
        Platform.OS os = Platform.detectOS();
        List<String> args = Arrays.asList(
            "--res", coreDir.resolve("res").toString(),
            "--dir", instanceDir.toString(),
            "--project", coreDir.resolve("res").toString()
        );
        Runners.of(os).run(coreDir, args);
    }

    static public void remove(InstanceInfo info, LauncherPaths paths) {
        Path instanceDir = paths.getInstanceDir(info.name());
        if (!FileUtils.isDir(instanceDir)) {
            throw new RuntimeException("Instance '" + info.name() + "' is not exists");
        }
        FileUtils.deleteRecursive(instanceDir);

        List<InstanceInfo> instances = list(paths);
        instances.removeIf(i -> i.name().equals(info.name()));
        writeRegistry(paths, instances);
    }
}
