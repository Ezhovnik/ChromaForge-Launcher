package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.util.ProjectInfo;

public class VersionCommand extends Command {
    public VersionCommand() {
        this.keyword = "version";
        this.args = "";
        this.help = "display the launcher version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        System.out.println(ProjectInfo.NAME + " v" + ProjectInfo.VERSION);
    }
}
