package chromaforge.launcher.install;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;

public interface Installer {
    void install(AssetInfo asset, Path installDir);
}
