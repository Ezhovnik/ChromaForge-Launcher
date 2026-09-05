package chromaforge.launcher.services;

import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.coders.json.JsonArray;
import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonParser;
import chromaforge.launcher.coders.json.JsonValue;
import chromaforge.launcher.github.BuildStatus;
import chromaforge.launcher.github.GitHubClient;
import chromaforge.launcher.github.ReleaseInfo;

public class ReleaseService {

    static public List<ReleaseInfo> fetchAll() {
        String json;
        json = new GitHubClient().fetchReleases();
        JsonValue root = JsonParser.parse(json);

        List<ReleaseInfo> releases = new ArrayList<>();
        if (root instanceof JsonArray) {
            JsonArray array = (JsonArray)root;
            for (JsonValue item : array.items()) {
                if (item instanceof JsonObject) {
                    ReleaseInfo r = ReleaseInfo.fromJson((JsonObject) item);
                    releases.add(r);
                }
            }
        } else {
            throw new RuntimeException("Unexpected response from server");
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
