package chromaforge.launcher.util;

public class Platform {
    public enum OS {
        WINDOWS,
        LINUX,
        MACOS,
        UNKNOWN
    }

    public static OS detectOS() {
        String name = System.getProperty("os.name", "").toLowerCase();
        if (name.contains("win")) {
            return OS.WINDOWS;
        }
        if (name.contains("mac")) {
            return OS.MACOS;
        }
        if (name.contains("nix") || name.contains("nux") || name.contains("aix")) {
            return OS.LINUX;
        }
        return OS.UNKNOWN;
    }

    public static void configureEncoding() {
        if (detectOS() == OS.WINDOWS) {
            try {
                new ProcessBuilder("cmd", "/c", "chcp 65001 > nul").inheritIO().start().waitFor();
            } catch (Exception ignored) {}
        }
    }
}
