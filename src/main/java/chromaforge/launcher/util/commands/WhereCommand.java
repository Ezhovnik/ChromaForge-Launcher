package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;

public class WhereCommand extends Command {
    public WhereCommand() {
        this.keyword = "where";
        this.args = "";
        this.help = "show data directory";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        System.out.println("  " + ConsoleColor.BOLD + "Data:" + ConsoleColor.RESET +  "   " + ConsoleColor.CYAN + paths.getDataDir().toAbsolutePath() + ConsoleColor.RESET);
    }
}
