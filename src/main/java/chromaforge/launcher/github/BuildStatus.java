package chromaforge.launcher.github;

public enum BuildStatus {
    INSTALLABLE,
    WRONG_OS,
    NO_BUILD;

    public static BuildStatus fromRelease(ReleaseInfo release) {
        AssetInfo matching = AssetInfo.fromRelease(release);
        return matching != null ? INSTALLABLE : (release.assets.isEmpty() ? NO_BUILD : WRONG_OS);
    }
}
