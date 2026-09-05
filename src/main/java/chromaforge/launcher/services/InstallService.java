package chromaforge.launcher.services;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.install.Installers;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.ConsoleUtils.ConsoleSymbols;
import chromaforge.launcher.util.Platform;

public class InstallService {

    static public void install(ReleaseInfo release, LauncherPaths paths) {
        Platform.OS os = Platform.detectOS();
        Path installDir = paths.getCoreDir(release.tagName.substring(1));
        AssetInfo asset = AssetInfo.fromRelease(release);

        System.out.println("Installing " + release.tagName + "...");
        Installers.of(os).install(asset, installDir);
        CheckService.createChecksumsFile(release.tagName.substring(1), paths);
        System.out.println(ConsoleSymbols.CHECK + " Successfully installed " + release.tagName);
    }
}
