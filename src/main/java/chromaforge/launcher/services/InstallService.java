package chromaforge.launcher.services;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.install.Installers;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.Platform;
import chromaforge.launcher.debug.Logger;

public class InstallService {
    static private Logger logger = Logger.getLogger("install-service");

    static public void install(ReleaseInfo release, LauncherPaths paths) {
        Platform.OS os = Platform.detectOS();
        Path installDir = paths.getCoreDir(release.tagName.substring(1));
        AssetInfo asset = AssetInfo.fromRelease(release);

        logger.info("Installing " + release.tagName + " ...");
        Installers.of(os).install(asset, installDir);
        logger.info("Successfully installed " + release.tagName);
    }
}
