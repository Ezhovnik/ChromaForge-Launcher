package chromaforge.launcher.util;

public class UsageException extends RuntimeException {
    public UsageException(String message) {
        super(message);
    }

    public UsageException(String message, Throwable cause) {
        super(message, cause);
    }
}
