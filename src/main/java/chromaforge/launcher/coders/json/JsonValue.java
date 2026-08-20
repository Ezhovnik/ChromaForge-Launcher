package chromaforge.launcher.coders.json;

public sealed interface JsonValue
    permits JsonNull, JsonBool, JsonLong, JsonDouble, JsonString, JsonArray, JsonObject  {}
