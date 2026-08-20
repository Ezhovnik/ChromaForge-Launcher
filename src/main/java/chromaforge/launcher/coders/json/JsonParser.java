package chromaforge.launcher.coders.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import chromaforge.launcher.coders.BasicParser;

public final class JsonParser extends BasicParser {
    public JsonParser(String source) {
        super(source);
    }

    private JsonValue parseList() {
        expect('[');
        JsonArray list = new JsonArray(new ArrayList<>());
        while(peek() != ']') {
            if (peek() == '#') {
                skipLine();
                continue;
            }
            list.items().add(parseValue());

            char next = peek();
            if (next == ',') {
                pos++;
            } else if (next == ']') {
                break;
            } else {
                throw new ParsingException("',' expected");
            }
        }
        pos++;
        return list;
    }

    private JsonValue parseValue() {
        char next = peek();
        if (next == '-' || next == '+' || isDigit(next)) {
            double numeric = parseNumber();
            if (numeric == Math.floor(numeric) && !Double.isInfinite(numeric) && Math.abs(numeric) <= Long.MAX_VALUE) {
                return new JsonLong((long) numeric);
            }
            return new JsonDouble(numeric);
        }
        if (isIdentifierStart(next)) {
            String literal = parseName();
            switch (literal) {
                case "true":
                    return new JsonBool(true);
                case "false":
                    return new JsonBool(false);
                case "inf":
                    return new JsonDouble(Double.POSITIVE_INFINITY);
                case "nan":
                    return new JsonDouble(Double.NaN);
                case "null":
                    return JsonNull.INSTANCE;
            }
            throw new ParsingException("Invalid keyword: " + literal);
        }
        if (next == '{') {
            return parseObject();
        }
        if (next == '[') {
            return parseList();
        }
        if (next == '"' || next == '\'') {
            pos++;
            return new JsonString(parseString(next, true));
        }
        throw new ParsingException("Unexpected character '" + next + "'");
    }

    private JsonValue parseObject() {
        expect('{');
        JsonObject object = new JsonObject(new LinkedHashMap<>());
        while (peek() != '}') {
            if (peek() == '#') {
                skipLine();
                continue;
            }
            expect('"');
            String key = parseString('"', true);
            char next = peek();
            if (next != ':') {
                throw new ParsingException("':' expected");
            }
            pos++;
            object.entries().put(key, parseValue());
            next = peek();
            if (next == ',') {
                pos++;
            } else if (next == '}') {
                break;
            } else {
                throw new ParsingException("',' expected");
            }
        }
        pos++;
        return object;
    }

    private JsonValue parse() {
        char next = peek();
        if (next == '{') {
            return parseObject();
        } else if (next == '[') {
            return parseList();
        }
        throw new ParsingException("'{' or '[' expected");
    }

    public static JsonValue parse(String source) {
        JsonParser parser = new JsonParser(source);
        return parser.parse();
    }
}
