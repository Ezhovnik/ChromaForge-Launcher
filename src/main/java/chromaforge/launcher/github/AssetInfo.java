package chromaforge.launcher.github;

import chromaforge.launcher.coders.json.JsonObject;
import chromaforge.launcher.coders.json.JsonString;
import chromaforge.launcher.util.Platform;
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

    public static AssetInfo fromRelease(ReleaseInfo release) {
        Platform.OS platform = Platform.detectOS();

        String version = release.tagName.startsWith("v") ? release.tagName.substring(1) : release.tagName;
        String suffix;
        switch (platform) {
            case Platform.OS.WINDOWS:
                suffix = "win64.zip";
                break;
            case Platform.OS.LINUX:
                suffix = "x86-64.AppImage";
                break;
            case Platform.OS.MACOS:
                suffix = "macos.dmg";
                break;
            default:
                return null;
        }

        String expected = "chromaforge-" + version + "_" + suffix;
        for (AssetInfo asset : release.assets) {
            if (asset.name.equals(expected)) {
                return asset;
            }
        }
        return null;
    }
}
