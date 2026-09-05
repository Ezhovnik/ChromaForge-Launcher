package chromaforge.launcher.util.commands;

import java.nio.file.Files;

import chromaforge.launcher.services.CheckService;
import chromaforge.launcher.services.CheckService.CheckException;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.services.LaunchService;
import chromaforge.launcher.io.LauncherPaths;

public class LaunchCommand extends Command {

    public LaunchCommand() {
        this.keyword = "launch";
        this.args = "[--check] <version>";
        this.help = "runs the specified engine version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName;
        String nextArg = nextArg(args);
        if (nextArg.equals("--check")) {
            System.out.println("Starting check...");
            tagName = nextArg(args);
            if (!isInstalled(tagName, paths)) {
                ConsoleUtils.error("Version " + tagName + " is not installed");
                return;
            }
            try {
                CheckService.check(tagName, paths, ConsoleUtils.consoleProgress());
            } catch (CheckException e) {
                ConsoleUtils.clearLine();
                ConsoleUtils.error("The check failed: " + e.getMessage());
                System.err.println(ConsoleColor.DIM + "  Try reinstalling with 'install " + tagName + "'" + ConsoleColor.RESET);
                return;
            }
            ConsoleUtils.success("Check was successful");
        } else {
            tagName = nextArg;
            if (!isInstalled(tagName, paths)) {
                ConsoleUtils.error("Version " + tagName + " is not installed");
                return;
            }
        }

        System.out.println("Launching '" + tagName + "'...");
        LaunchService.launch(tagName, paths);
    }

    private boolean isInstalled(String version, LauncherPaths paths) {
        return Files.isDirectory(paths.getCoreDir(version));
    }
}
