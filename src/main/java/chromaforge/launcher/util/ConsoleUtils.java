package chromaforge.launcher.util;

import chromaforge.launcher.interfaces.Progress;

public class ConsoleUtils {
    public static class ConsoleColor {
        public static final String RESET = "\u001B[0m";
        public static final String BOLD = "\u001B[1m";
        public static final String DIM = "\u001B[2m";
        public static final String UNDERLINE = "\u001B[4m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String MAGENTA = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
    }

    public static class ConsoleSymbols {
        public static final String CHECK = "\u2713";
        public static final String CROSS = "\u2717";
    }

    public static void success(String message) {
        System.out.println(ConsoleColor.GREEN + ConsoleSymbols.CHECK + " " + message + ConsoleColor.RESET);
    }

    public static void error(String message) {
        System.err.println(ConsoleColor.RED + ConsoleSymbols.CROSS + " " + message + ConsoleColor.RESET);
    }

    public static void warn(String message) {
        System.err.println(ConsoleColor.YELLOW + ConsoleSymbols.CROSS + " " + message + ConsoleColor.RESET);
    }

    public static void clearLine() {
        System.out.print("\r\u001B[K");
    }

    static public Progress consoleProgress() {
        return (done, total) -> {
            int percent = (int) (done * 100.0 / total);
            int barLength = 50;
            int filled = (int) (done * barLength / total);

            StringBuilder bar = new StringBuilder("\r  [");
            for (int i = 0; i < barLength; i++) {
                if (i < filled) {
                    bar.append(ConsoleColor.GREEN).append("=").append(ConsoleColor.RESET);
                } else {
                    bar.append(ConsoleColor.DIM).append("-").append(ConsoleColor.RESET);
                }
            }
            bar.append("] ").append(ConsoleColor.BOLD).append(percent).append("%").append(ConsoleColor.RESET);

            System.out.print(bar);

            if (done == total) {
                System.out.println();
            }
        };
    }
}
