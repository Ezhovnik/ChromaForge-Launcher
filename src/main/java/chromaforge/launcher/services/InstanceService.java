package chromaforge.launcher.services;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import chromaforge.launcher.coders.toml.TomlWriter;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.run.Runners;
import chromaforge.launcher.util.InstanceInfo;
import chromaforge.launcher.util.Platform;

public class InstanceService {
    static public void create(InstanceInfo info, LauncherPaths paths) {
        if (!info.name().matches("^[\\w\\-. ]+$")) {
            throw new RuntimeException("Invalid name for instance");
        }

        Path instancePath = paths.getInstanceDir(info.name());
        if (FileUtils.isDir(instancePath)) {
            throw new RuntimeException("Instance with this name already exists");
        }

        if (!FileUtils.isDir(paths.getCoreDir(info.coreVersion()))) {
            throw new RuntimeException("Core version " + info.coreVersion() + " is not installed");
        }

        FileUtils.mkdir(instancePath);

        dvObject instance = new dvObject(new LinkedHashMap<>());
        instance.entries().put("core_version", new dvString(info.coreVersion()));

        FileUtils.writeString(
            paths.getInstanceDir(info.name()).resolve("instance.toml"),
            TomlWriter.stringify(instance, "") + "\n"
        );
    }

    static public void launch(InstanceInfo info, LauncherPaths paths) {
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

    }
}
