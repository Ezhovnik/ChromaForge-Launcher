package chromaforge.launcher.github;

import chromaforge.launcher.util.Platform;

public enum BuildStatus {
    INSTALLABLE,
    WRONG_OS,
    NO_BUILD;

    public static BuildStatus fromRelease(ReleaseInfo release) {
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
                return WRONG_OS;
        }

        String expected = "chromaforge-" + version + "_" + suffix;
        for (AssetInfo asset : release.assets) {
            if (asset.name.equals(expected)) {
                return INSTALLABLE;
            }
        }
        return NO_BUILD;
    }
}
