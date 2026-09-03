package chromaforge.launcher;

import chromaforge.launcher.util.CommandLine;
import chromaforge.launcher.util.LauncherVersion;
import chromaforge.launcher.debug.Logger;
import chromaforge.launcher.io.LauncherPaths;

public class Launcher {
    private static Logger logger = Logger.getLogger("main");
    private LauncherPaths paths;

    public Launcher() {
        paths = new LauncherPaths(".");
    }

    public LauncherPaths getPaths() {
        return paths;
    }

    public static void main(String[] args) {
        Logger.init("logs/launcher.log");
        logger.info("ChromaForge-Launcher version: " + LauncherVersion.VERSION);

        Launcher launcher = new Launcher();

        CommandLine.parse_cmdline(args, launcher.getPaths());
        logger.info("Launcher has finished successfully");
    }
}
