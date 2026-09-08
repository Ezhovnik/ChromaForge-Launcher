package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.github.GitHubClient.GitHubClientException;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstallService;
import chromaforge.launcher.services.ReleaseService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.CoreVersion;

public class InstallCommand extends Command {
    public InstallCommand() {
        this.keyword = "install";
        this.args = "<version>";
        this.help = "installs the specified engine version";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String tagName = nextArg(args);
        CoreVersion version = CoreVersion.parse(tagName);
        if (FileUtils.exists(paths.getCoreDir(version))) {
            ConsoleUtils.warn("Version '" + tagName + "' already installed. Use 'rm' first to reinstall");
            return;
        }

        List<ReleaseInfo> releases;
        try {
            releases = ReleaseService.fetchAll();
        } catch (GitHubClientException e) {
            ConsoleUtils.error("Failed to connect to GitHub: " + e.getMessage());
            return;
        } catch (Exception e) {
            ConsoleUtils.error("Failed to fetch releases: " + e.getMessage());
            return;
        }

        ReleaseInfo release = ReleaseService.findInstallable(releases, tagName);
        if (release == null) {
            ConsoleUtils.error("Version '" + tagName + "' not found. Run 'fetch' to see available versions");
            return;
        }

        System.out.println("Installing '" + tagName + "'...");
        try {
            InstallService.install(release, paths);
            ConsoleUtils.success("Successfully installed '" + tagName + "'");
        } catch (Exception e) {
            ConsoleUtils.error("Failed to install '" + tagName + "' : " + e.getMessage());
        }
    }
}
