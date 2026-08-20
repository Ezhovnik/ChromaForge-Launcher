package chromaforge.launcher.github;

import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.coders.json.JsonArray;
import chromaforge.launcher.coders.json.JsonBool;
import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonString;
import chromaforge.launcher.coders.json.JsonValue;

public class ReleaseInfo {
    public final String tagName;
    public final String name;
    public final boolean isPreRelease;
    public final String publishedAt;
    public final List<AssetInfo> assets;

    public ReleaseInfo(String tagName, String name, boolean isPreRelease, String publishedAt, List<AssetInfo> assets) {
        this.tagName = tagName;
        this.name = name;
        this.isPreRelease = isPreRelease;
        this.publishedAt = publishedAt;
        this.assets = assets;
    }

    public static ReleaseInfo fromJson(JsonObject object) {
        String tagName = ((JsonString) object.entries().get("tag_name")).value();
        String name = ((JsonString) object.entries().get("name")).value();
        boolean isPreRelease = ((JsonBool) object.entries().get("prerelease")).value();
        String publishedAt = ((JsonString) object.entries().get("published_at")).value();
        JsonArray assets = (JsonArray) object.entries().get("assets");
        List<AssetInfo> list = new ArrayList<>();
        for (JsonValue item : assets.items()) {
            if (item instanceof JsonObject) {
                list.add(AssetInfo.fromJson((JsonObject)item));
            }
        }

        return new ReleaseInfo(tagName, name, isPreRelease, publishedAt, list);
    }
}
