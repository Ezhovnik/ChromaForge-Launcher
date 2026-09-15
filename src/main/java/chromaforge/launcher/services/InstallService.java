package chromaforge.launcher.services;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.install.InstallListener;
import chromaforge.launcher.install.Installers;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.CoreVersion;
import chromaforge.launcher.util.Platform;

public class InstallService {

    static public void install(AssetInfo asset, CoreVersion version, LauncherPaths paths, InstallListener listener) {
        Path finalDir = paths.getCoreDir(version);
        Path staging = null;

        try {
            staging = FileUtils.createTempDirectory(paths.getCoresDir(), ".chromaforge-v" + version + "-");
            Installers.of(Platform.detectOS()).install(asset, staging, listener);
            CheckService.createChecksumsFile(staging, paths.getChecksumsFile(version));
            if (FileUtils.isDir(finalDir)) {
                FileUtils.deleteRecursive(finalDir);
            }
            FileUtils.move(staging, finalDir);
            staging = null;
        } catch (Exception e) {
            if (staging != null) {
                FileUtils.deleteRecursive(staging);
            }
            throw e;
        }

        CoreService.registerVersion(version, paths);
    }
}
