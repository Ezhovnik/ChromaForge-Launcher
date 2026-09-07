package chromaforge.launcher.data.dv;

public record dvDouble(double value) implements dvValue {
    @Override
    public String toString() {
        return Double.toString(value);
    }
}
