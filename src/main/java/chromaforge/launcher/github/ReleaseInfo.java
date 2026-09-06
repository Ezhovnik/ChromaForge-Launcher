package chromaforge.launcher.github;

import java.util.ArrayList;
import java.util.List;

import chromaforge.launcher.data.dv.dvArray;
import chromaforge.launcher.data.dv.dvBool;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.data.dv.dvValue;

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

    public static ReleaseInfo fromJson(dvObject object) {
        String tagName = ((dvString) object.entries().get("tag_name")).value();
        String name = ((dvString) object.entries().get("name")).value();
        boolean isPreRelease = ((dvBool) object.entries().get("prerelease")).value();
        String publishedAt = ((dvString) object.entries().get("published_at")).value();
        dvArray assets = (dvArray) object.entries().get("assets");
        List<AssetInfo> list = new ArrayList<>();
        for (dvValue item : assets.items()) {
            if (item instanceof dvObject) {
                list.add(AssetInfo.fromJson((dvObject)item));
            }
        }

        return new ReleaseInfo(tagName, name, isPreRelease, publishedAt, list);
    }
}
