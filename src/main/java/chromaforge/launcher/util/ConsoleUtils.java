package chromaforge.launcher.util;

import java.io.PrintStream;

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
        public static final String WARN = "!";
        public static final String ARROW = "\u2192";
    }

    private static final PrintStream out = System.out;

    private static final boolean colorEnabled = detectColorSupport();

    private static boolean detectColorSupport() {
        if (System.getenv("NO_COLOR") != null) {
            return false;
        }
        return System.console() != null;
    }

    private static String colored(String code, String message) {
        if (!colorEnabled) {
            return message;
        }
        return code + message + ConsoleColor.RESET;
    }

    public static void success(String message) {
        out.println(colored(ConsoleColor.GREEN, ConsoleSymbols.CHECK + " " + message));
    }

    public static void error(String message) {
        out.println(colored(ConsoleColor.RED, ConsoleSymbols.CROSS + " " + message));
    }

    public static void warn(String message) {
        out.println(colored(ConsoleColor.YELLOW, ConsoleSymbols.WARN + " " + message));
    }

    public static void stage(String message) {
        out.println(colored(ConsoleColor.CYAN, ConsoleSymbols.ARROW + " " + message));
    }

    public static void tip(String message) {
        out.println(colored(ConsoleColor.DIM, message));
    }

    public static void clearLine() {
        out.print("\r\u001B[K");
    }

    static public Progress consoleProgress() {
        return new Progress() {
            private static final int BAR_LEN = 50;
            private static final long THROTTLE_NS = 100_000_000;
            private long lastPrintNs = 0;

            @Override
            public void onProgress(long done, long total) {
                if (total <= 0) {
                    return;
                }

                long now = System.nanoTime();
                boolean complete = done >= total;
                if (!complete && now - lastPrintNs < THROTTLE_NS) {
                    return;
                }
                lastPrintNs = now;

                int percent = (int) (done * 100 / total);
                int filled = (int) (done * BAR_LEN / total);

                StringBuilder bar = new StringBuilder("\r\u001B[K  [");
                for (int i = 0; i < BAR_LEN; i++) {
                    if (i < filled) {
                        bar.append(colored(ConsoleColor.GREEN, "="));
                    } else {
                        bar.append(colored(ConsoleColor.DIM, "-"));
                    }
                }
                bar.append("] ").append(colored(ConsoleColor.BOLD, percent + "%"));

                out.print(bar);

                if (complete) {
                    out.println();
                }
            }
        };
    }
}
