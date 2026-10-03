package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.util.StringUtils;
import chromaforge.launcher.github.GitHubClient.GitHubClientException;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.interfaces.Progress;
import chromaforge.launcher.install.InstallListener;
import chromaforge.launcher.install.InstallSettings;
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
        this.args = "<version> [--skip-checksums] [--retry]";
        this.help = "installs the specified engine version";
    }

    @Override
    protected void registerArgs() {
        parser.flag("--skip-checksums", "-k", "don't verify the SHA-256 of the downloaded archive");
        parser.flag("--retry", "-r", "retry an interrupted download if possible");
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        parser.parse(args, 1);

        String tagName = requiredArg(parser, 0, "version");
        CoreVersion version = CoreVersion.parse(tagName);

        if (CoreService.isInstalled(version, paths)) {
            ConsoleUtils.warn("Version '" + tagName + "' already installed");
            ConsoleUtils.tip("  Use 'rm' first to reinstall");
            return ExitCode.USAGE;
        }

        List<ReleaseInfo> releases;
        Thread spinner = ConsoleUtils.startSpinner("Fetching releases");
        try {
            releases = ReleaseService.fetchAll();
        } catch (GitHubClientException e) {
            ConsoleUtils.stopSpinner(spinner);
            spinner = null;
            ConsoleUtils.error("Failed to connect to GitHub: " + e.getMessage());
            return ExitCode.FAILURE;
        } catch (Exception e) {
            ConsoleUtils.stopSpinner(spinner);
            spinner = null;
            ConsoleUtils.error("Failed to fetch releases: " + e.getMessage());
            return ExitCode.FAILURE;
        } finally {
            ConsoleUtils.stopSpinner(spinner);
        }

        ReleaseInfo release = ReleaseService.findInstallable(releases, tagName);
        if (release == null) {
            ConsoleUtils.error("Version '" + tagName + "' not found");
            ConsoleUtils.tip("  Run 'fetch' to see available versions");
            return ExitCode.USAGE;
        }

        AssetInfo asset = AssetInfo.fromRelease(release);
        if (asset == null) {
            ConsoleUtils.error("Failed to find asset for download");
            return ExitCode.FAILURE;
        }

        ConsoleUtils.tip("Release '" + tagName + "' found");
        ConsoleUtils.tip(String.format("  %-15s %s", "File:", asset.name));
        ConsoleUtils.tip(String.format("  %-15s %s", "Download size:", StringUtils.humanSize(asset.size)));
        ConsoleUtils.tip(String.format("  %-15s %s", "SHA-256:", asset.sha256));
        ConsoleUtils.tip(String.format("  %-15s %s", "Install:", paths.getCoreDir(version).toAbsolutePath()));

        try {
            InstallSettings settings = new InstallSettings(
                parser.has("--skip-checksums"),
                parser.has("--retry")
            );
            if (settings.skipChecksums()) {
                ConsoleUtils.warn("Checksum verification for integrity will not be calculated for downloaded files");
            }
            InstallService.install(asset, CoreVersion.parse(release.tagName.substring(1)), paths,
                new InstallListener() {
                    @Override
                    public void onStatus(String status) {
                        ConsoleUtils.stage(status + " '" + tagName + "'...");
                    }

                    @Override
                    public Progress progress() {
                        return ConsoleUtils.consoleProgress();
                    }
                },
                settings
            );
            if (CoreService.isInstalled(version, paths)) {
                ConsoleUtils.success("Successfully installed '" + tagName + "'");
            } else {
                ConsoleUtils.error("Failed to find the version after download");
                return ExitCode.FAILURE;
            }
        } catch (Exception e) {
            ConsoleUtils.error("Failed to install '" + tagName + "' : " + e.getMessage());
            return ExitCode.FAILURE;
        }
        return ExitCode.SUCCESS;
    }
}
