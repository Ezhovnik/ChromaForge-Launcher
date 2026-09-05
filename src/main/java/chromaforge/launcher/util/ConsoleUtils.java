package chromaforge.launcher.util;

import chromaforge.launcher.interfaces.Progress;

public class ConsoleUtils {
    public static class ConsoleColor {
        public static final String RESET = "\u001B[0m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String RED = "\u001B[31m";
        public static final String CYAN = "\u001B[36m";
        public static final String BOLD = "\u001B[1m";
    };

    static public Progress consoleProgress() {
        return (done, total) -> {
            int percent = (int) (done * 100.0 / total);
            int barLength = 50;
            int filled = (int) (done * barLength / total);

            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < barLength; i++) {
                bar.append(i < filled ? "=" : "-");
            }
            bar.append("] ").append(percent).append("%");

            System.out.print("\r" + bar);
            System.out.flush();

            if (done == total) {
                System.out.println();
            }
        };
    }
}
