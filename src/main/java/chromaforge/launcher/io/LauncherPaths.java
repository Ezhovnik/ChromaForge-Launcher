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

    public Path getMetaDir() {
        Path path = workDir.resolve("meta");
        if (!FileUtils.exists(path)) {
            FileUtils.mkdir(path);
        }
        return path;
    }

    public Path getChecksumsDir() {
        Path path = getMetaDir().resolve("checksums");
        if (!FileUtils.exists(path)) {
            FileUtils.mkdir(path);
        }
        return path;
    }

    public Path getChecksumsFile(String v) {
        return getChecksumsDir().resolve(v + ".sha256");
    }
}
