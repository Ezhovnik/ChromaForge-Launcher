package chromaforge.launcher.install;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;

public final class MacInstaller implements Installer {
    private final Downloader downloader;

    public MacInstaller(Downloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void install(AssetInfo asset, Path installDir) {
        // Тут ничего нет...
    }
}
