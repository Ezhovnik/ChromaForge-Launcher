package chromaforge.launcher.coders.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import chromaforge.launcher.coders.BasicParser;
import chromaforge.launcher.data.dv.dvArray;
import chromaforge.launcher.data.dv.dvBool;
import chromaforge.launcher.data.dv.dvDouble;
import chromaforge.launcher.data.dv.dvLong;
import chromaforge.launcher.data.dv.dvNull;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.data.dv.dvValue;

public final class JsonParser extends BasicParser {
    public JsonParser(String source) {
        super(source);
    }

    private dvValue parseList() {
        expect('[');
        dvArray list = new dvArray(new ArrayList<>());
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

    private dvValue parseValue() {
        char next = peek();
        if (next == '-' || next == '+' || isDigit(next)) {
            double numeric = parseNumber();
            if (numeric == Math.floor(numeric) && !Double.isInfinite(numeric) && Math.abs(numeric) <= Long.MAX_VALUE) {
                return new dvLong((long) numeric);
            }
            return new dvDouble(numeric);
        }
        if (isIdentifierStart(next)) {
            String literal = parseName();
            switch (literal) {
                case "true":
                    return new dvBool(true);
                case "false":
                    return new dvBool(false);
                case "inf":
                    return new dvDouble(Double.POSITIVE_INFINITY);
                case "nan":
                    return new dvDouble(Double.NaN);
                case "null":
                    return dvNull.INSTANCE;
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
            return new dvString(parseString(next, true));
        }
        throw new ParsingException("Unexpected character '" + next + "'");
    }

    private dvValue parseObject() {
        expect('{');
        dvObject object = new dvObject(new LinkedHashMap<>());
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

    private dvValue parse() {
        char next = peek();
        if (next == '{') {
            return parseObject();
        } else if (next == '[') {
            return parseList();
        }
        throw new ParsingException("'{' or '[' expected");
    }

    public static dvValue parse(String source) {
        JsonParser parser = new JsonParser(source);
        return parser.parse();
    }
}
