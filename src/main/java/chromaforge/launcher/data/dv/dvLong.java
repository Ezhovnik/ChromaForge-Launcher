package chromaforge.launcher.data.dv;

public record dvLong(long value) implements dvValue {
    @Override
    public String toString() {
        return Long.toString(value);
    }
}
