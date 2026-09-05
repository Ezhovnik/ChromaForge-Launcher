package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ConsoleUtils.ConsoleSymbols;

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
            System.out.println(ConsoleColor.GREEN + ConsoleSymbols.CHECK + " Version " + version + " removed" + ConsoleColor.RESET);
        } catch (Exception e) {
            System.err.println(ConsoleColor.RED + ConsoleSymbols.CROSS + " Could not remove " + version + ": " + e.getMessage() + ConsoleColor.RESET);
        }
    }
}
