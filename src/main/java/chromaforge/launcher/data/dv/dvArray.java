package chromaforge.launcher.data.dv;

import java.util.List;

public record dvArray(List<dvValue> items) implements dvValue {}
