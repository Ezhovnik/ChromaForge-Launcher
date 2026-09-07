package chromaforge.launcher.coders.toml;

import java.lang.StringBuilder;
import java.util.Map;

import chromaforge.launcher.data.dv.dvValue;
import chromaforge.launcher.data.dv.dvArray;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvString;

public class TomlWriter {
    private static void toString(StringBuilder sb, dvValue value) {
        if (value instanceof dvObject) {
            objectToString(sb, (dvObject) value);
        } else if (value instanceof dvArray) {
            arrayToString(sb, (dvArray) value);
        } else if (value instanceof dvString) {
            sb.append('"');
            sb.append(value.toString());
            sb.append('"');
        } else {
            sb.append(value.toString());
        }
    }

    private static void arrayToString(StringBuilder sb, dvArray array) {
        sb.append('[');
        int index = 0;
        for (dvValue value : array.items()) {
            if (index > 0) {
                sb.append(", ");
            }
            toString(sb, value);
            index++;
        }
        sb.append(']');
    }

    private static void objectToString(StringBuilder sb, dvObject object) {
        sb.append('{');
        int index = 0;
        for (Map.Entry<String, dvValue> entry : object.entries().entrySet()) {
            if (index > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey());
            sb.append(" = ");
            toString(sb, object);
            index++;
        }
        sb.append('}');
    }

    public static String stringify(dvObject root, String name) {
        StringBuilder sb = new StringBuilder("");
        if (!name.isEmpty()) {
            sb.append('[');
            sb.append(name);
            sb.append("]\n");
        }

        for (Map.Entry<String, dvValue> entry : root.entries().entrySet()) {
            String key = entry.getKey();
            dvValue value = entry.getValue();
            if (!((value instanceof dvObject) && (value instanceof dvArray))) {
                sb.append(key);
                sb.append(" = ");
                if (value instanceof dvString) {
                    sb.append('"');
                    sb.append(value.toString());
                    sb.append('"');
                } else {
                    sb.append(value.toString());
                }
                sb.append('\n');
            }
        }

        for (Map.Entry<String, dvValue> entry : root.entries().entrySet()) {
            String key = entry.getKey();
            dvValue value = entry.getValue();
            if (value instanceof dvObject) {
                sb.append('\n');
                sb.append(stringify((dvObject) value, name.isEmpty() ? key : name + "." + key));
            } else if (value instanceof dvArray) {
                sb.append(name.isEmpty() ? key : name + "." + key);
                sb.append(" = [");
                dvArray array = (dvArray) value;
                for (int i = 0; i < array.items().size(); ++i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    toString(sb, array.items().get(i));
                }
                sb.append("]");
            }
        }

        return sb.toString();
    }
}
