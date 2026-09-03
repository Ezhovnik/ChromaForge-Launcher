package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;

public abstract class Command {
    public String keyword;
    public String args;
    public String help;

    protected int index = 0;

    public abstract void execute(String[] args, LauncherPaths paths);

    protected String nextArg(String[] args) {
        if (index + 1 >= args.length) {
            throw new RuntimeException("Missing argument for " + keyword);
        }
        return args[++index];
    }
}
