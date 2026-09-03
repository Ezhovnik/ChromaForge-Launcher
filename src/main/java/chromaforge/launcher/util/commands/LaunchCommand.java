package chromaforge.launcher.util.commands;

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
        String tagName = nextArg(args);
        logger.info("Launch '" + tagName + "'...");

        LaunchService.launch(tagName, paths);
    }
}
