package chromaforge.launcher.debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import chromaforge.launcher.util.ConsoleUtils;

public class Logger {
    private static final Map<String, Logger> instances = new ConcurrentHashMap<>();
    private final java.util.logging.Logger julLogger;
    private final String name;

    private Logger(String name) {
        this.name = name;
        this.julLogger = java.util.logging.Logger.getLogger("chromaforge.launcher." + name);
    }

    public static void init(String logFilePath) {
        try {
            Path parent = Paths.get(logFilePath).getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            System.err.println("Failed to create log directory: " + e.getMessage());
            return;
        }

        java.util.logging.Logger parentLogger = java.util.logging.Logger.getLogger("chromaforge.launcher");
        parentLogger.setUseParentHandlers(false);
        parentLogger.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new ColoredFormatter());
        consoleHandler.setLevel(Level.ALL);
        parentLogger.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler(logFilePath, false);
            fileHandler.setFormatter(new PlainFormatter());
            fileHandler.setLevel(Level.ALL);
            parentLogger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Failed to create file handler: " + e.getMessage());
        }
    }

    public static Logger getLogger(String name) {
        return instances.computeIfAbsent(name, Logger::new);
    }

    private void log(LogLevel level, String msg) {
        if (level == LogLevel.PRINT) {
            System.out.println("[" + name + "]    " + msg);
            return;
        }
        julLogger.log(level.julLevel, msg);
    }

    private void log(LogLevel level, Supplier<String> msgSupplier) {
        if (level == LogLevel.PRINT) {
            System.out.println("[" + name + "]    " + msgSupplier.get());
            return;
        }
        julLogger.log(level.julLevel, msgSupplier);
    }

    public void trace(String msg) {
        log(LogLevel.TRACE, msg);
    }

    public void trace(Supplier<String> sup) {
        log(LogLevel.TRACE, sup);
    }

    public void debug(String msg) {
        log(LogLevel.DEBUG, msg);
    }

    public void debug(Supplier<String> sup) {
        log(LogLevel.DEBUG, sup);
    }

    public void info(String msg) {
        log(LogLevel.INFO, msg);
    }

    public void info(Supplier<String> sup) {
        log(LogLevel.INFO, sup);
    }

    public void warning(String msg) {
        log(LogLevel.WARNING, msg);
    }

    public void warning(Supplier<String> sup) {
        log(LogLevel.WARNING, sup);
    }

    public void error(String msg) {
        log(LogLevel.ERROR, msg);
    }

    public void error(Supplier<String> sup) {
        log(LogLevel.ERROR, sup);
    }

    public void critical(String msg) {
        log(LogLevel.CRITICAL, msg);
    }

    public void critical(Supplier<String> sup) {
        log(LogLevel.CRITICAL, sup);
    }

    public void print(String msg) {
        log(LogLevel.PRINT, msg);
    }

    public void print(Supplier<String> sup) {
        log(LogLevel.PRINT, sup);
    }

    public void setLevel(LogLevel level) {
        julLogger.setLevel(level.julLevel);
    }

    private static class ColoredFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String color = getColor(record.getLevel());
            String prefix = getLevelPrefix(record.getLevel());
            String coloredPrefix = color + prefix + ConsoleUtils.ConsoleColor.RESET;
            String moduleName = extractModuleName(record.getLoggerName());
            String formattedModule = String.format("%20s", moduleName);
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"));
            return coloredPrefix + " " + time + " [" + formattedModule + "] " + record.getMessage() + "\n";
        }

        private String getColor(Level level) {
            if (level == Level.FINER) {
                return ConsoleUtils.ConsoleColor.RESET;
            }
            if (level == Level.FINE) {
                return ConsoleUtils.ConsoleColor.CYAN;
            }
            if (level == Level.INFO) {
                return ConsoleUtils.ConsoleColor.GREEN;
            }
            if (level == Level.WARNING) {
                return ConsoleUtils.ConsoleColor.YELLOW;
            }
            if (level == Level.SEVERE) {
                return ConsoleUtils.ConsoleColor.RED + ConsoleUtils.ConsoleColor.BOLD;
            }
            return ConsoleUtils.ConsoleColor.RESET;
        }

        private String getLevelPrefix(Level level) {
            if (level == Level.FINER) {
                return "[T]";
            }
            if (level == Level.FINE) {
                return "[D]";
            }
            if (level == Level.INFO) {
                return "[I]";
            }
            if (level == Level.WARNING) {
                return "[W]";
            }
            if (level == Level.SEVERE) {
                return "[E]";
            }
            return "[?]";
        }
    }

    private static class PlainFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String prefix = getLevelPrefix(record.getLevel());
            String moduleName = extractModuleName(record.getLoggerName());
            String formattedModule = String.format("%20s", moduleName);
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"));
            return prefix + " " + time + " [" + formattedModule + "] " + record.getMessage() + "\n";
        }

        private String getLevelPrefix(Level level) {
            if (level == Level.FINER) {
                return "[T]";
            }
            if (level == Level.FINE) {
                return "[D]";
            }
            if (level == Level.INFO) {
                return "[I]";
            }
            if (level == Level.WARNING) {
                return "[W]";
            }
            if (level == Level.SEVERE) {
                return "[E]";
            }
            return "[?]";
        }
    }

    private static String extractModuleName(String fullLoggerName) {
        if (fullLoggerName != null && fullLoggerName.startsWith("chromaforge.launcher.")) {
            return fullLoggerName.substring("chromaforge.launcher.".length());
        }
        return fullLoggerName != null ? fullLoggerName : "unknown";
    }
}

enum LogLevel {
    PRINT(Level.INFO),
    TRACE(Level.FINER),
    DEBUG(Level.FINE),
    INFO(Level.INFO),
    WARNING(Level.WARNING),
    ERROR(Level.SEVERE),
    CRITICAL(Level.SEVERE);

    final Level julLevel;
    LogLevel(Level julLevel) {
        this.julLevel = julLevel;
    }
}
