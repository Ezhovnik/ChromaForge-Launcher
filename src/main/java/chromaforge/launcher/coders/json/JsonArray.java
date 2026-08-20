package chromaforge.launcher.coders.json;

import java.util.List;

public record JsonArray(List<JsonValue> items) implements JsonValue {}
