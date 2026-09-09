package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.ArgParser;

public abstract class Command {
    public String keyword;
    public String args;
    public String help;

    protected final ArgParser parser = new ArgParser();

    public Command() {
        registerArgs();
    }

    protected void registerArgs() {
    }

    public abstract void execute(String[] args, LauncherPaths paths);

    public String usage() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(keyword);
        if (!args.isEmpty()) {
            sb.append(' ').append(args);
        }
        sb.append("\n    ").append(help);

        String flags = parser.flagsHelp();
        if (!flags.isEmpty()) {
            sb.append("\n\n  Options:\n").append(flags);
        }
        return sb.toString();
    }

    protected String requiredArg(ArgParser parser, int index, String name) {
        if (parser.positionals().size() <= index) {
            throw new RuntimeException("Missing argument '" + name + "'");
        }
        return parser.positionals().get(index);
    }
}
