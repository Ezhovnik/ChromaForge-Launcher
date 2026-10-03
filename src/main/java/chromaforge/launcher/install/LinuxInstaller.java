package chromaforge.launcher.install;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.io.FileUtils;

public final class LinuxInstaller implements Installer {
    private final Downloader downloader;

    public LinuxInstaller(Downloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void install(AssetInfo asset, Path installDir, InstallListener listener, InstallSettings settings) {
        try {
            FileUtils.mkdirs(installDir);
            Path target = installDir.resolve("ChromaForge.AppImage");
            Path temp = Files.createTempFile(installDir, "chromaforge", ".zip");
            listener.onStatus("Downloading");
            downloader.download(
                asset.browserDownloadUrl,
                temp,
                listener,
                settings.skipChecksums() ? null : asset.sha256,
                settings.canRetry() ? 5 : 1
            );
            Files.move(temp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            setExecutable(target);
            Files.deleteIfExists(temp);

            if (!Files.exists(target)) {
                throw new RuntimeException("Target file missing after installation: " + target);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while installing " + asset.name, e);
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException("Failed to install " + asset.name + " : " + e.getMessage(), e);
        }
    }

    private void setExecutable(Path file) throws IOException {
        if (Files.getFileStore(file).supportsFileAttributeView(PosixFileAttributeView.class)) {
            Set<PosixFilePermission> perms = EnumSet.of(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ,
                PosixFilePermission.OTHERS_EXECUTE
            );
            Files.setPosixFilePermissions(file, perms);
        } else {
            try {
                ProcessBuilder pb = new ProcessBuilder("chmod", "+x", file.toString());
                pb.inheritIO().start().waitFor();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while setting executable", e);
            }
        }
    }
}
