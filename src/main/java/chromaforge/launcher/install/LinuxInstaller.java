package chromaforge.launcher.install;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

import chromaforge.launcher.github.AssetInfo;

public final class LinuxInstaller implements Installer {
    private final Downloader downloader;

    public LinuxInstaller(Downloader downloader) {
        this.downloader = downloader;
    }

    @Override
    public void install(AssetInfo asset, Path installDir) {
        System.out.println("LinuxInstaller: installing " + asset.name + " to " + installDir);
        try {
            Files.createDirectories(installDir);
            Path target = installDir.resolve("ChromaForge.AppImage");
            Path temp = Files.createTempFile("chromaforge", ".AppImage");

            try {
                System.out.println("Downloading from: " + asset.browserDownloadUrl);
                downloader.download(asset.browserDownloadUrl, temp, null);
                System.out.println("Download complete, moving to " + target);
                Files.move(temp, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Setting executable permissions...");
                setExecutable(target);
                System.out.println("Installation successful: " + target);
            } finally {
                Files.deleteIfExists(temp);
            }

            if (!Files.exists(target)) {
                throw new RuntimeException("Target file missing after installation: " + target);
            }
            if (!Files.isExecutable(target)) {
                System.out.println("Warning: " + target + " is not executable");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to install " + asset.name, e);
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
