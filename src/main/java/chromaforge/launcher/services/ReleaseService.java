package chromaforge.launcher.services;

import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.coders.json.JsonArray;
import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonParser;
import chromaforge.launcher.coders.json.JsonValue;
import chromaforge.launcher.github.BuildStatus;
import chromaforge.launcher.github.GitHubClient;
import chromaforge.launcher.github.GitHubClient.GitHubClientException;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.debug.Logger;

public class ReleaseService {
    static private Logger logger = Logger.getLogger("release-service");

    static public List<ReleaseInfo> fetchAll() {
        String json;
        try {
            json = new GitHubClient().fetchReleases();
        } catch (GitHubClientException e) {
            logger.error("An error occurred while working with the GitHub API: " + e.getMessage());
            return null;
        }
        JsonValue root = JsonParser.parse(json);

        List<ReleaseInfo> releases = new ArrayList<>();
        if (root instanceof JsonArray) {
            JsonArray array = (JsonArray)root;
            for (JsonValue item : array.items()) {
                if (item instanceof JsonObject) {
                    ReleaseInfo r = ReleaseInfo.fromJson((JsonObject) item);
                    releases.add(r);
                } else {
                    logger.warning("Skipping non-object element: " + item);
                }
            }
        } else {
            logger.error("Expected JSON array, got " + root.getClass());
            return null;
        }
        return releases;
    }

    static public ReleaseInfo findInstallable(List<ReleaseInfo> releases, String version) {
        for (ReleaseInfo release : releases) {
            if (BuildStatus.fromRelease(release) == BuildStatus.INSTALLABLE && release.tagName.equals("v" + version)) {
                return release;
            }
        }
        return null;
    }

    static public ReleaseInfo findInstallable(List<ReleaseInfo> releases) {
        for (ReleaseInfo release : releases) {
            if (BuildStatus.fromRelease(release) == BuildStatus.INSTALLABLE) {
                return release;
            }
        }
        return null;
    }
}
