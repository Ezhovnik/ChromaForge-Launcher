package chromaforge.launcher.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record InstanceInfo (String name, CoreVersion coreVersion, String createdAt) {
    public InstanceInfo(String name, String coreVersion) {
        this(
            name,
            CoreVersion.parse(coreVersion),
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    public InstanceInfo(String name, CoreVersion coreVersion) {
        this(
            name,
            coreVersion,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
