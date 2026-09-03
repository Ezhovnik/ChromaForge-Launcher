package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstallService;
import chromaforge.launcher.services.ReleaseService;
import chromaforge.launcher.debug.Logger;

public class InstallCommand extends Command {
    private static Logger logger = Logger.getLogger("install-command");

    public InstallCommand() {
        this.keyword = "install";
        this.args = "<version>";
        this.help = "installs the specified engine version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName = nextArg(args);

        List<ReleaseInfo> releases = ReleaseService.fetchAll();
        ReleaseInfo release = ReleaseService.findInstallable(releases, tagName);
        if (release == null) {
            logger.error("Failed to find " + tagName);
            return;
        }

        InstallService.install(release, paths);
    }
}
