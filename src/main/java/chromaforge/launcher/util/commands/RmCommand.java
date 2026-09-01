package chromaforge.launcher.util.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.Iterator;
import chromaforge.launcher.debug.Logger;

public class RmCommand extends Command {
    private static Logger logger = Logger.getLogger("rm-command");

    public RmCommand() {
        this.keyword = "rm";
        this.args = "<version>";
        this.help = "removes the specified engine version";
    }

    @Override
    public void execute(String[] args) {
        String version = nextArg(args);
        logger.info("Removing '" + version + "' ...");

        Path installDir = Path.of("cores/chromaforge-v" + version);
        if (!Files.exists(installDir)) {
            throw new RuntimeException("Version " + version + " is not installed");
        }
        try (Stream<Path> walk = Files.walk(installDir)) {
            Iterator<Path> it = walk.sorted(Comparator.reverseOrder()).iterator();
            while (it.hasNext()) {
                Path p = it.next();
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete " + p, e);
                }
            }
            logger.info("Remover version " + version);
        } catch (IOException e) {
            throw new RuntimeException("Failed to traverse directory " + installDir, e);
        }
    }
}
