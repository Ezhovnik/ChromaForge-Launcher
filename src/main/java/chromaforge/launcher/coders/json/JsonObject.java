package chromaforge.launcher.coders.json;

import java.util.LinkedHashMap;

public record JsonObject(LinkedHashMap<String, JsonValue> entries) implements JsonValue {}
