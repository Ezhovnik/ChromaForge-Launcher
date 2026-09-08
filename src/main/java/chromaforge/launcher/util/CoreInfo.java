package chromaforge.launcher.util;

import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.data.dv.dvValue;

public record CoreInfo(CoreVersion version, String installedAt) {
    static public CoreInfo fromObject(String version, dvObject obj) {
        String installedAt = "";
        dvValue v = obj.entries().get("installed_at");
        if (v instanceof dvString s) {
            installedAt = s.value();
        }
        return new CoreInfo(CoreVersion.parse(version), installedAt);
    }
}
