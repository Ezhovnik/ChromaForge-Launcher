package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ProjectInfo;

public class HelpCommand extends Command {
    private List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.keyword = "help";
        this.args = "[command]";
        this.help = "display help for the launcher or a specific command";
        this.commands = commands;
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        if (args.length > 1) {
            for (Command cmd : commands) {
                if (cmd.keyword.equals(args[1])) {
                    System.out.println();
                    System.out.println(cmd.usage());
                    System.out.println();
                    return;
                }
            }
            ConsoleUtils.error("Unknown command: '" + args[1] + "'");
            System.out.println("  " + ConsoleColor.DIM + "Run 'help' to see all available commands" + ConsoleColor.RESET);
            return;
        }

        System.out.println();
        System.out.println("  " + ConsoleColor.BOLD + ConsoleColor.CYAN + ProjectInfo.NAME + ConsoleColor.RESET
            + " " + ConsoleColor.DIM + "v" + ProjectInfo.VERSION + ConsoleColor.RESET
            + "  " + ConsoleColor.DIM + "by " + ProjectInfo.AUTHOR + ConsoleColor.RESET);
        System.out.println("  " + ConsoleColor.DIM + ProjectInfo.ABOUT + ConsoleColor.RESET);
        System.out.println();

        System.out.println("  " + ConsoleColor.BOLD + "Usage:" + ConsoleColor.RESET);
        System.out.println("    " + ConsoleColor.CYAN + "launcher" + ConsoleColor.RESET + " <command> [args]");
        System.out.println();

        System.out.println("  " + ConsoleColor.BOLD + "Commands:" + ConsoleColor.RESET);
        for (Command cmd : commands) {
            System.out.print("    " + ConsoleColor.CYAN + ConsoleColor.BOLD + String.format("%-28s", cmd.keyword + " " + cmd.args) + ConsoleColor.RESET);
            System.out.println("  " + ConsoleColor.DIM + cmd.help + ConsoleColor.RESET);
        }

        System.out.println();
        System.out.println("    " + ConsoleColor.DIM + "Run 'help <command>' for details about a specific command" + ConsoleColor.RESET);
        System.out.println("  " + ConsoleColor.DIM + ProjectInfo.EXAMPLES + ConsoleColor.RESET);
        System.out.println();
        System.out.println("  " + ConsoleColor.DIM + "Launcher Repository: " + ProjectInfo.REPOSITORY + ConsoleColor.RESET);
        System.out.println("  " + ConsoleColor.DIM + "Engine Repository:   " + ProjectInfo.ENGINE_REPOSITORY + ConsoleColor.RESET);
        System.out.println();
    }
}
