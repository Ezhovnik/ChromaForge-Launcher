package chromaforge.launcher.run;

import java.nio.file.Path;
import java.util.List;

public interface EngineRunner {
    void run(Path coreDir, List<String> args);
}
