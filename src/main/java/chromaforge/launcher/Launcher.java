package chromaforge.launcher;

import chromaforge.launcher.util.CommandLine;
import chromaforge.launcher.util.LauncherVersion;
import chromaforge.launcher.debug.Logger;

public class Launcher {
    private static Logger logger = Logger.getLogger("main");
    public static void main(String[] args) {
        Logger.init("logs/launcher.log");
        logger.info("ChromaForge-Launcher version: " + LauncherVersion.VERSION);
        CommandLine.parse_cmdline(args);
        logger.info("Launcher has finished successfully");
    }
}
