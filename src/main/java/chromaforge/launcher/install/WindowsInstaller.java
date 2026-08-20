package chromaforge.launcher.install;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import chromaforge.launcher.coders.zip.Unzipper;
import chromaforge.launcher.github.AssetInfo;

public final class WindowsInstaller implements Installer {
    private final Downloader downloader;
    private final Unzipper unzipper;

    public WindowsInstaller(Downloader downloader, Unzipper unzipper) {
        this.downloader = downloader;
        this.unzipper = unzipper;
    }

    @Override
    public void install(AssetInfo asset, Path installDir) {
        Path temp = null;
        try {
            temp = Files.createTempFile("chromaforge", ".zip");
            Files.createDirectories(installDir);
            downloader.download(asset.browserDownloadUrl, temp, null);
            unzipper.unzip(temp, installDir);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to install " + asset.name, e);
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ignored) {
                    // Удаление temp-файла — не повод падать после успешной установки
                }
            }
        }

    }
}
