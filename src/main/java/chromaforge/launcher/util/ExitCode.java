package chromaforge.launcher.util;

public enum ExitCode {
    SUCCESS(0),
    USAGE(1),
    FAILURE(2);
    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
