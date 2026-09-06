package chromaforge.launcher.data.dv;

import java.util.LinkedHashMap;

public record dvObject(LinkedHashMap<String, dvValue> entries) implements dvValue {}
