package chromaforge.launcher.io;

import java.nio.file.Path;
import java.util.Optional;

import chromaforge.launcher.util.CoreVersion;

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
        Path path = dataDir.resolve("cores");
        if (!FileUtils.exists(path)) {
            FileUtils.mkdirs(path);
        }
        return path;
    }

    public Path getCoreDir(CoreVersion v) {
        return getCoresDir().resolve("chromaforge-v" + v.toString());
    }

    public Path getCoresLockFile() {
        return getCoresDir().resolve("lock.toml");
    }

    public Path getInstancesDir() {
        Path path = dataDir.resolve("instances");
        if (!FileUtils.exists(path)) {
            FileUtils.mkdirs(path);
        }
        return path;
    }

    public Path getInstanceDir(String name) {
        return getInstancesDir().resolve(name);
    }

    public Path getInstancesLockFile() {
        return getInstancesDir().resolve("lock.toml");
    }

    public Path getMetaDir() {
        Path path = dataDir.resolve("meta");
        if (!FileUtils.exists(path)) {
            FileUtils.mkdirs(path);
        }
        return path;
    }

    public Path getChecksumsDir() {
        Path path = getMetaDir().resolve("checksums");
        if (!FileUtils.exists(path)) {
            FileUtils.mkdirs(path);
        }
        return path;
    }

    public Path getChecksumsFile(CoreVersion v) {
        return getChecksumsDir().resolve(v.toString() + ".sha256");
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
