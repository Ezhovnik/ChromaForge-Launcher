package chromaforge.launcher;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import chromaforge.launcher.util.CommandLine;
import chromaforge.launcher.util.Platform;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;

public class Launcher {
    private LauncherPaths paths;
    private boolean portable;

    public Launcher() {
        Path exeDir = LauncherPaths.getExeDir();
        portable = FileUtils.exists(exeDir.resolve("settings.toml"));

        Path dataDir;

        if (portable) {
            dataDir = exeDir;
        } else {
            dataDir = Platform.getLocalAppDataDir().resolve("ChromaForge-Launcher");
            FileUtils.mkdirs(dataDir);
        }

        paths = new LauncherPaths(dataDir);
    }

    public LauncherPaths getPaths() {
        return paths;
    }

    public static void main(String[] args) {
        Platform.configureEncoding();

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        Launcher launcher = new Launcher();
        CommandLine.parse_cmdline(args, launcher.getPaths());
    }
}
