package chromaforge.launcher.io;

import chromaforge.launcher.data.dv.dvLong;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvValue;

public interface RegistryFormat {
    static void write(dvObject root, long version) {
        root.entries().put("format_version", new dvLong(version));
    }

    static void check(dvObject root, long version) {
        dvValue v = root.entries().get("format_version");
        if (v instanceof dvLong l && l.value() > version) {
            throw new RuntimeException("Data file was created by a newer version of the launcher");
        }
    }
}
