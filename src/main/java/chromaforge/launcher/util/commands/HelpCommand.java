package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ProjectInfo;

public class HelpCommand extends Command {
    private List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.keyword = "help";
        this.args = "";
        this.help = "display this help";

        this.commands = commands;
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        System.out.println();
        System.out.println("  " + ConsoleColor.BOLD + ConsoleColor.CYAN + ProjectInfo.NAME + ConsoleColor.RESET
            + " " + ConsoleColor.DIM + "v" + ProjectInfo.VERSION + ConsoleColor.RESET
            + "  " + ConsoleColor.DIM + "by " + ProjectInfo.AUTHOR + ConsoleColor.RESET);
        System.out.println("  " + ConsoleColor.DIM + ProjectInfo.ABOUT + ConsoleColor.RESET);
        System.out.println();

        System.out.println("  " + ConsoleColor.BOLD + "Usage:" + ConsoleColor.RESET);
        System.out.println("    " + ConsoleColor.CYAN + "launcher.bat" + ConsoleColor.RESET + " <command> [args]");
        System.out.println();

        System.out.println("  " + ConsoleColor.BOLD + "Commands:" + ConsoleColor.RESET);
        for (Command cmd : commands) {
            System.out.print("    " + ConsoleColor.CYAN + ConsoleColor.BOLD + String.format("%-28s", cmd.keyword + " " + cmd.args) + ConsoleColor.RESET);
            System.out.println("  " + ConsoleColor.DIM + cmd.help + ConsoleColor.RESET);
        }

        System.out.println();
        System.out.println("  " + ConsoleColor.DIM + ProjectInfo.EXAMPLES + ConsoleColor.RESET);
        System.out.println();
        System.out.println("  " + ConsoleColor.DIM + "Launcher Repository: " + ProjectInfo.REPOSITORY + ConsoleColor.RESET);
        System.out.println("  " + ConsoleColor.DIM + "Engine Repository:   " + ProjectInfo.ENGINE_REPOSITORY + ConsoleColor.RESET);
        System.out.println();
    }
}
