package chromaforge.launcher.install;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import chromaforge.launcher.coders.zip.Unzipper;
import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.io.FileUtils;

public final class WindowsInstaller implements Installer {
    private final Downloader downloader;
    private final Unzipper unzipper;

    public WindowsInstaller(Downloader downloader, Unzipper unzipper) {
        this.downloader = downloader;
        this.unzipper = unzipper;
    }

    @Override
    public void install(AssetInfo asset, Path installDir, InstallListener listener, InstallSettings settings) {
        Path temp = null;
        try {
            FileUtils.mkdirs(installDir);
            temp = Files.createTempFile("chromaforge", ".zip");

            listener.onStatus("Downloading");
            downloader.download(
                asset.browserDownloadUrl,
                temp,
                listener,
                asset.sha256,
                settings.canRetry() ? 5 : 1
            );
            listener.onStatus("Extracting");
            unzipper.unzip(temp, installDir);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while installing " + asset.name, e);
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to install " + asset.name + " : " + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(temp);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete temp-file " + temp + " : " + e.getMessage(), e);
            }
        }
    }
}
