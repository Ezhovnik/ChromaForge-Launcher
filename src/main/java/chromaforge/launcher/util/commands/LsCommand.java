package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.CoreVersion;

public class LsCommand extends Command {
    public LsCommand() {
        this.keyword = "ls";
        this.args = "";
        this.help = "list installed engine versions";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        List<CoreVersion> installed = CoreService.listInstalled(paths);
        if (installed == null || installed.isEmpty()) {
            ConsoleUtils.warn("No installed versions found");
            ConsoleUtils.tip("  Use 'launcher install <version>' to install an engine version");
            return;
        }

        System.out.println("Installed versions " + ConsoleUtils.ConsoleColor.DIM + "(" + installed.size() + ")" + ConsoleUtils.ConsoleColor.RESET);
        for (CoreVersion v : installed) {
            System.out.println("  " + ConsoleUtils.ConsoleColor.GREEN + ConsoleUtils.ConsoleSymbols.CHECK + ConsoleUtils.ConsoleColor.RESET
                + " " + ConsoleUtils.ConsoleColor.BOLD + "v" + v.toString() + ConsoleUtils.ConsoleColor.RESET);
        }
    }
}
