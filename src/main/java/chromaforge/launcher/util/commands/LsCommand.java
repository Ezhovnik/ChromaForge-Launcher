package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ConsoleUtils.ConsoleSymbols;

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
            System.out.println(ConsoleColor.YELLOW + ConsoleSymbols.CROSS + " No installed versions found" + ConsoleColor.RESET);
            System.out.println(ConsoleColor.DIM + "  Use 'run.bat install <version>' to install an engine version" + ConsoleColor.RESET);
            return;
        }

        System.out.println("Installed versions " + ConsoleColor.DIM + "(" + installed.size() + ")" + ConsoleColor.RESET);
        for (String v : installed) {
            System.out.println("  " + ConsoleColor.GREEN + ConsoleSymbols.CHECK + ConsoleColor.RESET
                + " " + ConsoleColor.BOLD + "v" + v + ConsoleColor.RESET);
        }
    }
}
