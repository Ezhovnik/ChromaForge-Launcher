package chromaforge.launcher.github;

import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonString;
import chromaforge.launcher.coders.json.JsonLong;

public class AssetInfo {
    public final String name;
    public final long size;
    public final String browserDownloadUrl;

    public AssetInfo(String name, long size, String browserDownloadUrl) {
        this.name = name;
        this.size = size;
        this.browserDownloadUrl = browserDownloadUrl;
    }

    public static AssetInfo fromJson(JsonObject object) {
        String name = ((JsonString) object.entries().get("name")).value();
        long size = ((JsonLong) object.entries().get("size")).value();
        String browserDownloadUrl = ((JsonString) object.entries().get("browser_download_url")).value();

        return new AssetInfo(name, size, browserDownloadUrl);
    }
}
