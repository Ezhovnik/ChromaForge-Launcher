package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.ArgParser;
import chromaforge.launcher.util.ExitCode;
import chromaforge.launcher.util.UsageException;

public abstract class Command {
    public String keyword;
    public String args;
    public String help;
    public String long_help;

    protected final ArgParser parser = new ArgParser();

    public Command() {
        registerArgs();
    }

    protected void registerArgs() {
    }

    public abstract ExitCode execute(String[] args, LauncherPaths paths);

    public String usage() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(keyword);
        if (!args.isEmpty()) {
            sb.append(' ').append(args);
        }
        sb.append("\n    ").append(long_help != null ? long_help : help);

        String flags = parser.flagsHelp();
        if (!flags.isEmpty()) {
            sb.append("\n\n  Options:\n").append(flags);
        }
        return sb.toString();
    }

    protected String requiredArg(ArgParser parser, int index, String name) {
        if (parser.positionals().size() <= index) {
            throw new UsageException("Missing argument '" + name + "'");
        }
        return parser.positionals().get(index);
    }
}
