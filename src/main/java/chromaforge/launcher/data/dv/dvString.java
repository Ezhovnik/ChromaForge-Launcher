package chromaforge.launcher.data.dv;

public record dvString(String value) implements dvValue {
    @Override
    public String toString() {
        return this.value;
    }
}
