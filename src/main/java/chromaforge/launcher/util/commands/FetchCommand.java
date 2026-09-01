package chromaforge.launcher.util.commands;

import chromaforge.launcher.coders.json.JsonArray;
import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonParser;
import chromaforge.launcher.coders.json.JsonValue;
import chromaforge.launcher.github.BuildStatus;
import chromaforge.launcher.github.GitHubClient;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.github.GitHubClient.GitHubClientException;
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
    public void execute(String[] args) {
        logger.info("Fetching releases...");
        String json;
        try {
            json = new GitHubClient().fetchReleases();
        } catch (GitHubClientException e) {
            logger.error("An error occurred while working with the GitHub API: " + e.getMessage());
            return;
        }
        JsonValue root = JsonParser.parse(json);
        logger.info("Found " + ((JsonArray)root).items().size() + " release(s)");

        System.out.println("Available releases:");
        System.out.printf("  %-16s %-12s %s%n", "Version", "Date", "Status");
        System.out.println("  " + "-".repeat(50));
        for (JsonValue item : ((JsonArray)root).items()) {
            ReleaseInfo r = ReleaseInfo.fromJson((JsonObject)item);
            BuildStatus status = BuildStatus.fromRelease(r);
            String date = r.publishedAt.substring(0, 10);
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
            System.out.printf("  %-16s %-12s %s %s%n", r.tagName.substring(1), date, indicator, statusText);
        }
    }
}
