package chromaforge.launcher.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.nio.file.Path;

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
import chromaforge.launcher.io.RegistryFormat;

public class InstanceService {
    private static final long REGISTRY_FORMAT_VERSION = 1;

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\w\\-. ]+$");
    private static final Set<String> RESERVED_NAMES = Set.of(
        "CON", "PRN", "AUX", "NUL",
        "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
        "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    );

    static public List<InstanceInfo> list(LauncherPaths paths) {
        List<InstanceInfo> instances = new ArrayList<>();
        Path file = paths.getInstancesLockFile();
        if (!FileUtils.exists(file)) {
            return instances;
        }

        dvValue root = TomlParser.parse(FileUtils.readString(file));
        if (root instanceof dvObject obj) {
            RegistryFormat.check(obj, REGISTRY_FORMAT_VERSION);
            for (Map.Entry<String, dvValue> e : obj.entries().entrySet()) {
                if (e.getValue() instanceof dvObject entry) {
                    CoreVersion coreVersion = null;
                    String createdAt = "";

                    dvValue v = entry.entries().get("core_version");
                    if (v instanceof dvString s) coreVersion = CoreVersion.parse(s.value());

                    v = entry.entries().get("created_at");
                    if (v instanceof dvString s) createdAt = s.value();

                    if (coreVersion != null) {
                        instances.add(new InstanceInfo(e.getKey(), coreVersion, createdAt));
                    }
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
        RegistryFormat.write(root, REGISTRY_FORMAT_VERSION);
        FileUtils.writeString(paths.getInstancesLockFile(), TomlWriter.stringify(root, ""));
    }

    static private boolean checkName(String name) {
        if (name == null || name.isBlank()
            || !NAME_PATTERN.matcher(name).matches()
            || name.endsWith(".") || name.endsWith(" ")) {
            return false;
        }
        String stem = name.indexOf('.') >= 0 ? name.substring(0, name.indexOf('.')) : name;
        if (RESERVED_NAMES.contains(stem.toUpperCase(Locale.ROOT))) {
            return false;
        }
        return true;
    }

    static public void create(InstanceInfo info, LauncherPaths paths) {
        if (!checkName(info.name())) {
            throw new RuntimeException("Invalid name for instance");
        }

        List<InstanceInfo> instances = list(paths);
        if (instances.stream().anyMatch(i -> i.name().equals(info.name()))) {
            throw new RuntimeException("Instance '" + info.name() + "' already exists");
        }

        if (!CoreService.isInstalled(info.coreVersion(), paths)) {
            throw new RuntimeException("Core version " + info.coreVersion() + " is not installed");
        }

        FileUtils.mkdir(paths.getInstanceDir(info.name()));

        instances.add(info);
        writeRegistry(paths, instances);
    }

    static public int launch(InstanceInfo info, LauncherPaths paths, List<String> extraArgs) {
        if (!exists(info.name(), paths)) {
            throw new RuntimeException("Instance '" + info.name() +"' is not exists");
        }

        if (!CoreService.isInstalled(info.coreVersion(), paths)) {
            throw new RuntimeException("Core version " + info.coreVersion() + " is not installed");
        }

        Path coreDir = paths.getCoreDir(info.coreVersion());
        Path instanceDir = paths.getInstanceDir(info.name());
        Platform.OS os = Platform.detectOS();
        List<String> args = new ArrayList<>(Arrays.asList(
            "--res", coreDir.resolve("res").toString(),
            "--dir", instanceDir.toString(),
            "--project", coreDir.resolve("res").toString()
        ));
        args.addAll(extraArgs);
        return Runners.of(os).run(coreDir, args);
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
