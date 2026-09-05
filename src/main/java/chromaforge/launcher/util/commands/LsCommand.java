package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;

public class LsCommand extends Command {
    public LsCommand() {
        this.keyword = "ls";
        this.args = "";
        this.help = "list installed engine versions";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        List<String> installed = CoreService.listInstalled(paths);
        if (installed == null || installed.isEmpty()) {
            System.out.println("No installed versions found");
        } else {
            System.out.println("Installed versions:");
            for (String v : installed) {
                System.out.println("  " + v);
            }
        }
    }
}
