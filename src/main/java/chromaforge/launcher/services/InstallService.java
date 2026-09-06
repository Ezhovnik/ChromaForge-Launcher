package chromaforge.launcher.services;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.install.Installers;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.Platform;

public class InstallService {

    static public void install(ReleaseInfo release, LauncherPaths paths) {
        Platform.OS os = Platform.detectOS();
        String version = release.tagName.substring(1);
        Path installDir = paths.getCoreDir(version);
        AssetInfo asset = AssetInfo.fromRelease(release);

        Installers.of(os).install(asset, installDir);
        CheckService.createChecksumsFile(version, paths);
    }
}
