package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CheckService;
import chromaforge.launcher.services.CheckService.CheckException;

public class CheckCommand extends Command {
    public CheckCommand() {
        this.keyword = "check";
        this.args = "<version>";
        this.help = "verifies the integrity of the engine files";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName = nextArg(args);
        try {
            CheckService.check(tagName, paths);
        } catch (CheckException e) {
            System.err.println("The check failed: " + e.getMessage());
            return;
        }
        System.out.println("Сheck was successful");
    }
}
