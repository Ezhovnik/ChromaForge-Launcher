package chromaforge.launcher.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record LauncherVersion(int major, int minor, int patch) {
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(?:[.-].*)?");

    public static final LauncherVersion UNKNOWN = new LauncherVersion(0, 0, 0);
    public static final LauncherVersion VERSION = load();

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    private static LauncherVersion load() {
        Package pkg = LauncherVersion.class.getPackage();
        String raw = pkg != null ? pkg.getImplementationVersion() : null;
        if (raw == null) {
            return UNKNOWN;
        }
        Matcher m = VERSION_PATTERN.matcher(raw.trim());
        if (!m.matches()) {
            return UNKNOWN;
        }
        return new LauncherVersion(
            Integer.parseInt(m.group(1)),
            Integer.parseInt(m.group(2)),
            Integer.parseInt(m.group(3))
        );
    }
}
