package chromaforge.launcher.install;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;

public final class WindowsInstaller implements Installer {
    private final Downloader downloader;

    public WindowsInstaller(Downloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void install(AssetInfo asset, Path installDir) {
        // Тут ничего нет...
    }
}
