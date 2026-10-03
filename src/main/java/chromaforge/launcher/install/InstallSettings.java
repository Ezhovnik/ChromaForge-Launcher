package chromaforge.launcher.install;

public record InstallSettings(
    boolean skipChecksums, boolean canRetry
)  {}
