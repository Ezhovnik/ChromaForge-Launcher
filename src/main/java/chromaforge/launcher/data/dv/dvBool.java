package chromaforge.launcher.data.dv;

public record dvBool(boolean value) implements dvValue {
    @Override
    public String toString() {
        if (this.value) {
            return "true";
        }
        return "false";
    }
}
