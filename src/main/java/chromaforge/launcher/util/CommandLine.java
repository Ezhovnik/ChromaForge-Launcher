package chromaforge.launcher.util;

import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.util.commands.*;

public class CommandLine {
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

    public CommandLine(String[] args) {
        this.args = args;
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
                cmd.execute(args);
                return;
            }
        }
    }

    static public void parse_cmdline(String[] args) {
        CommandLine parser = new CommandLine(args);
        parser.parse_args();
    }
}
