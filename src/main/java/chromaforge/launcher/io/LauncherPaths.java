package chromaforge.launcher.io;

import java.nio.file.Path;

public class LauncherPaths {
    private final Path workDir;

    public LauncherPaths(Path workDir) {
        this.workDir = workDir;
    }

    public LauncherPaths(String workDir) {
        this.workDir = Path.of(workDir);
    }

    public Path getCoresDir() {
        return workDir.resolve("cores");
    }

    public Path getCoreDir(String v) {
        return getCoresDir().resolve("chromaforge-v" + v);
    }
}
