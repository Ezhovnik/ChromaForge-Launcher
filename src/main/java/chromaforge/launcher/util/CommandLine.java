package chromaforge.launcher.util;

import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.util.commands.*;
import chromaforge.launcher.debug.Logger;
import chromaforge.launcher.io.LauncherPaths;

public class CommandLine {
    private static Logger logger = Logger.getLogger("command-line");
    private LauncherPaths paths;

    private static final List<Command> allCommands = new ArrayList<>();
    static {
        allCommands.add(new FetchCommand());
        allCommands.add(new InstallCommand());
        allCommands.add(new LaunchCommand());
        allCommands.add(new LsCommand());
        allCommands.add(new RmCommand());
        allCommands.add(new VersionCommand());
        allCommands.add(new HelpCommand(allCommands));
    }

    private String[] args;

    public CommandLine(String[] args, LauncherPaths paths) {
        this.args = args;
        this.paths = paths;
    }

    private void parse_args() {
        String keyword;
        if (args.length == 0) {
            keyword = "help";
        } else {
            keyword = args[0];
        }
        for (Command cmd : allCommands) {
            if (cmd.keyword.equals(keyword)) {
                try {
                    cmd.execute(args, paths);
                } catch (Exception e) {
                    logger.error("An error occurred while running the command: " + e.getMessage());
                }
                return;
            }
        }
    }

    static public void parse_cmdline(String[] args, LauncherPaths paths) {
        CommandLine parser = new CommandLine(args, paths);
        parser.parse_args();
    }
}
