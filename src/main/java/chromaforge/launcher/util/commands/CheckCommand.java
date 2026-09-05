package chromaforge.launcher.util.commands;

import java.nio.file.Files;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CheckService;
import chromaforge.launcher.services.CheckService.CheckException;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ConsoleUtils.ConsoleSymbols;

public class CheckCommand extends Command {
    public CheckCommand() {
        this.keyword = "check";
        this.args = "<version>";
        this.help = "verifies the integrity of the engine files";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName = nextArg(args);
        if (!Files.isDirectory(paths.getCoreDir(tagName))) {
            System.err.println(ConsoleColor.RED + ConsoleSymbols.CROSS + " Version " + tagName + " is not installed" + ConsoleColor.RESET);
            return;
        }
        try {
            CheckService.check(tagName, paths, ConsoleUtils.consoleProgress());
        } catch (CheckException e) {
            ConsoleUtils.clearLine();
            System.err.println(ConsoleColor.RED + ConsoleSymbols.CROSS + " The check failed: " + e.getMessage() + ConsoleColor.RESET);
            System.err.println(ConsoleColor.DIM + "  Try reinstalling with 'install " + tagName + "'" + ConsoleColor.RESET);
            return;
        }
        System.out.println(ConsoleSymbols.CHECK + " Check was successful");
    }
}
