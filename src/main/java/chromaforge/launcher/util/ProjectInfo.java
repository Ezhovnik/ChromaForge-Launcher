package chromaforge.launcher.util;

public final class ProjectInfo {
    public static final String NAME = "ChromaForge Launcher";
    public static final String VERSION = LauncherVersion.VERSION.toString();
    public static final String AUTHOR = "Ezhovnik";
    public static final String ABOUT = "CLI launcher for ChromaForge voxel engine";
    public static final String REPOSITORY = "https://github.com/Ezhovnik/ChromaForge-Launcher";
    public static final String ENGINE_REPOSITORY = "https://github.com/Ezhovnik/ChromaForge-v2";

    public static final String EXAMPLES = "\n"
        + "    Examples:\n"
        + "      launcher.bat fetch                List available versions from GitHub\n"
        + "      launcher.bat install 0.4.1        Install engine v0.4.1\n"
        + "      launcher.bat ls                   List installed versions\n"
        + "      launcher.bat launch 0.4.1         Launch engine v0.4.1\n"
        + "      launcher.bat launch --check 0.4.1 Verify integrity and launch\n"
        + "      launcher.bat check 0.4.1          Verify integrity of engine files\n"
        + "      launcher.bat rm 0.4.1             Remove installed version";

    private ProjectInfo() {}
}
