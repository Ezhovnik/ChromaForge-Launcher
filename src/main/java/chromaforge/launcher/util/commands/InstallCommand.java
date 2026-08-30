package chromaforge.launcher.util.commands;

import java.nio.file.Path;

import chromaforge.launcher.coders.json.JsonArray;
import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonParser;
import chromaforge.launcher.coders.json.JsonValue;
import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.github.BuildStatus;
import chromaforge.launcher.github.GitHubClient;
import chromaforge.launcher.github.ReleaseInfo;
import chromaforge.launcher.install.Installers;
import chromaforge.launcher.util.Platform;

public class InstallCommand extends Command {
    public InstallCommand() {
        this.keyword = "install";
        this.args = "<version>";
        this.help = "installs the specified engine version";
    }

    @Override
    public void execute(String[] args) {
        String tagName = nextArg(args);

        Platform.OS os = Platform.detectOS();

        String json = new GitHubClient().fetchReleases();
        JsonValue root = JsonParser.parse(json);

        ReleaseInfo release = null;
        for (JsonValue item : ((JsonArray)root).items()) {
            ReleaseInfo r = ReleaseInfo.fromJson((JsonObject)item);
            if (BuildStatus.fromRelease(r) == BuildStatus.INSTALLABLE && r.tagName.equals("v" + tagName)) {
                release = r;
                break;
            }
        }
        if (release == null) {
            throw new RuntimeException("No installable release found");
        }

        AssetInfo asset = AssetInfo.fromRelease(release);
        if (asset == null) {
            throw new RuntimeException("No asset found for your platform in " + release.tagName);
        }

        Path installDir = Path.of("cores/chromaforge-" + release.tagName);
        System.out.println("Installing " + release.tagName + " ...");
        Installers.of(os).install(asset, installDir);
    }
}
