package chromaforge.launcher.run;

import java.nio.file.Path;
import java.util.List;

public interface EngineRunner {
    int run(Path coreDir, List<String> args);
}
