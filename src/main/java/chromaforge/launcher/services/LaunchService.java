package chromaforge.launcher.services;

import java.nio.file.Path;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.run.Runners;
import chromaforge.launcher.util.Platform;

public class LaunchService {
    static public void launch(String version, LauncherPaths paths) {
        Path engineDir = paths.getCoreDir(version);
        Platform.OS os = Platform.detectOS();
        Runners.of(os).run(engineDir);
    }
}
