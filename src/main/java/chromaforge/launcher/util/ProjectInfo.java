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
        + "      launcher fetch               List available versions from GitHub\n"
        + "      launcher install 0.4.1       Install engine v0.4.1\n"
        + "      launcher ls                  List installed versions\n"
        + "      launcher new my_world 0.4.1  Create a new instance\n"
        + "      launcher launch my_world     Launch an instance\n"
        + "      launcher instances           List all instances\n"
        + "      launcher rmi my_world        Remove an instance\n"
        + "      launcher rm 0.4.1            Remove installed version\n"
        + "      launcher check 0.4.1         Verify integrity of engine files\n"
        + "      launcher where               Show data directory";

    private ProjectInfo() {}
}
