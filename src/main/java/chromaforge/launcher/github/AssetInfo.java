package chromaforge.launcher.github;

import chromaforge.launcher.data.dv.dvLong;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.util.Platform;

public class AssetInfo {
    public final String name;
    public final long size;
    public final String browserDownloadUrl;

    public AssetInfo(String name, long size, String browserDownloadUrl) {
        this.name = name;
        this.size = size;
        this.browserDownloadUrl = browserDownloadUrl;
    }

    public static AssetInfo fromJson(dvObject object) {
        String name = ((dvString) object.entries().get("name")).value();
        long size = ((dvLong) object.entries().get("size")).value();
        String browserDownloadUrl = ((dvString) object.entries().get("browser_download_url")).value();

        return new AssetInfo(name, size, browserDownloadUrl);
    }

    public static AssetInfo fromRelease(ReleaseInfo release) {
        Platform.OS platform = Platform.detectOS();

        String version = release.tagName.startsWith("v") ? release.tagName.substring(1) : release.tagName;
        String suffix;
        switch (platform) {
            case WINDOWS:
                suffix = "win64.zip";
                break;
            case LINUX:
                suffix = "x86-64.AppImage";
                break;
            case MACOS:
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
