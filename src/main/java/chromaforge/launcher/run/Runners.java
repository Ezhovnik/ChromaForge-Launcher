package chromaforge.launcher.run;

import chromaforge.launcher.util.Platform;

public class Runners {
    public static EngineRunner of(Platform.OS os) {
        switch (os) {
            case WINDOWS:
                return new WindowsRunner();
            case LINUX:
                return new LinuxRunner();
            case MACOS:
                return new MacRunner();
            default:
                throw new RuntimeException("The platform is not supported");
        }
    }
}
