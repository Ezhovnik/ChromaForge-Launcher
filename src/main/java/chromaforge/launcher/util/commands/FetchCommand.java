package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.github.BuildStatus;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.ReleaseService;
import chromaforge.launcher.util.ConsoleColor;
import chromaforge.launcher.debug.Logger;

public class FetchCommand extends Command {
    private static Logger logger = Logger.getLogger("fetch-command");

    public FetchCommand() {
        this.keyword = "fetch";
        this.args = "";
        this.help = "display available releases";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        logger.info("Fetching releases...");

        List<ReleaseInfo> releases = ReleaseService.fetchAll();
        logger.info("Found " + releases.size() + " release(s)");

        System.out.println("Available releases:");
        System.out.printf("  %-16s %-12s %s%n", "Version", "Date", "Status");
        System.out.println("  " + "-".repeat(50));
        for (ReleaseInfo release : releases) {
            BuildStatus status = BuildStatus.fromRelease(release);
            String date = release.publishedAt.substring(0, 10);
            String color;
            switch (status) {
                case INSTALLABLE:
                    color = ConsoleColor.GREEN;
                    break;
                case WRONG_OS:
                    color = ConsoleColor.YELLOW;
                    break;
                case NO_BUILD:
                    color = ConsoleColor.RED;
                    break;
                default:
                    color = ConsoleColor.RESET;
            }
            String indicator = status == BuildStatus.INSTALLABLE ? "[+]" : "[-]";
            String statusText = color + status.name() + ConsoleColor.RESET;
            System.out.printf(
                "  %-16s %-12s %s %s%n",
                release.tagName.substring(1),
                date,
                indicator,
                statusText
            );
        }
    }
}
