package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.github.BuildStatus;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.github.GitHubClient.GitHubClientException;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.ReleaseService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ExitCode;
import chromaforge.launcher.util.ConsoleUtils.ConsoleColor;
import chromaforge.launcher.util.ConsoleUtils.ConsoleSymbols;

public class FetchCommand extends Command {

    public FetchCommand() {
        this.keyword = "fetch";
        this.args = "";
        this.help = "display releases";
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
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

        System.out.println("Found releases " + ConsoleColor.DIM + "(" + releases.size() + ")" + ConsoleColor.RESET);
        System.out.printf("  " + ConsoleColor.DIM + "%-16s %-12s %s" + ConsoleColor.RESET + "%n", "Version", "Date", "Status");
        System.out.println("  " + ConsoleColor.DIM + "-".repeat(50) + ConsoleColor.RESET);
        for (ReleaseInfo release : releases) {
            BuildStatus status = BuildStatus.fromRelease(release);
            String date = release.publishedAt.substring(0, 10);
            String indicator;
            String statusColor;
            switch (status) {
                case INSTALLABLE:
                    indicator = ConsoleColor.GREEN + ConsoleSymbols.CHECK + ConsoleColor.RESET;
                    statusColor = ConsoleColor.GREEN;
                    break;
                case WRONG_OS:
                    indicator = ConsoleColor.YELLOW + ConsoleSymbols.CROSS + ConsoleColor.RESET;
                    statusColor = ConsoleColor.YELLOW;
                    break;
                case NO_BUILD:
                    indicator = ConsoleColor.RED + ConsoleSymbols.CROSS + ConsoleColor.RESET;
                    statusColor = ConsoleColor.RED;
                    break;
                default:
                    indicator = "?";
                    statusColor = ConsoleColor.RESET;
            }
            System.out.printf(
                "  %-16s %-12s %s %s%s%s%n",
                release.tagName.substring(1),
                date,
                indicator,
                statusColor,
                status.name(),
                ConsoleColor.RESET
            );
        }
        return ExitCode.SUCCESS;
    }
}
