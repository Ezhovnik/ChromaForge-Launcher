package chromaforge.launcher.services;

import java.nio.file.Path;

import chromaforge.launcher.run.Runners;
import chromaforge.launcher.util.Platform;

public class LaunchService {
    static public void launch(String version) {
        Path engineDir = CoreService.getEngineDir(version);
        Platform.OS os = Platform.detectOS();
        Runners.of(os).run(engineDir);
    }
}
