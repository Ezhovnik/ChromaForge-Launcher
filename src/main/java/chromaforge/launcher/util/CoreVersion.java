package chromaforge.launcher.util;

public record CoreVersion(int major, int minor, int patch) implements Comparable<CoreVersion> {
    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public static CoreVersion parse(String versionString) {
        String[] parts = versionString.split("\\.");
        if (parts.length != 3) {
            throw new UsageException("Invalid version format: " + versionString);
        }
        try {
            return new CoreVersion(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
            );
        } catch (NumberFormatException e) {
            throw new UsageException("Invalid version format: " + versionString);
        }
    }

    @Override
    public int compareTo(CoreVersion other) {
        int cmp = Integer.compare(this.major, other.major);
        if (cmp != 0) return cmp;
        cmp = Integer.compare(this.minor, other.minor);
        if (cmp != 0) return cmp;
        return Integer.compare(this.patch, other.patch);
    }
}
