package chromaforge.launcher.util;

public record LauncherVersion(int major, int minor, int patch) {
    public static final LauncherVersion VERSION = load();

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    private static LauncherVersion load() {
        String v = LauncherVersion.class.getPackage().getImplementationVersion();
        String[] parts = v.split("\\.");
        return new LauncherVersion(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2])
        );

    }
}
