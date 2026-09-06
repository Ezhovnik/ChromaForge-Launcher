package chromaforge.launcher.io;

import java.nio.file.Path;
import java.util.Optional;

public class LauncherPaths {
    private final Path dataDir;

    public LauncherPaths(Path dataDir) {
        this.dataDir = dataDir;
    }

    public LauncherPaths(String dataDir) {
        this.dataDir = Path.of(dataDir);
    }

    public Path getDataDir() {
        return this.dataDir;
    }

    public Path getCoresDir() {
        return dataDir.resolve("cores");
    }

    public Path getCoreDir(String v) {
        return getCoresDir().resolve("chromaforge-v" + v);
    }

    public Path getMetaDir() {
        Path path = dataDir.resolve("meta");
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

    static public Path getExeDir() {
        ProcessHandle currentProcess = ProcessHandle.current();
        Optional<String> command = currentProcess.info().command();
        if (command.isPresent()) {
            return Path.of(command.get()).getParent();
        }
        return Path.of(".").toAbsolutePath().normalize();
    }
}
