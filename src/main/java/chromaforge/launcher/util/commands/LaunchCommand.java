package chromaforge.launcher.util.commands;

import chromaforge.launcher.services.CheckService;
import chromaforge.launcher.services.CheckService.CheckException;
import chromaforge.launcher.services.LaunchService;
import chromaforge.launcher.debug.Logger;
import chromaforge.launcher.io.LauncherPaths;

public class LaunchCommand extends Command {
    private static Logger logger = Logger.getLogger("launch-command");

    public LaunchCommand() {
        this.keyword = "launch";
        this.args = "<version>";
        this.help = "runs the specified engine version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName;
        String nextArg = nextArg(args);
        if (nextArg.equals("--check")) {
            logger.info("Starting check...");
            tagName = nextArg(args);
            try {
                CheckService.check(tagName, paths);
            } catch (CheckException e) {
                System.err.println("The check failed: " + e.getMessage());
                return;
            }
            System.out.println("Сheck was successful");
        } else {
            tagName = nextArg;
        }
        logger.info("Launch '" + tagName + "'...");

        LaunchService.launch(tagName, paths);
    }
}
