package chromaforge.launcher;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import chromaforge.launcher.util.CommandLine;
import chromaforge.launcher.util.Platform;
import chromaforge.launcher.io.LauncherPaths;

public class Launcher {
    private LauncherPaths paths;

    public Launcher() {
        paths = new LauncherPaths(".");
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
