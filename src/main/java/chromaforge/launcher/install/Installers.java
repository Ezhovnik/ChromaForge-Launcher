package chromaforge.launcher.install;

import chromaforge.launcher.coders.zip.Unzipper;
import chromaforge.launcher.util.Platform;

public class Installers {
    private Installers() {}

    public static Installer of(Platform.OS os) {
        switch (os) {
            case WINDOWS:
                return new WindowsInstaller(new Downloader(), new Unzipper());
            case LINUX:
                return new LinuxInstaller(new Downloader());
            case MACOS:
                return new MacInstaller(new Downloader());
            default:
                throw new RuntimeException("The platform is not supported");
        }
    }
}
