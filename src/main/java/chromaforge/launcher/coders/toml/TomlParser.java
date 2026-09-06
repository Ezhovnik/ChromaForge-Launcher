package chromaforge.launcher.coders.toml;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import chromaforge.launcher.coders.BasicParser;
import chromaforge.launcher.data.dv.dvDouble;
import chromaforge.launcher.data.dv.dvLong;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvArray;
import chromaforge.launcher.data.dv.dvBool;
import chromaforge.launcher.data.dv.dvValue;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.data.dv.dvNull;

public class TomlParser extends BasicParser {
    private dvObject root;

    public TomlParser(String source) {
        super(source);
        this.hashComment = true;
        this.root = new dvObject(new LinkedHashMap<>());
    }

    private String parseMultilineString() {
        pos += 2;

        StringBuilder sb = new StringBuilder("");
        while (hasNext()) {
            char c = source.charAt(pos);
            if (c == '"' && remain() >= 2 && source.charAt(pos + 1) == '"' && source.charAt(pos + 2) == '"') {
                pos += 3;
                return sb.toString();
            }
            if (c == '\\') {
                pos++;
                c = nextChar();
                if (c >= '0' && c <= '7') {
                    pos--;
                    sb.append(parseSimpleInt(8));
                    continue;
                }
                switch (c) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 'b': sb.append('\b'); break;
                    case 't': sb.append('\t'); break;
                    case 'f': sb.append('\f'); break;
                    case '\'': sb.append('\''); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case '\n': continue;
                    default:
                        throw new ParsingException("'\\" + c + "' is an illegal escape");
                }
                continue;
            }
            sb.append(c);
            pos++;
        }
        throw new ParsingException("Unexpected end");
    }

    private dvValue parseValue() {
        char c = peek();
        if (isDigit(c)) {
            int start = pos;
            double value = parseNumber(1);
            if (hasNext() && peekNoJump() == '-') {
                while(hasNext()) {
                    c = source.charAt(pos);
                    if (!isDigit(c) && c != ':' && c != '.' && c != '-' && c != 'T' && c != 'Z') {
                        break;
                    }
                    pos++;
                }
                return new dvString(source.substring(start, pos));
            }
            if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) <= Long.MAX_VALUE) {
                return new dvLong((long) value);
            }
            return new dvDouble(value);
        } else if (c == '+' || c == '-') {
            int sign = c == '-' ? -1 : 1;
            pos++;
            double numeric = parseNumber(sign);
            if (numeric == Math.floor(numeric) && !Double.isInfinite(numeric) && Math.abs(numeric) <= Long.MAX_VALUE) {
                return new dvLong((long) numeric);
            }
            return new dvDouble(numeric);
        } else if (isIdentifierStart(c)) {
            String keyword = parseName();
            if (keyword.equals("true") || keyword.equals("false")) {
                return new dvBool(keyword.equals("true"));
            } else if (keyword.equals("inf")) {
                return new dvDouble(Double.POSITIVE_INFINITY);
            } else if (keyword.equals("nan")) {
                return new dvDouble(Double.NaN);
            }
            throw new ParsingException("Unknown keyword: " + keyword);
        } else if (c == '"' || c == '\'') {
            pos++;
            if (remain() >= 2 && c == '"' && source.charAt(pos) == '"' && source.charAt(pos + 1) == '"') {
                return new dvString(parseMultilineString());
            }
            return new dvString(parseString(c, true));
        } else if (c == '[') {
            pos++;
            dvArray values = new dvArray(new ArrayList<>());
            while (peek() != ']') {
                values.items().add(parseValue());
                if (peek() != ']') {
                    expect(',');
                }
            }
            pos++;
            return values;
        } else if (c == '{') {
            pos++;
            dvObject table = new dvObject(new LinkedHashMap<>());
            while (peek() != '}') {
                String key = parseName();
                expect('=');
                table.entries().put(key, parseValue());
                if (peek() != '}') {
                    expect(',');
                }
            }
            pos++;
            return table;
        }
        throw new ParsingException("Feature is not supported");
    }

    private static class LValue {
        final dvObject parent;
        final String key;

        LValue(dvObject parent, String key) {
            this.parent = parent;
            this.key = key;
        }

        dvValue get() {
            return parent.entries().get(key);
        }

        void set(dvValue value) {
            parent.entries().put(key, value);
        }
    }

    private LValue parseLValue(dvObject root) {
        dvObject current = root;
        String name;

        while (hasNext()) {
            char c = peek();
            if (c == '\'' || c == '"') {
                pos++;
                name = parseString(c, true);
            } else {
                name = parseName();
            }

            dvValue child = current.entries().get(name);
            if (child == null || child == dvNull.INSTANCE) {
                child = new dvObject(new LinkedHashMap<>());
                current.entries().put(name, child);
            }

            if (peek() != '.') {
                return new LValue(current, name);
            }
            pos++;

            if (child instanceof dvObject obj) {
                current = obj;
            } else {
                throw new ParsingException("Expected table");
            }
        }

        throw new ParsingException("Unexpected end of lvalue");
    }

    private void readSection(dvObject map, dvObject root) {
        while (hasNext()) {
            skipWhitespace(true);
            if (!hasNext()) {
                break;
            }
            char c = nextChar();
            if (c == '[') {
                if (hasNext() && peek() == '[') {
                    pos++;
                    LValue lvalue = parseLValue(root);
                    dvValue val = lvalue.get();
                    if (val == null || val == dvNull.INSTANCE) {
                        val = new dvArray(new ArrayList<>());
                        lvalue.set(val);
                    } else if (!(val instanceof dvArray)) {
                        throw new ParsingException("Target is not an array");
                    }
                    expect(']');
                    expect(']');
                    dvObject section = new dvObject(new LinkedHashMap<>());
                    readSection(section, root);
                    ((dvArray) val).items().add(section);
                    return;
                }
                LValue lvalue = parseLValue(root);
                dvValue val = lvalue.get();
                if (val == null || val == dvNull.INSTANCE) {
                    val = new dvObject(new LinkedHashMap<>());
                    lvalue.set(val);
                } else if (!(val instanceof dvObject)) {
                    throw new ParsingException("Target is not a table");
                }
                expect(']');
                readSection((dvObject) val, root);
                return;
            }
            pos--;
            LValue lvalue = parseLValue(map);
            expect('=');
            lvalue.set(parseValue());
            expectNewLine();
        }
    }

    private dvValue read() {
        skipWhitespace(true);
        if (!hasNext()) {
            return root;
        }
        readSection(root, root);
        return root;
    }

    static public dvValue parse(String source) {
        TomlParser parser = new TomlParser(source);
        return parser.read();
    }
}
