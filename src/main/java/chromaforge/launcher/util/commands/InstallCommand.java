package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.github.GitHubClient.GitHubClientException;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.CoreService;
import chromaforge.launcher.services.InstallService;
import chromaforge.launcher.services.ReleaseService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.CoreVersion;
import chromaforge.launcher.util.ExitCode;

public class InstallCommand extends Command {
    public InstallCommand() {
        this.keyword = "install";
        this.args = "<version>";
        this.help = "installs the specified engine version";
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String tagName = requiredArg(parser, 0, "version");
        CoreVersion version = CoreVersion.parse(tagName);

        if (CoreService.isInstalled(version, paths)) {
            ConsoleUtils.warn("Version '" + tagName + "' already installed. Use 'rm' first to reinstall");
            return ExitCode.USAGE;
        }

        List<ReleaseInfo> releases;
        try {
            releases = ReleaseService.fetchAll();
        } catch (GitHubClientException e) {
            ConsoleUtils.error("Failed to connect to GitHub: " + e.getMessage());
            return ExitCode.FAILURE;
        } catch (Exception e) {
            ConsoleUtils.error("Failed to fetch releases: " + e.getMessage());
            return ExitCode.FAILURE;
        }

        ReleaseInfo release = ReleaseService.findInstallable(releases, tagName);
        if (release == null) {
            ConsoleUtils.error("Version '" + tagName + "' not found. Run 'fetch' to see available versions");
            return ExitCode.USAGE;
        }

        System.out.println("Installing '" + tagName + "'...");
        try {
            InstallService.install(release, paths);
            if (CoreService.isInstalled(version, paths)) {
                ConsoleUtils.success("Successfully installed '" + tagName + "'");
            } else {
                throw new RuntimeException("Failed to find the version after download");
            }
        } catch (Exception e) {
            ConsoleUtils.error("Failed to install '" + tagName + "' : " + e.getMessage());
            return ExitCode.FAILURE;
        }
        return ExitCode.SUCCESS;
    }
}
