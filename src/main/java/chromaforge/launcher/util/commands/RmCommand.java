package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.CoreVersion;

public class RmCommand extends Command {

    public RmCommand() {
        this.keyword = "rm";
        this.args = "<version>";
        this.help = "removes the specified engine version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName = nextArg(args);
        System.out.println("Removing '" + tagName + "'...");

        try {
            CoreService.removeVersion(CoreVersion.parse(tagName), paths);
            ConsoleUtils.success("Version '" + tagName + "' removed");
        } catch (Exception e) {
            ConsoleUtils.error("Could not remove '" + tagName + "' : " + e.getMessage());
        }
    }
}
