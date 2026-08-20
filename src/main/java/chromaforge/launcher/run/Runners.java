package chromaforge.launcher.run;

import chromaforge.launcher.util.Platform;

public class Runners {
    public static EngineRunner of(Platform.OS os) {
        switch (os) {
            case Platform.OS.WINDOWS:
                return new WindowsRunner();
            case Platform.OS.LINUX:
                return new LinuxRunner();
            case Platform.OS.MACOS:
                return new MacRunner();
            default:
                throw new RuntimeException("The platform is not supported");
        }
    }
}
