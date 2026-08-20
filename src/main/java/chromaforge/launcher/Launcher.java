package chromaforge.launcher;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.github.BuildStatus;
import chromaforge.launcher.github.GitHubClient;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.install.Installers;
import chromaforge.launcher.run.Runners;
import chromaforge.launcher.util.Platform;

import java.nio.file.Path;

import chromaforge.launcher.coders.json.JsonArray;
import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonParser;
import chromaforge.launcher.coders.json.JsonValue;

public class Launcher {
    public static void main(String[] args) {
        Platform.OS os = Platform.detectOS();

        String json = new GitHubClient().fetchReleases();
        JsonValue root = JsonParser.parse(json);

        ReleaseInfo release = null;
        for (JsonValue item : ((JsonArray)root).items()) {
            ReleaseInfo r = ReleaseInfo.fromJson((JsonObject)item);
            if (BuildStatus.fromRelease(r) == BuildStatus.INSTALLABLE) {
                release = r;
                break;
            }
        }
        if (release == null) {
            throw new RuntimeException("No installable release found");
        }

        AssetInfo asset = AssetInfo.fromRelease(release);

        Path installDir = Path.of("chromaforge");
        System.out.println("Installing " + release.tagName + " ...");
        Installers.of(os).install(asset, installDir);

        System.out.println("Launching " + release.tagName + " ...");
        Runners.of(os).run(installDir);
    }
}
