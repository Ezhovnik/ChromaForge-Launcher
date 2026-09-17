package chromaforge.launcher;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import chromaforge.launcher.util.CommandLine;
import chromaforge.launcher.util.ExitCode;
import chromaforge.launcher.util.Platform;
import chromaforge.launcher.util.UsageException;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.util.ConsoleUtils;
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

    public static boolean isDebug() {
        return "true".equals(System.getProperty("cfl.debug", "false")) ||
            "true".equals(System.getenv("CFL_DEBUG"));
    }

    public static void main(String[] args) {
        try {
            Platform.configureEncoding();

            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

            Launcher launcher = new Launcher();
            int code = CommandLine.parse_cmdline(args, launcher.getPaths()).code();
            System.exit(code);
        } catch (UsageException e) {
            ConsoleUtils.error(e.getMessage());
            ConsoleUtils.tip("  Run 'help' for usage");
            System.exit(ExitCode.USAGE.code());
        } catch (Exception t) {
            String msg = t.getMessage();
            if (msg == null) {
                msg = t.getClass().getSimpleName();
            }
            ConsoleUtils.error("Fatal error: " + msg);
            if (isDebug()) {
                t.printStackTrace();
            }
            System.exit(ExitCode.FAILURE.code());
        }
    }
}
