package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;
import chromaforge.launcher.util.ConsoleUtils;

public class RmCommand extends Command {

    public RmCommand() {
        this.keyword = "rm";
        this.args = "<version>";
        this.help = "removes the specified engine version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String version = nextArg(args);
        System.out.println("Removing '" + version + "'...");

        try {
            CoreService.removeVersion(version, paths);
            ConsoleUtils.success("Version " + version + " removed");
        } catch (Exception e) {
            ConsoleUtils.error("Could not remove " + version + ": " + e.getMessage());
        }
    }
}
