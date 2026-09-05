package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstallService;
import chromaforge.launcher.services.ReleaseService;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ConsoleUtils.ConsoleSymbols;

public class InstallCommand extends Command {
    public InstallCommand() {
        this.keyword = "install";
        this.args = "<version>";
        this.help = "installs the specified engine version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName = nextArg(args);

        List<ReleaseInfo> releases = ReleaseService.fetchAll();
        if (releases == null) {
            System.err.println(ConsoleColor.RED + ConsoleSymbols.CROSS + " Failed to connect to GitHub" + ConsoleColor.RESET);
            return;
        }
        ReleaseInfo release = ReleaseService.findInstallable(releases, tagName);
        if (release == null) {
            System.err.println(ConsoleColor.RED + ConsoleSymbols.CROSS + " Version " + tagName + " not found. Run 'fetch' to see available versions" + ConsoleColor.RESET);
            return;
        }

        InstallService.install(release, paths);
    }
}
